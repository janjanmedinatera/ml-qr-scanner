package medina.juanantonio.mlqrscanner.library.data

import android.content.Context
import android.content.Intent
import android.net.Uri

open class QRHandler(private val context: Context) {

    open fun handleResult(value: Any, qrType: QRType) {

        when (qrType) {
            QRType.URL -> {
                val browserIntent =
                    Intent(Intent.ACTION_VIEW, Uri.parse(value.toString()))
                context.startActivity(browserIntent)
            }
            else -> return
        }
    }
}