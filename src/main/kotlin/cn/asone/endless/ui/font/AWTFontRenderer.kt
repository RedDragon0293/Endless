package cn.asone.endless.ui.font

import cn.asone.endless.Endless
import cn.asone.endless.utils.mc
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.opengl.GL11
import java.awt.*
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class AWTFontRenderer(val font: Font) {
    val scale = 0.5F
    private val chars: Array<FontChar?> = Array(65535) { return@Array null }

    private val fontHeight: Int
    private val fontMetrics: FontMetrics
    private val cacheDir = File(mc.mcDataDir, ".cache/${Endless.CLIENT_NAME}/fonts/${getFontDirName()}")

    val height: Int
        get() = ((fontHeight - 8) * scale).toInt()


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
        for (ascii in 32 until 126) {
            loadChar(ascii.toChar())
        }
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
        GlStateManager.pushMatrix()
        GL11.glTranslated(x, y - 3, 0.0)
        GL11.glScalef(scale, scale, scale)
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

    private fun getFontDirName() =
        (
                font.fontName.replace(" ", "_")
                        + (
                        if (font.isBold) {
                            "-bold"
                        } else if (font.isItalic) {
                            "-italic"
                        } else {
                            ""
                        }
                        )
                        + "-${font.size}"
                )

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

        return (width * scale).toInt()
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
    private fun getCharCacheFile(char: Char): File {
        if (!cacheDir.exists())
            cacheDir.mkdirs()
        return File(cacheDir, "${charInfo(char)}.png")
    }

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

        if (char.code == 32)
            return fontImage

        for (m in 0 until fontImage.height) {
            for (n in 0 until fontImage.width) {
                if (fontImage.getRGB(n, m) != 0)
                    return fontImage
            }
        }
        val imageStream = AWTFontRenderer::class.java.getResourceAsStream(
            String.format(
                "/assets/minecraft/textures/font/unicode_page_%02x.png",
                char.code / 256
            )
        )
        if (imageStream == null) {
            println(
                "Failed to load mc font texture! Char: $char; Code: ${char.code}; Page: ${
                    String.format(
                        "%02x",
                        char.code / 256
                    )
                }"
            )
            return fontImage
        }
        val image = ImageIO.read(imageStream)
        val j: Int = mc.fonts.glyphWidth[char.code].toInt() ushr 4
        val k: Int = mc.fonts.glyphWidth[char.code].toInt() and 15
        val f1 = k + 1

        /**
         * 图片中所在列
         */
        val xPos: Int = (char.code % 16 * 16) + j

        /**
         * 图片中所在行
         */
        val yPos: Int = ((char.code and 0xFF) / 16 * 16)

        /**
         * 图片宽度
         */
        val width = f1 - j
        val height =
        if (fontHeight < 16)
            16
        else
            fontHeight
        val startY = (height - 16) / 2
        val charImage = BufferedImage(width + 10, height, BufferedImage.TYPE_INT_ARGB)
        for (m in 0 until width) {
            for (n in 0..15) {
                charImage.setRGB(m + 4, startY + n, image.getRGB(m + xPos, n + yPos))
            }
        }
        return charImage
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

    private fun getFontChar(charCode: Int): FontChar {
        return chars[charCode] ?: loadChar(charCode.toChar())
    }
}