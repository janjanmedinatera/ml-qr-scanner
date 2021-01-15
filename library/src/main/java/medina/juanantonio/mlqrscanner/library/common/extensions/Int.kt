package medina.juanantonio.mlqrscanner.library.common.extensions

import medina.juanantonio.mlqrscanner.library.data.QRFormat
import medina.juanantonio.mlqrscanner.library.data.QRType

fun Int.getQRFormat() = when (this) {
    0 -> QRFormat.ALL_FORMATS
    1 -> QRFormat.CODE_128
    2 -> QRFormat.CODE_39
    4 -> QRFormat.CODE_93
    8 -> QRFormat.CODABAR
    16 -> QRFormat.DATA_MATRIX
    32 -> QRFormat.EAN_13
    64 -> QRFormat.EAN_8
    128 -> QRFormat.ITF
    256 -> QRFormat.QR_CODE
    512 -> QRFormat.UPC_A
    1024 -> QRFormat.UPC_E
    2048 -> QRFormat.PDF417
    4096 -> QRFormat.AZTEC
    else -> QRFormat.UNKNOWN
}

fun Int.getQRType() = when (this) {
    1 -> QRType.CONTACT_INFO
    2 -> QRType.EMAIL
    3 -> QRType.ISBN
    4 -> QRType.PHONE
    5 -> QRType.PRODUCT
    6 -> QRType.SMS
    7 -> QRType.TEXT
    8 -> QRType.URL
    9 -> QRType.WIFI
    10 -> QRType.GEO
    11 -> QRType.CALENDAR_EVENT
    12 -> QRType.DRIVER_LICENSE
    else -> QRType.UNKNOWN
}