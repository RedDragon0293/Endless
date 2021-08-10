package cn.asone.endless.ui.font

import cn.asone.endless.Endless
import cn.asone.endless.config.ConfigManager
import cn.asone.endless.utils.ClientUtils
import cn.asone.endless.utils.mc
import cn.asone.endless.value.AbstractValue
import cn.asone.endless.value.BoolValue
import cn.asone.endless.value.ValueRegister
import java.awt.Font
import java.io.File
import java.io.FileInputStream
import java.util.*

object Fonts : ValueRegister {
    private val fontsDir: File = File(ConfigManager.rootDir, "fonts").apply {
        if (!exists())
            if (!mkdir())
                throw Exception("无法创建字体文件夹!")
    }
    val forceCustomFont = object : BoolValue("ForceCustomFont", true) {
        override fun changeValue(newValue: Boolean) {
            super.changeValue(newValue)
            if (newValue)
                mc.fontRendererObj = mcRegular18
            else
                mc.fontRendererObj = mc.fonts
        }
    }

    @JvmField
    val condensedLight16: GameFontRenderer

    @JvmField
    val light18: GameFontRenderer

    @JvmField
    val mcRegular18: GameFontRenderer

    @JvmField
    val regular20: GameFontRenderer

    @JvmField
    val medium22: GameFontRenderer

    @JvmField
    val light24: GameFontRenderer

    @JvmField
    val regular24: GameFontRenderer

    @JvmField
    val regular26: GameFontRenderer

    @JvmField
    val light30: GameFontRenderer

    @JvmField
    val regular38: GameFontRenderer

    @JvmField
    val medium44: GameFontRenderer
    init {
        ClientUtils.logger.info("正在初始化默认字体...")
        val var0 = System.currentTimeMillis()
        condensedLight16 = GameFontRenderer(getAssetsFont("HarmonyOS_Sans_Condensed_Light.ttf", 16))
        light18 = GameFontRenderer(getAssetsFont("HarmonyOS_Sans_Light.ttf", 18))
        mcRegular18 = GameFontRenderer(getAssetsFont("HarmonyOS_Sans_Regular.ttf", 18), true)
        regular20 = GameFontRenderer(getAssetsFont("HarmonyOS_Sans_Regular.ttf", 20))
        medium22 = GameFontRenderer(getAssetsFont("HarmonyOS_Sans_Medium.ttf", 22))
        light24 = GameFontRenderer(getAssetsFont("HarmonyOS_Sans_Light.ttf", 24))
        regular24 = GameFontRenderer(getAssetsFont("HarmonyOS_Sans_Regular.ttf", 24))
        regular26 = GameFontRenderer(getAssetsFont("HarmonyOS_Sans_Regular.ttf", 26))
        light30 = GameFontRenderer(getAssetsFont("HarmonyOS_Sans_Light.ttf", 30))
        regular38 = GameFontRenderer(getAssetsFont("HarmonyOS_Sans_Regular.ttf", 38))
        medium44 = GameFontRenderer(getAssetsFont("HarmonyOS_Sans_Medium.ttf", 44))

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

    override val values: ArrayList<AbstractValue<*>> = arrayListOf(forceCustomFont)
}