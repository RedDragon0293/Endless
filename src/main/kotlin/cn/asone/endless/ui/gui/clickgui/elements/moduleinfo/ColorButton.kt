package cn.asone.endless.ui.gui.clickgui.elements.moduleinfo

import cn.asone.endless.value.IntValue

class ColorButton(override val value: IntValue, isSub: Boolean) : AbstractValueButton(value, isSub) {
    override fun drawBox(mouseX: Int, mouseY: Int) {
        super.drawBox(mouseX, mouseY)

    }
}