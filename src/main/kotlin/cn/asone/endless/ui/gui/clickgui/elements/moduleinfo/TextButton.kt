package cn.asone.endless.ui.gui.clickgui.elements.moduleinfo

import cn.asone.endless.value.TextValue
import java.awt.Color

class TextButton(override val value: TextValue, isSub: Boolean) : AbstractValueButton(value, isSub) {
    override fun drawText(mouseX: Int, mouseY: Int) {
        super.drawText(mouseX, mouseY)
        valueFont.drawString(
            value.get(),
            x + (if (isSub) 212 else 232) - 5 - valueFont.getStringWidth(value.get()),
            y + 7,
            Color.black.rgb
        )
    }
}