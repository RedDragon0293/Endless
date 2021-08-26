package cn.asone.endless.ui.font

import cn.asone.endless.utils.StringUtils
import cn.asone.endless.utils.mc
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.resources.IResourceManager
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import java.awt.Font

class GameFontRenderer(font: Font, companionStyle: Boolean = false) : FontRenderer(
    mc.gameSettings,
    ResourceLocation("textures/font/ascii.png"), mc.textureManager, false
) {

    private var defaultFont = AWTFontRenderer(font)
    private var boldFont: AWTFontRenderer? = null
    private var italicFont: AWTFontRenderer? = null
    private var boldItalicFont: AWTFontRenderer? = null

    val height: Int
        get() = defaultFont.height

    val size: Int
        get() = defaultFont.font.size

    init {
        FONT_HEIGHT = height
        if (companionStyle) {
            boldFont = AWTFontRenderer(font.deriveFont(Font.BOLD))
            italicFont = AWTFontRenderer(font.deriveFont(Font.ITALIC))
            boldItalicFont = AWTFontRenderer(font.deriveFont(Font.BOLD or Font.ITALIC))
        }
    }

    fun drawString(s: String, x: Float, y: Float, color: Int) = drawString(s, x, y, color, false)

    override fun drawStringWithShadow(text: String, x: Float, y: Float, color: Int) =
        drawString(text, x, y, color, true)

    override fun drawCenteredString(s: String, x: Float, y: Float, color: Int, shadow: Boolean) =
        drawString(s, x - getStringWidth(s) / 2F, y, color, shadow)

    fun drawCenteredString(s: String, x: Float, y: Float, color: Int) =
        drawText(s, x - getStringWidth(s) / 2F, y, color, false)

    override fun drawString(text: String, x: Float, y: Float, color: Int, shadow: Boolean): Int {
        var newColor = color
        var newtext = text
        if (this.bidiFlag)
            newtext = this.bidiReorder(text)

        if (shadow) {
            newColor = (newColor and 16579836) /*FC FC FC*/ shr 2 or newColor and -16777216 /*FF 00 00 00*/
            drawText(newtext, x + 1F, y + 1F, newColor, true)
        }
        newColor = color

        if (newColor and -67108864 /*FC 00 00 00*/ == 0) {
            newColor = newColor or -16777216 /*FF 00 00 00*/
        }

        return drawText(newtext, x, y, newColor, false)
    }

    private fun drawText(rawText: String?, x: Float, y: Float, colorHex: Int, ignoreColor: Boolean): Int {
        if (rawText == null)
            return 0
        if (rawText.isNullOrEmpty())
            return x.toInt()

        GlStateManager.translate(x - 1.5, y + 0.5, 0.0)
        GlStateManager.enableAlpha()
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO)
        GlStateManager.enableTexture2D()

        var hexColor = colorHex
        if (hexColor and -67108864 /*FC 00 00 00*/ == 0)
            hexColor = hexColor or -16777216 /*FF 00 00 00*/

        val alpha: Int = (hexColor shr 24 and 0xff)

        if (rawText.contains("§")) {
            val parts = rawText.split("§")

            var currentFont = defaultFont

            var width = 0.0

            // Color code states
            var obfuscated = false
            var bold = false
            var italic = false
            var strikeThrough = false
            var underline = false

            parts.forEachIndexed { index, part ->
                if (part.isEmpty())
                    return@forEachIndexed

                if (index == 0) {
                    currentFont.drawString(part, width, 0.0, hexColor)
                    width += currentFont.getStringWidth(part)
                } else {
                    val words = part.substring(1)
                    val type = part[0]

                    when (getColorIndex(type)) {
                        in 0..15 -> {
                            if (!ignoreColor) {
                                hexColor = getColorCode(type) or (alpha shl 24)
                            }

                            bold = false
                            italic = false
                            obfuscated = false
                            underline = false
                            strikeThrough = false
                        }
                        16 -> obfuscated = true
                        17 -> bold = true
                        18 -> strikeThrough = true
                        19 -> underline = true
                        20 -> italic = true
                        21 -> {
                            hexColor = colorHex
                            if (hexColor and -67108864 == 0)
                                hexColor = hexColor or -16777216

                            bold = false
                            italic = false
                            obfuscated = false
                            underline = false
                            strikeThrough = false
                        }
                    }

                    currentFont = if (bold && italic)
                        boldItalicFont ?: defaultFont
                    else if (bold)
                        boldFont ?: defaultFont
                    else if (italic)
                        italicFont ?: defaultFont
                    else
                        defaultFont

                    currentFont.drawString(
                        if (obfuscated) StringUtils.randomObfuscatedText(words) else words,
                        width,
                        0.0,
                        hexColor
                    )

                    if (strikeThrough) {
                        drawLine(
                            width + 1,
                            currentFont.height / 2 - 1.0,
                            (width + currentFont.getStringWidth(words)) + 1,
                            currentFont.height / 2 - 1.0,
                            FONT_HEIGHT / 16F,
                        )
                    }

                    if (underline) {
                        drawLine(
                            width + 1,
                            currentFont.height - 1.0,
                            (width + currentFont.getStringWidth(words)) + 1,
                            currentFont.height - 1.0,
                            FONT_HEIGHT / 16F,
                        )
                    }

                    width += currentFont.getStringWidth(words)
                }
            }
        } else
            defaultFont.drawString(rawText, 0.0, 0.0, hexColor)

        GlStateManager.disableBlend()
        GlStateManager.translate(-(x - 1.5), -(y + 0.5), 0.0)
        GlStateManager.color(1f, 1f, 1f, 1f)

        return (x + getStringWidth(rawText)).toInt()
    }

    //override fun getColorCode(charCode: Char) = mc.fontRendererObj.getColorCode(charCode)
    //ColorUtils.hexColors[getColorIndex(charCode)] ColorUtils.hexColors[colorIndex] or (alpha shl 24)

    override fun getStringWidth(text: String): Int {

        return if (text.contains("§")) {
            val parts = text.split("§")

            var currentFont = defaultFont
            var width = 0
            var bold = false
            var italic = false

            parts.forEachIndexed { index, part ->
                if (part.isEmpty())
                    return@forEachIndexed

                if (index == 0) {
                    width += currentFont.getStringWidth(part)
                } else {
                    val words = part.substring(1)
                    val type = part[0]
                    val colorIndex = getColorIndex(type)
                    when {
                        colorIndex < 16 -> {
                            bold = false
                            italic = false
                        }
                        colorIndex == 17 -> bold = true
                        colorIndex == 20 -> italic = true
                        colorIndex == 21 -> {
                            bold = false
                            italic = false
                        }
                    }

                    currentFont = if (bold && italic)
                        boldItalicFont ?: defaultFont
                    else if (bold)
                        boldFont ?: defaultFont
                    else if (italic)
                        italicFont ?: defaultFont
                    else
                        defaultFont

                    width += currentFont.getStringWidth(words)
                }
            }

            width
        } else
            defaultFont.getStringWidth(text)
    }

    private fun drawLine(x: Double, y: Double, x1: Double, y1: Double, width: Float) {
        GlStateManager.disableTexture2D()
        GL11.glLineWidth(width)
        GL11.glBegin(GL11.GL_LINES)
        GL11.glVertex2d(x, y)
        GL11.glVertex2d(x1, y1)
        GL11.glEnd()
        GlStateManager.enableTexture2D()
    }

    override fun getCharWidth(character: Char) = getStringWidth(character.toString())

    override fun onResourceManagerReload(resourceManager: IResourceManager) {}

    override fun bindTexture(resourceLocation: ResourceLocation?) {}

    companion object {
        @JvmStatic
        fun getColorIndex(type: Char): Int {
            return when (type) {
                in '0'..'9' -> type - '0'
                in 'a'..'f' -> type - 'a' + 10
                in 'k'..'o' -> type - 'k' + 16
                'r' -> 21
                else -> -1
            }
        }
    }
}