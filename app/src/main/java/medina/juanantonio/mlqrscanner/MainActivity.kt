package medina.juanantonio.mlqrscanner

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import medina.juanantonio.mlqrscanner.library.Scanner
import medina.juanantonio.mlqrscanner.library.common.Constants.BarcodeIntent.BARCODE_FORMAT_RESULT
import medina.juanantonio.mlqrscanner.library.common.Constants.BarcodeIntent.BARCODE_RAW_RESULT
import medina.juanantonio.mlqrscanner.library.common.Constants.BarcodeIntent.BARCODE_TYPE_RESULT
import medina.juanantonio.mlqrscanner.library.common.extensions.getQRType
import medina.juanantonio.mlqrscanner.library.data.QRHandler

class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 111
        private const val REQUEST_QR = 222
    }

    private val isCameraPermissionGranted: Boolean
        get() = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.button_qr_scanner).setOnClickListener {
            if (isCameraPermissionGranted)
                Scanner.startQR(this, REQUEST_QR)
            else
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_CAMERA_PERMISSION
                )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode != REQUEST_CAMERA_PERMISSION ||
            grantResults.isEmpty() ||
            grantResults.first() != PackageManager.PERMISSION_GRANTED
        ) return

        Scanner.startQR(this, REQUEST_QR)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_QR) {
            val rawResult = data?.getStringExtra(BARCODE_RAW_RESULT) ?: ""
            val resultFormat = data?.getIntExtra(BARCODE_FORMAT_RESULT, 0) ?: 0
            val resultType = data?.getIntExtra(BARCODE_TYPE_RESULT, 0) ?: 0

            val resultTextView = findViewById<TextView>(R.id.textview_result)
            resultTextView.text = rawResult

            QRHandler(this).handleResult(rawResult, resultType.getQRType())
        }
    }
}