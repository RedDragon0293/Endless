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
    val font20 = CFontRenderer(getAssetsFont("Roboto-Regular.ttf", 20), true, true)

    @JvmField
    val font24 = CFontRenderer(getAssetsFont("Roboto-Regular.ttf", 24), true, true)

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