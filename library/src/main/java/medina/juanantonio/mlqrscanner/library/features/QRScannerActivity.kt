package medina.juanantonio.mlqrscanner.library.features

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import medina.juanantonio.mlqrscanner.library.R
import medina.juanantonio.mlqrscanner.library.common.extensions.sendBarcodeResult
import medina.juanantonio.mlqrscanner.library.common.extensions.startImageChooser
import medina.juanantonio.mlqrscanner.library.data.BarcodeListener
import medina.juanantonio.mlqrscanner.library.data.QRScannerProcessor
import medina.juanantonio.mlqrscanner.library.ml.utils.CameraSource
import medina.juanantonio.mlqrscanner.library.ml.utils.CameraSourcePreview
import medina.juanantonio.mlqrscanner.library.ml.utils.GraphicOverlay

class QRScannerActivity : AppCompatActivity(), BarcodeListener {

    companion object {
        private const val REQUEST_IMAGE = 111
    }

    private lateinit var qrScannerProcessor: QRScannerProcessor
    private lateinit var previewView: CameraSourcePreview
    private lateinit var graphicOverlay: GraphicOverlay

    private var cameraSource: CameraSource? = null
    private var lensFacing = CameraSource.CAMERA_FACING_BACK

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_scanner)

        val imageViewGallery = findViewById<ImageView>(R.id.imageview_gallery)
        imageViewGallery.setOnClickListener {
            startImageChooser(REQUEST_IMAGE)
        }

        val imageViewChangeCamera = findViewById<ImageView>(R.id.imageview_change_camera)
        imageViewChangeCamera.setOnClickListener {
            changeCamera()
        }

        qrScannerProcessor = QRScannerProcessor(this)
        previewView = findViewById(R.id.preview_view)
        graphicOverlay = findViewById(R.id.graphic_overlay)

        createCameraSource()
    }

    override fun onBarcodeResult(result: List<Barcode>) {
        sendBarcodeResult(result.first())
    }

    override fun onResume() {
        super.onResume()
        startCameraSource()
    }

    override fun onPause() {
        super.onPause()
        previewView.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraSource?.release()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK || data == null) return

        if (requestCode == REQUEST_IMAGE) {
            processQRBitmap(data.data)
        }
    }

    private fun createCameraSource() {
        if (cameraSource == null) {
            cameraSource = CameraSource(this, graphicOverlay)
        }

        cameraSource?.setFacing(lensFacing)
        cameraSource?.setMachineLearningFrameProcessor(
            qrScannerProcessor
        )
    }

    /**
     * Starts or restarts the camera source, if it exists. If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private fun startCameraSource() {
        if (cameraSource != null) {
            previewView.start(cameraSource, graphicOverlay)
        }
    }

    private fun processQRBitmap(uri: Uri?) {
        val image = InputImage.fromFilePath(this, uri ?: return)
        BarcodeScanning.getClient().process(image).addOnSuccessListener {
            if (it.isNotEmpty()) sendBarcodeResult(it.first())
        }
    }

    private fun changeCamera() {
        if (cameraSource != null) {
            val newLensFacing =
                when (lensFacing) {
                    CameraSource.CAMERA_FACING_FRONT -> CameraSource.CAMERA_FACING_BACK
                    CameraSource.CAMERA_FACING_BACK -> CameraSource.CAMERA_FACING_FRONT
                    else -> return
                }
            lensFacing = newLensFacing
            cameraSource?.setFacing(newLensFacing)
        }

        previewView.stop()
        startCameraSource()
    }
}