package cn.asone.endless.ui.gui.clickgui.elements.moduleinfo

import cn.asone.endless.utils.RenderUtils
import cn.asone.endless.value.IntValue
import java.awt.Color

class IntButton(override val value: IntValue, isSub: Boolean) : AbstractValueButton(value, isSub) {
    override fun drawBox(mouseX: Int, mouseY: Int) {
        super.drawBox(mouseX, mouseY)
        if (isHovering(mouseX, mouseY)) {
            /**
             * White background in order to hover other texts
             */
            /*RenderUtils.drawRect(
                x + (if (isSub) 212 else 232) - 110 - 4 - valueFont.getStringWidth(value.get().toString()) - 2,
                y + 5,
                valueFont.getStringWidth(value.get().toString()) + 4F,
                10F,
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
                Color(140, 140, 140).rgb
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
                Color(0, 111, 255).rgb
            )
            RenderUtils.drawAntiAliasingCircle(
                x + (if (isSub) 212 else 232) - 5 - (1 - (value.get().toFloat() / value.range.last)) * 100 - 4,
                y + 10,
                4F,
                Color(0, 111, 255).rgb
            )
        }
    }

    override fun drawText(mouseX: Int, mouseY: Int) {
        super.drawText(mouseX, mouseY)
        valueFont.drawString(
            value.get().toString(),
            if (isHovering(mouseX, mouseY))
                x + (if (isSub) 212 else 232) - 110 - 4 - valueFont.getStringWidth(value.get().toString())
            else
                x + (if (isSub) 212 else 232) - 5 - valueFont.getStringWidth(value.get().toString()),
            y + 7,
            Color.black.rgb
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