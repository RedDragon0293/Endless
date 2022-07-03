package cn.asone.endless.ui.gui.clickgui.elements.moduleinfo

import cn.asone.endless.ui.font.Fonts
import cn.asone.endless.ui.gui.clickgui.GuiClickGUI
import cn.asone.endless.utils.ColorUtils
import cn.asone.endless.utils.RenderUtils
import cn.asone.endless.option.FloatOption

class FloatButton(override val value: FloatOption, isSub: Boolean) : AbstractOptionButton(value, isSub) {
    private val valueString
        get() = String.format("%.2f", value.get())

    override fun drawBox(mouseX: Int, mouseY: Int) {
        super.drawBox(mouseX, mouseY)
        if (isHovering(mouseX, mouseY)) {
            /**
             * White background in order to hover other texts
             */
            /*RenderUtils.drawRect(
                x + (if (isSub) 212 else 232) - 110 - 4 - valueFont.getStringWidth(valueString),
                y + 7,
                valueFont.getStringWidth(valueString).toFloat(),
                valueFont.height.toFloat(),
                Color.white.rgb
            )*/
            /**
             * Bar background
             */
            RenderUtils.drawAntiAliasingRoundedRect(
                x + (if (isSub) 212 else 232) - 5 - 100 - 4,
                y + 8,
                100F,
                4F,
                2F,
                ColorUtils.getColorInt(140, 140, 140)
            )
            /**
             * Value percent
             */
            RenderUtils.drawAntiAliasingRoundedRect(
                x + (if (isSub) 212 else 232) - 5 - 100 - 4,
                y + 8,
                (value.get() / value.range.endInclusive) * 100,
                4F,
                2F,
                ColorUtils.getColorInt(0, 111, 255)
            )
            RenderUtils.drawAntiAliasingCircle(
                x + (if (isSub) 212 else 232) - 5 - (1 - (value.get() / value.range.endInclusive)) * 100 - 4,
                y + 10,
                4F,
                ColorUtils.getColorInt(0, 111, 255)
            )
        }
    }

    override fun drawText(mouseX: Int, mouseY: Int) {
        super.drawText(mouseX, mouseY)
        Fonts.condensedLight18.drawString(
            valueString,
            if (isHovering(mouseX, mouseY))
                x + (if (isSub) 212 else 232) - 110 - 4 - Fonts.condensedLight18.getStringWidth(valueString)
            else
                x + (if (isSub) 212 else 232) - 5 - Fonts.condensedLight18.getStringWidth(valueString),
            y + 7,
            GuiClickGUI.textColor
        )
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        /**
         * Hovering progress bar
         */
        if (mouseX >= x + (if (isSub) 212 else 232) - 5 - 100 - 4
            && mouseX <= x + (if (isSub) 212 else 232) - 5 - 4
            && mouseY >= y + 6
            && mouseY <= y + 14
        ) {
            value.set(
                (mouseX - (x + (if (isSub) 212 else 232) - 5 - 100 - 4)) / 100F * value.range.endInclusive
            )
        }
    }

    override fun mouseDragged(mouseX: Int, mouseY: Int, mouseButton: Int, duration: Long) {
        /**
         * Hovering progress bar
         */
        if (mouseX >= x + (if (isSub) 212 else 232) - 5 - 100 - 4
            && mouseX <= x + (if (isSub) 212 else 232) - 5 - 4
            && mouseY >= y + 6
            && mouseY <= y + 14
        ) {
            value.set(
                (mouseX - (x + (if (isSub) 212 else 232) - 5 - 100 - 4)) / 100F * value.range.endInclusive
            )
        }
    }
}