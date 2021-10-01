package cn.asone.endless.ui.font

import cn.asone.endless.utils.StringUtils
import cn.asone.endless.utils.extensions.mc
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.resources.IResourceManager
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import java.awt.Font

class GameFontRenderer(font: Font, companionStyle: Boolean = false) : FontRenderer(
    mc.gameSettings,
    ResourceLocation("textures/font/ascii.png"), mc.textureManager, false
) {
    private var defaultFont = RedFontRenderer(font)
    private var boldFont: RedFontRenderer? = null
    private var italicFont: RedFontRenderer? = null
    private var boldItalicFont: RedFontRenderer? = null

    init {
        FONT_HEIGHT = defaultFont.fontHeight
        if (companionStyle) {
            boldFont = RedFontRenderer(font.deriveFont(Font.BOLD))
            italicFont = RedFontRenderer(font.deriveFont(Font.ITALIC))
            boldItalicFont = RedFontRenderer(font.deriveFont(Font.BOLD or Font.ITALIC))
        }
    }

    fun drawString(s: String, x: Float, y: Float, color: Int) = drawString(s, x, y, color, false)

    override fun drawStringWithShadow(text: String, x: Float, y: Float, color: Int) =
        drawString(text, x, y, color, true)

    override fun drawCenteredString(s: String, x: Float, y: Float, color: Int, shadow: Boolean) =
        drawString(s, x - getStringWidth(s) / 2F, y, color, shadow)

    fun drawCenteredString(s: String, x: Float, y: Float, color: Int) =
        drawTextInternal(s, x - getStringWidth(s) / 2F, y, color, false)

    override fun drawString(text: String, x: Float, y: Float, color: Int, shadow: Boolean): Int {
        var newColor = color
        var newText = text
        if (this.bidiFlag)
            newText = this.bidiReorder(text)

        if (shadow) {
            newColor = (newColor and 16579836) /*FC FC FC*/ shr 2 or newColor and -16777216 /*FF 00 00 00*/
            drawTextInternal(newText, x + 0.5F, y + 0.5F, newColor, true)
        }
        newColor = color

        if (newColor and -67108864 /*FC 00 00 00*/ == 0) {
            newColor = newColor or -16777216 /*FF 00 00 00*/
        }

        return drawTextInternal(newText, x, y, newColor, false)
    }

    private fun drawTextInternal(rawText: String?, x: Float, y: Float, color: Int, ignoreColor: Boolean): Int {
        if (rawText == null)
            return 0
        if (rawText.isNullOrEmpty())
            return x.toInt()

        //GlStateManager.translate(x - 1.5, y + 0.5, 0.0)
        GlStateManager.enableAlpha()
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO)
        GlStateManager.enableTexture2D()

        var hexColor = color
        if (hexColor and -67108864 /*FC 00 00 00*/ == 0)
            hexColor = hexColor or -16777216 /*FF 00 00 00*/

        val alpha: Int = (hexColor shr 24 and 0xff)

        // Color code states
        var obfuscated = false
        var bold = false
        var italic = false
        var strikeThrough = false
        var underline = false
        var width = 0.0
        var currentFont: RedFontRenderer = defaultFont

        var i = 0
        while (i < rawText.length) {
            var char = rawText[i]
            if (char.code == 167 && i + 1 < rawText.length) {
                val type = rawText[i + 1]
                when (getColorIndex(type)) {
                    in 0..15 -> {
                        if (!ignoreColor) {
                            hexColor = getColorCode(type) or (alpha shl 24)
                        }
                        bold = false
                        italic = false
                        currentFont = defaultFont
                        obfuscated = false
                        underline = false
                        strikeThrough = false
                    }
                    16 -> obfuscated = true
                    17 -> {
                        bold = true
                        currentFont = if (italic)
                            boldItalicFont ?: defaultFont
                        else
                            boldFont ?: defaultFont
                    }
                    18 -> strikeThrough = true
                    19 -> underline = true
                    20 -> {
                        italic = true
                        currentFont = if (bold)
                            boldItalicFont ?: defaultFont
                        else
                            italicFont ?: defaultFont
                    }
                    21 -> {
                        hexColor = color
                        if (hexColor and -67108864 == 0)
                            hexColor = hexColor or -16777216

                        bold = false
                        italic = false
                        currentFont = defaultFont
                        obfuscated = false
                        underline = false
                        strikeThrough = false
                    }
                }
                i++
            } else {
                if (obfuscated) {
                    val currentWidth = getCharWidth(char)
                    var randomChar: Char
                    val index = StringUtils.asciiCharacters.indexOf(char)
                    var total = 0
                    if (index != -1) {
                        do {
                            randomChar = StringUtils.asciiCharacters.random()
                            total++
                        } while (currentWidth != getCharWidth(randomChar)
                            /**
                             * 若多次尝试后仍无法匹配则放弃混淆防止死循环
                             */
                            && total <= StringUtils.asciiCharacters.length * 2
                        )
                        char = randomChar
                    }
                }
                val singleWidth = currentFont.drawChar(char, x + width, y.toDouble(), hexColor)
                if (strikeThrough) {
                    drawLine(
                        x + width,
                        y + currentFont.fontHeight / 2 - 2.0,
                        singleWidth.toFloat(),
                        FONT_HEIGHT / 16F,
                    )
                }

                if (underline) {
                    drawLine(
                        x + width,
                        y + currentFont.fontHeight - 2.0,
                        singleWidth.toFloat(),
                        FONT_HEIGHT / 16F,
                    )
                }
                width += singleWidth
            }
            i++
        }
        GlStateManager.disableBlend()
        GlStateManager.color(1F, 1F, 1F, 1F)

        return (x + width).toInt()
    }

    //override fun getColorCode(charCode: Char) = mc.fontRendererObj.getColorCode(charCode)
    //ColorUtils.hexColors[getColorIndex(charCode)] ColorUtils.hexColors[colorIndex] or (alpha shl 24)

    override fun getStringWidth(text: String): Int {
        // Color code states
        var obfuscated = false
        var bold = false
        var italic = false
        var width = 0.0
        var currentFont: RedFontRenderer = defaultFont

        var i = 0
        while (i < text.length) {
            var char = text[i]
            if (char.code == 167 && i + 1 < text.length) {
                val type = text[i + 1]
                when (getColorIndex(type)) {
                    in 0..15 -> {
                        bold = false
                        italic = false
                        currentFont = defaultFont
                        obfuscated = false
                    }
                    16 -> obfuscated = true
                    17 -> {
                        bold = true
                        currentFont = if (italic)
                            boldItalicFont ?: defaultFont
                        else
                            boldFont ?: defaultFont
                    }
                    20 -> {
                        italic = true
                        currentFont = if (bold)
                            boldItalicFont ?: defaultFont
                        else
                            italicFont ?: defaultFont
                    }
                    21 -> {
                        bold = false
                        italic = false
                        currentFont = defaultFont
                        obfuscated = false
                    }
                }
                i++
            } else {
                if (obfuscated) {
                    val currentWidth = super.getCharWidth(char)
                    var randomChar: Char
                    val index = StringUtils.asciiCharacters.indexOf(char)
                    if (index != -1) {
                        do {
                            randomChar = StringUtils.asciiCharacters.random()
                        } while (currentWidth != super.getCharWidth(randomChar))
                        char = randomChar
                    }
                }
                val singleWidth = currentFont.getCharWidth(char)
                width += singleWidth
            }
            i++
        }

        return width.toInt()
    }

    private fun drawLine(x: Double, y: Double, width: Float, height: Float) {
        val tessellator = Tessellator.getInstance()
        val worldrenderer = tessellator.worldRenderer
        GlStateManager.disableTexture2D()
        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
        worldrenderer.pos(x, y, 0.0).endVertex()
        worldrenderer.pos(x + width, y, 0.0).endVertex()
        worldrenderer.pos(x + width, y - height, 0.0).endVertex()
        worldrenderer.pos(x, y - height, 0.0).endVertex()
        tessellator.draw()
        GlStateManager.enableTexture2D()
        /*GlStateManager.disableTexture2D()
        GL11.glLineWidth(width)
        GL11.glBegin(GL11.GL_LINES)
        GL11.glVertex2d(x, y)
        GL11.glVertex2d(x1, y1)
        GL11.glEnd()
        GlStateManager.enableTexture2D()*/
    }

    fun refresh() {
        this.defaultFont.refresh()
        this.boldFont?.refresh()
        this.italicFont?.refresh()
        this.boldItalicFont?.refresh()
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