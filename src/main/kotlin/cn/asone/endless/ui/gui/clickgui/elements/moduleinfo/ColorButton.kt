package cn.asone.endless.ui.gui.clickgui.elements.moduleinfo

import cn.asone.endless.option.IntOption

class ColorButton(override val value: IntOption, isSub: Boolean) : AbstractOptionButton(value, isSub) {
    override fun drawBox(mouseX: Int, mouseY: Int) {
        super.drawBox(mouseX, mouseY)

    }
}