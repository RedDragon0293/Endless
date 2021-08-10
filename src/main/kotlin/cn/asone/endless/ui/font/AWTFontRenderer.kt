package cn.asone.endless.ui.font

import cn.asone.endless.Endless
import cn.asone.endless.utils.mc
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.opengl.GL11
import java.awt.*
import java.awt.image.BufferedImage
import java.io.File
import java.util.*
import javax.imageio.ImageIO

class AWTFontRenderer(val font: Font) {
    private val chars = HashMap<Int, FontChar>()

    private val fontHeight: Int
    private val fontMetrics: FontMetrics
    private val cacheDir = File(mc.mcDataDir, ".cache/${Endless.CLIENT_NAME}/fonts/${getFontDirName()}").apply {
        if (!exists())
            mkdirs()
    }

    val height: Int
        get() = (fontHeight - 8) / 2

    init {
        val graphics = BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB).graphics as Graphics2D
        putHints(graphics)
        graphics.font = font
        fontMetrics = graphics.fontMetrics
        fontHeight = if (fontMetrics.height <= 0) {
            font.size
        } else {
            fontMetrics.height + 3
        }

        //loadChar(" ")
        //prepareCharImages('0', '9')
        //prepareCharImages('a', 'z')
        //prepareCharImages('A', 'Z')
        prepareCharImages(32, 126)
    }

    /**
     * Allows you to draw a string with the target font
     *
     * @param text  to render
     * @param x     location for target position
     * @param y     location for target position
     * @param color of the text
     */
    fun drawString(text: String, x: Double, y: Double, color: Int) {
        val scale = 0.5
        GlStateManager.pushMatrix()
        GL11.glTranslated(x, y - 3, 0.0)
        GL11.glScaled(scale, scale, scale)
        //GL11.glTranslated(x * 2F, y * 2.0 - 2.0, 0.0)

        val red = (color shr 16 and 0xff) / 255F
        val green = (color shr 8 and 0xff) / 255F
        val blue = (color and 0xff) / 255F
        val alpha = (color shr 24 and 0xff) / 255F

        GlStateManager.color(red, green, blue, alpha)
        /*
        var isLastUTF16 = false
        var highSurrogate = '\u0000'
        for (char in text.toCharArray()) {
            if (char in '\ud800'..'\udfff') {
                if (isLastUTF16) {
                    val utf16Char = "$highSurrogate$char"
                    val singleWidth = drawChar(utf16Char)
                    GL11.glTranslatef(singleWidth - 8f, 0f, 0f)
                } else {
                    highSurrogate = char
                }
                isLastUTF16 = !isLastUTF16
            } else {
                val singleWidth = drawChar(char.toString())
                GL11.glTranslatef(singleWidth - 8f, 0f, 0f)
                isLastUTF16 = false
            }
        }*/
        for (char in text.toCharArray()) {
            val singleWidth = drawChar(char)
            GL11.glTranslatef(singleWidth - 8f, 0f, 0f)
        }
        GlStateManager.popMatrix()
    }

    private fun getFontDirName() = font.fontName.replace(" ", "_").lowercase(Locale.getDefault()) +
            (if (font.isBold) {
                "-bold"
            } else {
                ""
            }) +
            "${
                if (font.isItalic) {
                    "-italic"
                } else {
                    ""
                }
            }-" +
            "${font.size}"

    /**
     * Draw char from texture to display
     *
     * @param char target font char to render
     */
    private fun drawChar(char: Char): Int {
        val fontChar = getFontChar(char.code)

        //mc.textureManager.bindTexture(fontChar.resourceLocation)
        GlStateManager.bindTexture(fontChar.tex.glTextureId)
        Gui.drawModalRectWithCustomSizedTexture(
            0,
            0,
            0f,
            0f,
            fontChar.width,
            fontHeight,
            fontChar.width.toFloat(),
            fontHeight.toFloat()
        )
        return fontChar.width
    }

    /**
     * Calculate the string width of a text
     *
     * @param text for width calculation
     * @return the width of the text
     */
    fun getStringWidth(text: String): Int {
        var width = 0
/*
        var isLastUTF16 = false
        var highSurrogate = '\u0000'
        for (char in text.toCharArray()) {
            if (char in '\ud800'..'\udfff') {
                if (isLastUTF16) {
                    val utf16Char = "$highSurrogate$char"

                    width += (chars[utf16Char] ?: loadChar(utf16Char)).width - 8
                } else {
                    highSurrogate = char
                }
                isLastUTF16 = !isLastUTF16
            } else {
                width += (chars["$char"] ?: loadChar("$char")).width - 8
                isLastUTF16 = false
            }
        }
        */
        for (char in text.toCharArray()) {
            width += (getFontChar(char.code)).width - 8
        }

        return width / 2
    }

    private fun putHints(graphics: Graphics2D) {
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON)
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    }

    private fun charInfo(char: Char): String {
        /*val charArr = char.toCharArray()
        if (char.length == 1) {
            return "char-${charArr[0].code}"
        } else if (char.length == 2 && charArr[0] in '\ud800'..'\udfff' && charArr[1] in '\ud800'..'\udfff') {
            val first = (charArr[0].code - 0xd800) * 0x400
            val second = charArr[1].code - 0xdc00
            return "char-${first + second + 0x10000}"
        }*/
        return "char-${char.code}"
        //throw IllegalStateException("The char $char not UTF-8 or UTF-16")
    }

    /**
     * 获取char对应的缓存文件
     */
    private fun getCharCacheFile(char: Char) = File(cacheDir, "${charInfo(char)}.png")

    /**
     * @return 通过Char获取的ResourceLocation
     */
    /*private fun getResourceLocationByChar(char: String) =
        ResourceLocation("endless/font/${getFontDirName()}/${charInfo(char)}")*/

    /**
     * 渲染字符图片
     */
    private fun renderCharImage(char: Char): BufferedImage {
        var charWidth = fontMetrics.stringWidth(char.toString()) + 8
        if (charWidth <= 0)
            charWidth = 7

        val fontImage = BufferedImage(charWidth, fontHeight, BufferedImage.TYPE_INT_ARGB)
        val graphics = fontImage.graphics as Graphics2D
        putHints(graphics)
        graphics.font = font
        graphics.color = Color.WHITE
        graphics.drawString(char.toString(), 3, 1 + fontMetrics.ascent)

        return fontImage
    }

    /**
     * @param char 字符
     * 初始化单个字符图片
     */
    private fun loadChar(char: Char): FontChar {
        val result: FontChar
        if (Fonts.cacheFont.get()) {
            val charImageFile = getCharCacheFile(char)
            if (charImageFile.exists()) {
                result = FontChar(char, ImageIO.read(charImageFile))
            } else {
                val image = renderCharImage(char)
                result = FontChar(char, image)
                saveFontCharToCache(char, image)
            }
        } else
            result = FontChar(char, renderCharImage(char))
        chars[char.code] = result
        return result
    }

    /**
     * 将渲染好的FontChar保存至缓存
     * @return 传入的FontChar
     */
    private fun saveFontCharToCache(char: Char, image: BufferedImage) {
        ImageIO.write(image, "png", getCharCacheFile(char))
    }

    /**
     * @param start 开始字符
     * @param stop 结束字符
     * 如果需要初始化单个直接loadChar(char)就行
     * 预初始化字符图片
     */
    private fun prepareCharImages(start: Int, stop: Int) {
        val startAscii = start.coerceAtMost(stop)
        val stopAscii = stop.coerceAtLeast(start)

        for (ascii in startAscii until stopAscii) {
            loadChar(ascii.toChar())
        }
    }

    private fun getFontChar(charCode: Int): FontChar {
        return chars[charCode] ?: loadChar(charCode.toChar())
    }
}