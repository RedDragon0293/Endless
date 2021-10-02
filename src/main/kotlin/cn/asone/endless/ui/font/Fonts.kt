package cn.asone.endless.ui.font

import cn.asone.endless.Endless
import cn.asone.endless.config.ConfigManager
import cn.asone.endless.utils.ClientUtils
import cn.asone.endless.utils.extensions.mc
import cn.asone.endless.utils.io.FileUtils
import cn.asone.endless.utils.io.HttpUtils
import cn.asone.endless.utils.security.SecurityUtils
import cn.asone.endless.value.AbstractValue
import cn.asone.endless.value.BoolValue
import cn.asone.endless.value.ValueRegister
import net.minecraft.client.resources.IResourceManager
import net.minecraft.client.resources.IResourceManagerReloadListener
import org.apache.commons.codec.digest.DigestUtils
import java.awt.Font
import java.io.File
import java.io.FileInputStream

object Fonts : ValueRegister, IResourceManagerReloadListener {
    private val fontsDir: File = File(ConfigManager.rootDir, "fonts").apply {
        if (!exists())
            if (!mkdir())
                throw Exception("无法创建字体文件夹!")
    }

    @JvmField
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
    val cacheFont = BoolValue("CacheFont", true)

    @JvmStatic
    lateinit var condensedLight16: GameFontRenderer

    @JvmStatic
    lateinit var condensedLight18: GameFontRenderer

    @JvmStatic
    lateinit var mcRegular18: GameFontRenderer

    @JvmStatic
    lateinit var regular20: GameFontRenderer

    @JvmStatic
    lateinit var medium22: GameFontRenderer

    @JvmStatic
    lateinit var light24: GameFontRenderer

    @JvmStatic
    lateinit var regular24: GameFontRenderer

    @JvmStatic
    lateinit var regular26: GameFontRenderer

    @JvmStatic
    lateinit var light30: GameFontRenderer

    @JvmStatic
    lateinit var regular38: GameFontRenderer

    @JvmStatic
    lateinit var medium44: GameFontRenderer

    @JvmStatic
    fun downloadFonts() {
        runCatching {
            val zipFile = File(fontsDir, "HarmonyOS_Sans.zip")
            if (zipFile.exists()) {
                val digest = DigestUtils.getSha1Digest()
                digest.update(zipFile.readBytes())
                val result = SecurityUtils.toHashValue(digest.digest())
                if (!result.equals("5e078001b9d855872f2d6034a32d8f37e62b7f2f", true)) {
                    ClientUtils.logger.warn("字体文件已经损坏. 重新下载字体文件.")
                    HttpUtils.download(
                        "https://reddragon0293.coding.net/p/endless/d/EndlessCloud/git/raw/main/HarmonyOS_Sans.zip?download=true",
                        zipFile
                    )
                }
                ClientUtils.logger.info("正在解压字体文件...")
                FileUtils.extractZip(zipFile)
            } else {
                ClientUtils.logger.info("正在下载字体文件...")
                HttpUtils.download(
                    "https://reddragon0293.coding.net/p/endless/d/EndlessCloud/git/raw/main/HarmonyOS_Sans.zip?download=true",
                    zipFile
                )
                //https://gitee.com/reddragon0293/endless-cloud/raw/master/fonts/HarmonyOS_Sans.zip
                //https://cloud.liquidbounce.net/LiquidBounce/fonts/Roboto.zip
                //https://github.com/RedDragon0293/EndlessCloud/raw/main/HarmonyOS_Sans.zip
                ClientUtils.logger.info("正在解压字体文件...")
                FileUtils.extractZip(zipFile)
            }
        }.onFailure {
            ClientUtils.logger.error("下载字体文件失败！")
            it.printStackTrace()
        }
    }

    @JvmStatic
    fun loadFonts() {
        ClientUtils.logger.info("正在初始化默认字体...")
        val var0 = System.currentTimeMillis()
        condensedLight16 = GameFontRenderer(getFont("HarmonyOS_Sans_Condensed_Light.ttf", 16))
        condensedLight18 = GameFontRenderer(getFont("HarmonyOS_Sans_Condensed_Light.ttf", 18))
        mcRegular18 = GameFontRenderer(getFont("HarmonyOS_Sans_Regular.ttf", 18), true)
        //mcRegular18 = GameFontRenderer(getFont("Roboto-Regular.ttf", 18), true)
        regular20 = GameFontRenderer(getFont("HarmonyOS_Sans_Regular.ttf", 20))
        medium22 = GameFontRenderer(getFont("HarmonyOS_Sans_Medium.ttf", 22))
        light24 = GameFontRenderer(getFont("HarmonyOS_Sans_Light.ttf", 24))
        regular24 = GameFontRenderer(getFont("HarmonyOS_Sans_Regular.ttf", 24))
        regular26 = GameFontRenderer(getFont("HarmonyOS_Sans_Regular.ttf", 26))
        light30 = GameFontRenderer(getFont("HarmonyOS_Sans_Light.ttf", 30))
        regular38 = GameFontRenderer(getFont("HarmonyOS_Sans_Regular.ttf", 38))
        medium44 = GameFontRenderer(getFont("HarmonyOS_Sans_Medium.ttf", 44))

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
                "/assets/minecraft/${Endless.CLIENT_NAME.lowercase()}/fonts/$name"
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

    override fun onResourceManagerReload(resourceManager: IResourceManager?) {
        condensedLight16.refresh()
        condensedLight18.refresh()
        mcRegular18.refresh()
        regular20.refresh()
        medium22.refresh()
        light24.refresh()
        regular24.refresh()
        regular26.refresh()
        light30.refresh()
        regular38.refresh()
        medium44.refresh()
    }

    override val values: ArrayList<AbstractValue<*>> = arrayListOf(
        forceCustomFont,
        cacheFont
    )
}