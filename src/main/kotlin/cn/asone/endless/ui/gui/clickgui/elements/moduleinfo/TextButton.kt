package cn.asone.endless.ui.gui.clickgui.elements.moduleinfo

import cn.asone.endless.ui.font.Fonts
import cn.asone.endless.ui.gui.clickgui.GuiClickGUI
import cn.asone.endless.option.TextOption

class TextButton(override val value: TextOption, isSub: Boolean) : AbstractOptionButton(value, isSub) {
    override fun drawText(mouseX: Int, mouseY: Int) {
        super.drawText(mouseX, mouseY)
        Fonts.condensedLight18.drawString(
            value.get(),
            x + (if (isSub) 212 else 232) - 5 - Fonts.condensedLight18.getStringWidth(value.get()),
            y + 7,
            GuiClickGUI.textColor
        )
    }
}