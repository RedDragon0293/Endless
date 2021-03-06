package cn.asone.endless.ui.gui.clickgui.elements.moduleinfo

import cn.asone.endless.ui.font.Fonts
import cn.asone.endless.ui.gui.clickgui.GuiClickGUI
import cn.asone.endless.utils.ColorUtils
import cn.asone.endless.utils.RenderUtils
import cn.asone.endless.option.IntOption

class IntButton(override val value: IntOption, isSub: Boolean) : AbstractOptionButton(value, isSub) {
    override fun drawBox(mouseX: Int, mouseY: Int) {
        super.drawBox(mouseX, mouseY)
        if (isHovering(mouseX, mouseY)) {
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
                (value.get().toFloat() / value.range.last) * 100,
                4F,
                2F,
                ColorUtils.getColorInt(0, 111, 255)
            )
            RenderUtils.drawAntiAliasingCircle(
                x + (if (isSub) 212 else 232) - 5 - (1 - (value.get().toFloat() / value.range.last)) * 100 - 4,
                y + 10,
                4F,
                ColorUtils.getColorInt(0, 111, 255)
            )
        }
    }

    override fun drawText(mouseX: Int, mouseY: Int) {
        super.drawText(mouseX, mouseY)
        Fonts.condensedLight18.drawString(
            value.get().toString(),
            if (isHovering(mouseX, mouseY))
                x + (if (isSub) 212 else 232) - 110 - 4 - Fonts.condensedLight18.getStringWidth(value.get().toString())
            else
                x + (if (isSub) 212 else 232) - 5 - Fonts.condensedLight18.getStringWidth(value.get().toString()),
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
                (mouseX - (x + (if (isSub) 212 else 232) - 5 - 100 - 4)) / 100F * value.range.last
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
                (mouseX - (x + (if (isSub) 212 else 232) - 5 - 100 - 4)) / 100F * value.range.last
            )
        }
    }
}