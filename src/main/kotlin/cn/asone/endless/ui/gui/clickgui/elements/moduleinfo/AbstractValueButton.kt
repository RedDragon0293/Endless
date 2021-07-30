package cn.asone.endless.ui.gui.clickgui.elements.moduleinfo

import cn.asone.endless.ui.font.CFontRenderer
import cn.asone.endless.ui.font.Fonts
import cn.asone.endless.value.Value

abstract class AbstractValueButton(val value: Value<*>) {
    var x = 0F
    var y = 0F
    var offset = 0F
    protected val nameFont = CFontRenderer(Fonts.getAssetsFont("Roboto-Regular", 26), true, true)
    abstract fun drawText()
    fun drawBox() {
        //RenderUtils.drawBorder(x, y, ClickGUI.)
    }
}