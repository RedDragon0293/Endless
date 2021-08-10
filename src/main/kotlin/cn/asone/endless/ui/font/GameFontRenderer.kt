package cn.asone.endless.ui.font

import cn.asone.endless.utils.mc
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.resources.IResourceManager
import net.minecraft.util.ChatAllowedCharacters
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import java.awt.Font
import java.util.*

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

    fun drawString(s: String, x: Float, y: Float, color: Int) = drawText(s, x, y, color, false)

    override fun drawStringWithShadow(text: String, x: Float, y: Float, color: Int) =
        drawString(text, x, y, color, true)

    override fun drawCenteredString(s: String, x: Float, y: Float, color: Int, shadow: Boolean) =
        drawString(s, x - getStringWidth(s) / 2F, y, color, shadow)

    fun drawCenteredString(s: String, x: Float, y: Float, color: Int) =
        drawText(s, x - getStringWidth(s) / 2F, y, color, false)

    override fun drawString(text: String, x: Float, y: Float, color: Int, shadow: Boolean): Int {
        if (shadow) {
            var newColor = color
            if (newColor and -67108864 /*FC 00 00 00*/ == 0) {
                newColor = newColor or -16777216 /*FF 00 00 00*/
            }

            if (shadow) {
                newColor = (newColor and 16579836) /*FC FC FC*/ shr 2 or newColor and -16777216 /*FF 00 00 00*/
            }
            drawText(text, x + 1F, y + 1F, newColor, true)
        }
        return drawText(text, x, y, color, false)
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
            var randomCase = false
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
                            randomCase = false
                            underline = false
                            strikeThrough = false
                        }
                        16 -> randomCase = true
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
                            randomCase = false
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
                        if (randomCase) randomMagicText(words) else words,
                        width,
                        0.0,
                        hexColor
                    )

                    if (strikeThrough)
                        drawLine(
                            width + 1,
                            currentFont.height / 2 - 1.0,
                            (width + currentFont.getStringWidth(words)) + 1,
                            currentFont.height / 2 - 1.0,
                            FONT_HEIGHT / 16F
                        )

                    if (underline)
                        drawLine(
                            width + 1,
                            currentFont.height - 1.0,
                            (width + currentFont.getStringWidth(words)) + 1,
                            currentFont.height - 1.0,
                            FONT_HEIGHT / 16F
                        )

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
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glLineWidth(width)
        GL11.glBegin(GL11.GL_LINES)
        GL11.glVertex2d(x, y)
        GL11.glVertex2d(x1, y1)
        GL11.glEnd()
        GL11.glEnable(GL11.GL_TEXTURE_2D)
    }

    private fun randomMagicText(text: String): String {
        val stringBuilder = StringBuilder()
        val allowedCharacters =
            "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000"

        for (c in text.toCharArray()) {
            if (ChatAllowedCharacters.isAllowedCharacter(c)) {
                val index = Random().nextInt(allowedCharacters.length)
                stringBuilder.append(allowedCharacters.toCharArray()[index])
            }
        }

        return stringBuilder.toString()
    }

    override fun getCharWidth(character: Char) = getStringWidth(character.toString())

    override fun onResourceManagerReload(resourceManager: IResourceManager) {}

    override fun bindTexture(location: ResourceLocation?) {}

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