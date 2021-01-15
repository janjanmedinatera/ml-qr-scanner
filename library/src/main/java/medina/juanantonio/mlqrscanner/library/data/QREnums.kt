package medina.juanantonio.mlqrscanner.library.data

enum class QRFormat {
    UNKNOWN,
    ALL_FORMATS,
    CODE_128,
    CODE_39,
    CODE_93,
    CODABAR,
    DATA_MATRIX,
    EAN_13,
    EAN_8,
    ITF,
    QR_CODE,
    UPC_A,
    UPC_E,
    PDF417,
    AZTEC,
}

enum class QRType {
    UNKNOWN,
    CONTACT_INFO,
    EMAIL,
    ISBN,
    PHONE,
    PRODUCT,
    SMS,
    TEXT,
    URL,
    WIFI,
    GEO,
    CALENDAR_EVENT,
    DRIVER_LICENSE,
}
