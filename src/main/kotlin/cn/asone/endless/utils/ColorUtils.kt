package cn.asone.endless.utils

object ColorUtils {
    @JvmStatic
    fun getRed(color: Int) = color shr 16 and 0xFF

    @JvmStatic
    fun getGreen(color: Int) = color shr 8 and 0xFF

    @JvmStatic
    fun getBlue(color: Int) = color and 0xFF

    @JvmStatic
    fun getAlpha(color: Int) = color ushr 24

    @JvmStatic
    fun getColorInt(red: Int, green: Int, blue: Int, alpha: Int = 255): Int {
        return alpha shl 24 or (red shl 16) or (green shl 8) or blue
    }

    @JvmStatic
    fun multiply(color1: Int, color2: Int) = getColorInt(
        getRed(color1) * getRed(color2) / 255,
        getGreen(color1) * getGreen(color2) / 255,
        getBlue(color1) * getBlue(color2) / 255,
        getAlpha(color1) * getAlpha(color2) / 255
    )
}