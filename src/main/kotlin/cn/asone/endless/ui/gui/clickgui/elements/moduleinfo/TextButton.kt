package cn.asone.endless.ui.gui.clickgui.elements.moduleinfo

import cn.asone.endless.ui.font.Fonts
import cn.asone.endless.ui.gui.clickgui.GuiClickGUI
import cn.asone.endless.value.TextValue

class TextButton(override val value: TextValue, isSub: Boolean) : AbstractValueButton(value, isSub) {
    override fun drawText(mouseX: Int, mouseY: Int) {
        super.drawText(mouseX, mouseY)
        Fonts.light18.drawString(
            value.get(),
            x + (if (isSub) 212 else 232) - 5 - Fonts.light18.getStringWidth(value.get()),
            y + 7,
            GuiClickGUI.textColor
        )
    }
}