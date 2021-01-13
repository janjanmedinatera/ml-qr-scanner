package medina.juanantonio.mlqrscanner.library.data

import android.content.Context
import com.google.mlkit.vision.barcode.Barcode
import medina.juanantonio.mlqrscanner.library.ml.barcode.BarcodeScannerProcessor
import medina.juanantonio.mlqrscanner.library.ml.utils.GraphicOverlay

class QRScannerProcessor(
    private val context: Context
) : BarcodeScannerProcessor(context, displayInfo = false) {

    private var barcodeListener: BarcodeListener? = null

    fun setBarcodeListener(barcodeListener: BarcodeListener) {
        this.barcodeListener = barcodeListener
    }

    override fun onSuccess(barcodes: List<Barcode>, graphicOverlay: GraphicOverlay) {
        super.onSuccess(barcodes, graphicOverlay)

        if (barcodes.isNotEmpty()) {
            barcodeListener?.onBarcodeResult(barcodes)

            if (barcodeListener == null && context is BarcodeListener) {
                context.onBarcodeResult(barcodes)
            }
        }
    }
}

interface BarcodeListener {
    fun onBarcodeResult(result: List<Barcode>)
}