package medina.juanantonio.mlqrscanner.library.data

import android.content.Context
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import medina.juanantonio.mlqrscanner.library.ml.VisionProcessorBase
import medina.juanantonio.mlqrscanner.library.ml.utils.GraphicOverlay

class QRScannerProcessor(
    private val context: Context
) : VisionProcessorBase<List<Barcode>>(context)  {

    private val barcodeScanner: BarcodeScanner = BarcodeScanning.getClient()
    private var barcodeListener: BarcodeListener? = null

    fun setBarcodeListener(barcodeListener: BarcodeListener) {
        this.barcodeListener = barcodeListener
    }

    override fun onSuccess(results: List<Barcode>, graphicOverlay: GraphicOverlay) {
        if (results.isNotEmpty()) {
            barcodeListener?.onBarcodeResult(results)

            if (barcodeListener == null && context is BarcodeListener) {
                context.onBarcodeResult(results)
            }
        }
    }

    override fun detectInImage(image: InputImage): Task<List<Barcode>> {
        return barcodeScanner.process(image)
    }

    override fun onFailure(e: Exception) {
    }

    override fun stop() {
        super.stop()
        barcodeScanner.close()
    }
}

interface BarcodeListener {
    fun onBarcodeResult(result: List<Barcode>)
}