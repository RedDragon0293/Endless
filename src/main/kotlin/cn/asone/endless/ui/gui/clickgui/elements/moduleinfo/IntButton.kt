package cn.asone.endless.ui.gui.clickgui.elements.moduleinfo

import cn.asone.endless.ui.font.CFontRenderer
import cn.asone.endless.ui.font.Fonts
import cn.asone.endless.value.IntValue
import java.awt.Color

class IntButton(override val value: IntValue, isSub: Boolean) : AbstractValueButton(value, isSub) {
    private val valueFont = CFontRenderer(Fonts.getAssetsFont("Roboto-Light.ttf", 18), true, true)
    override fun drawBox() {
        super.drawBox()

    }

    override fun drawText() {
        super.drawText()
        valueFont.drawString(
            value.get().toString(),
            x + (if (isSub) 212 else 232) - 7 - valueFont.getStringWidth(value.get().toString()),
            y + 7,
            Color.black.rgb
        )
    }

    override fun mouseDragged(mouseX: Int, mouseY: Int, mouseButton: Int, duration: Long) {

    }
}