package medina.juanantonio.mlqrscanner.library

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.fragment.app.Fragment
import medina.juanantonio.mlqrscanner.library.features.QRScannerActivity
import medina.juanantonio.mlqrscanner.library.features.QRScannerXActivity

object Scanner {
    private val cameraActivity: Class<*>
        get() {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                QRScannerXActivity::class.java
            else
                QRScannerActivity::class.java
        }

    fun startQR(activity: Activity, code: Int) {
        val intent = Intent(activity, cameraActivity)
        activity.startActivityForResult(intent, code)
    }

    fun startQR(fragment: Fragment, code: Int) {
        val activity = fragment.requireActivity()
        val intent = Intent(activity, cameraActivity)
        activity.startActivityForResult(intent, code)
    }
}