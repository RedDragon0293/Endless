package cn.asone.endless.ui.gui.clickgui.elements.moduleslist

import cn.asone.endless.ui.font.CFontRenderer
import cn.asone.endless.ui.font.Fonts
import java.awt.Color

abstract class AbstractButton(val name: String, var state: Boolean) {
    var x = 0F
    var y = 0F
    protected val font = CFontRenderer(Fonts.getAssetsFont("Roboto-Light.ttf", 23), true, true)

    fun updateX(x: Float) {
        this.x = x
    }

    fun updateY(y: Float) {
        this.y = y
    }

    fun drawBox() {

    }

    fun drawText() {
        font.drawString(name, x + 2, y, Color.black.rgb)
    }

    abstract fun mouseClicked(mouseX: Int, mouseY: Int)
}