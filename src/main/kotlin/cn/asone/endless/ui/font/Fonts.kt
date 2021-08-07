package cn.asone.endless.ui.font

import cn.asone.endless.Endless
import cn.asone.endless.config.ConfigManager
import cn.asone.endless.utils.ClientUtils
import java.awt.Font
import java.io.File
import java.io.FileInputStream
import java.util.*

object Fonts {
    private val fontsDir: File = File(ConfigManager.rootDir, "fonts").apply {
        if (!exists())
            if (!mkdir())
                throw Exception("无法创建字体文件夹!")
    }
    @JvmField
    val thin16: CFontRenderer
    @JvmField
    val light18: CFontRenderer
    @JvmField
    val regular20: CFontRenderer
    @JvmField
    val medium21: CFontRenderer
    @JvmField
    val light24: CFontRenderer
    @JvmField
    val regular24: CFontRenderer
    @JvmField
    val regular26: CFontRenderer
    @JvmField
    val light30: CFontRenderer
    @JvmField
    val regular38: CFontRenderer
    @JvmField
    val medium44: CFontRenderer

    init {
        ClientUtils.logger.info("正在初始化默认字体...")
        val var0 = System.currentTimeMillis()
        thin16 = CFontRenderer(getAssetsFont("Roboto-Thin.ttf", 16), true, false)
        light18 = CFontRenderer(getAssetsFont("Roboto-Light.ttf", 18), true, true)
        regular20 = CFontRenderer(getAssetsFont("Roboto-Regular.ttf", 20), true, true)
        medium21 = CFontRenderer(getAssetsFont("Roboto-Medium.ttf", 21), true, true)
        light24 = CFontRenderer(getAssetsFont("Roboto-Light.ttf", 24), true, true)
        regular24 = CFontRenderer(getAssetsFont("Roboto-Regular.ttf", 24), true, true)
        regular26 = CFontRenderer(getAssetsFont("Roboto-Regular.ttf", 26), true, true)
        light30 = CFontRenderer(getAssetsFont("Roboto-Light.ttf", 30), true, true)
        regular38 = CFontRenderer(getAssetsFont("Roboto-Regular.ttf", 38), true, true)
        medium44 = CFontRenderer(getAssetsFont("Roboto-Medium.ttf", 44), true, true)
        ClientUtils.logger.info("成功初始化默认字体, 用时${(System.currentTimeMillis() - var0) / 1000F}秒.")
    }

    @JvmStatic
    fun getFont(name: String, size: Int): Font {
        return try {
            val file = File(fontsDir, name)
            if (!file.exists()) {
                ClientUtils.logger.error("找不到字体 $name. 使用默认字体.")
                return Font("default", Font.PLAIN, size)
            }
            val inputStream = FileInputStream(file)
            val awtClientFont = Font.createFont(Font.TRUETYPE_FONT, inputStream).deriveFont(Font.PLAIN, size.toFloat())
            inputStream.close()
            awtClientFont
        } catch (e: Exception) {
            e.printStackTrace()
            Font("default", Font.PLAIN, size)
        }
    }

    @JvmStatic
    fun getAssetsFont(name: String, size: Int): Font {
        return try {
            val inputStream = Fonts::class.java.getResourceAsStream(
                "/assets/minecraft/${Endless.CLIENT_NAME.lowercase(Locale.getDefault())}/fonts/$name"
            )
            if (inputStream == null) {
                ClientUtils.logger.error("无法加载字体 $name. 使用默认字体.")
                return Font("default", Font.PLAIN, size)
            }

            val awtClientFont = Font.createFont(Font.TRUETYPE_FONT, inputStream).deriveFont(Font.PLAIN, size.toFloat())
            inputStream.close()
            awtClientFont
        } catch (e: Exception) {
            e.printStackTrace()
            Font("default", Font.PLAIN, size)
        }
    }
}