package medina.juanantonio.mlqrscanner.library.features

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import medina.juanantonio.mlqrscanner.library.R
import medina.juanantonio.mlqrscanner.library.common.extensions.sendBarcodeResult
import medina.juanantonio.mlqrscanner.library.common.extensions.startImageChooser
import medina.juanantonio.mlqrscanner.library.data.BarcodeListener
import medina.juanantonio.mlqrscanner.library.data.QRScannerProcessor
import medina.juanantonio.mlqrscanner.library.ml.utils.CameraXViewModel
import medina.juanantonio.mlqrscanner.library.ml.utils.GraphicOverlay
import medina.juanantonio.mlqrscanner.library.ml.utils.VisionImageProcessor

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class QRScannerXActivity : AppCompatActivity(), BarcodeListener {

    companion object {
        private const val REQUEST_IMAGE = 111
    }

    private lateinit var cameraSelector: CameraSelector
    private lateinit var previewView: PreviewView
    private lateinit var graphicOverlay: GraphicOverlay

    private var cameraProvider: ProcessCameraProvider? = null
    private var previewUseCase: Preview? = null
    private var analysisUseCase: ImageAnalysis? = null
    private var imageProcessor: VisionImageProcessor? = null
    private var needUpdateGraphicOverlayImageSourceInfo = false
    private var lensFacing = CameraSelector.LENS_FACING_BACK

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_scanner_x)

        cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
        previewView = findViewById(R.id.preview_view)
        graphicOverlay = findViewById(R.id.graphic_overlay)

        val imageViewGallery =
            findViewById<ImageView>(R.id.imageview_gallery)
        imageViewGallery.setOnClickListener {
            startImageChooser(REQUEST_IMAGE)
        }

        val imageViewChangeCamera =
            findViewById<ImageView>(R.id.imageview_change_camera)
        imageViewChangeCamera.setOnClickListener {
            changeCamera()
        }

        ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )
            .get(CameraXViewModel::class.java)
            .processCameraProvider
            .observe(
                this,
                { provider: ProcessCameraProvider? ->
                    cameraProvider = provider
                    bindAllCameraUseCases()
                }
            )
    }

    public override fun onResume() {
        super.onResume()
        bindAllCameraUseCases()
    }

    override fun onPause() {
        super.onPause()
        imageProcessor?.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        imageProcessor?.stop()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK || data == null) return

        if (requestCode == REQUEST_IMAGE) {
            processQRBitmap(data.data)
        }
    }

    override fun onBarcodeResult(result: List<Barcode>) {
        sendBarcodeResult(result.first())
    }

    private fun bindAllCameraUseCases() {
        cameraProvider?.run {
            unbindAll()
            bindPreviewUseCase()
            bindAnalysisUseCase()
        }
    }

    private fun bindPreviewUseCase() {
        previewUseCase?.run {
            cameraProvider?.unbind(previewUseCase)
        }

        val builder = Preview.Builder()
        previewUseCase = builder.build()
        previewUseCase?.setSurfaceProvider(previewView.surfaceProvider)
        cameraProvider?.bindToLifecycle(this, cameraSelector, previewUseCase)
    }

    private fun bindAnalysisUseCase() {
        if (cameraProvider == null) return
        if (analysisUseCase != null) cameraProvider?.unbind(analysisUseCase)
        if (imageProcessor != null) imageProcessor?.stop()

        imageProcessor = QRScannerProcessor(this)

        val builder = ImageAnalysis.Builder()
        analysisUseCase = builder.build()
        needUpdateGraphicOverlayImageSourceInfo = true

        analysisUseCase?.setAnalyzer(
            ContextCompat.getMainExecutor(this), { imageProxy ->
                if (needUpdateGraphicOverlayImageSourceInfo) {
                    val isImageFlipped =
                        lensFacing == CameraSelector.LENS_FACING_FRONT
                    val rotationDegrees =
                        imageProxy.imageInfo.rotationDegrees
                    if (rotationDegrees == 0 || rotationDegrees == 180) {
                        graphicOverlay.setImageSourceInfo(
                            imageProxy.width, imageProxy.height, isImageFlipped
                        )
                    } else {
                        graphicOverlay.setImageSourceInfo(
                            imageProxy.height, imageProxy.width, isImageFlipped
                        )
                    }
                    needUpdateGraphicOverlayImageSourceInfo = false
                }

                imageProcessor?.processImageProxy(imageProxy, graphicOverlay)
            }
        )

        cameraProvider?.bindToLifecycle(this, cameraSelector, analysisUseCase)
    }

    private fun processQRBitmap(uri: Uri?) {
        val image = InputImage.fromFilePath(this, uri ?: return)
        BarcodeScanning.getClient().process(image).addOnSuccessListener {
            if (it.isNotEmpty()) sendBarcodeResult(it.first())
        }
    }

    private fun changeCamera() {
        val newLensFacing =
            when (lensFacing) {
                CameraSelector.LENS_FACING_FRONT -> CameraSelector.LENS_FACING_BACK
                CameraSelector.LENS_FACING_BACK -> CameraSelector.LENS_FACING_FRONT
                else -> return
            }
        val newCameraSelector =
            CameraSelector.Builder().requireLensFacing(newLensFacing).build()

        if (cameraProvider?.hasCamera(newCameraSelector) == true) {
            lensFacing = newLensFacing
            cameraSelector = newCameraSelector
            bindAllCameraUseCases()
        }
    }
}
