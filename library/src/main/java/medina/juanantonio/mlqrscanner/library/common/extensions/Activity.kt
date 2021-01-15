package medina.juanantonio.mlqrscanner.library.common.extensions

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.vision.barcode.Barcode
import medina.juanantonio.mlqrscanner.library.common.Constants.BarcodeIntent.BARCODE_FORMAT_RESULT
import medina.juanantonio.mlqrscanner.library.common.Constants.BarcodeIntent.BARCODE_RAW_RESULT
import medina.juanantonio.mlqrscanner.library.common.Constants.BarcodeIntent.BARCODE_TYPE_RESULT

fun AppCompatActivity.sendBarcodeResult(barcode: Barcode) {
    val intent = intent.apply {
        putExtra(BARCODE_RAW_RESULT, barcode.rawValue)
        putExtra(BARCODE_FORMAT_RESULT, barcode.format)
        putExtra(BARCODE_TYPE_RESULT, barcode.valueType)
    }
    setResult(Activity.RESULT_OK, intent)
    finish()
}

fun AppCompatActivity.startImageChooser(code: Int) {
    val intent = Intent()
    intent.type = "image/*"
    intent.action = Intent.ACTION_GET_CONTENT
    startActivityForResult(
        Intent.createChooser(intent, "Select Picture"),
        code
    )
}