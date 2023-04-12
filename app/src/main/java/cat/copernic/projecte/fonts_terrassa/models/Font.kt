package cat.copernic.projecte.fonts_terrassa.models

data class Font(val id: String, val name: String, val lat: Double, val lon: Double, val info: String,
                val estat: Int, val type: Int, val adreca: String, val distance: Double) {
    var fontId: String? = null
    var fontName: String? = null
    var fontLat: Double? = null
    var fontLon: Double? = null
    var fontInfo: String? = null
    var fontEstat: Int? = null
    var fontType: Int? = null
    var fontAdreca: String? = null
    var fontDistance: Double? = null

    init {
        this.fontId = id
        this.fontName = name
        this.fontLat = lat
        this.fontLon = lon
        this.fontInfo = info
        this.fontEstat = estat
        this.fontType = type
        this.fontAdreca = adreca
        this.fontDistance = distance
    }

}