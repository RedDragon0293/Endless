package cn.asone.endless.ui.gui.clickgui.elements.moduleslist

import cn.asone.endless.ui.font.CFontRenderer
import cn.asone.endless.ui.font.Fonts
import cn.asone.endless.ui.gui.clickgui.ClickGUI
import cn.asone.endless.utils.RenderUtils
import cn.asone.endless.utils.mc
import cn.asone.endless.utils.playSound
import java.awt.Color

abstract class AbstractButton(open val name: String) {
    var x = 0F
    var y = 0F
    var offset = 0F
    open var state = false
    val visible: Boolean
        get() = y < ClickGUI.windowYStart + ClickGUI.guiHeight - 7 && y + 23 > ClickGUI.windowYStart + 7
    protected val font = CFontRenderer(Fonts.getAssetsFont("Roboto-Light.ttf", 24), true, true)

    fun updateX(x: Float) {
        this.x = x
    }

    fun updateY(y: Float) {
        this.y = y
    }

    fun drawBox() {
        //Background
        RenderUtils.drawAntiAliasingRoundedRect(x, y + offset, 130F, 23F, 5F, Color(253, 253, 253).rgb)
        //Button
        RenderUtils.drawAntiAliasingRoundedRect(
            x + 130 - 3 - 17,
            y + offset + 6,
            17F,
            11F,
            5.5F,
            if (state) Color(0, 111, 250).rgb else Color(117, 117, 117).rgb
        )
        RenderUtils.drawAntiAliasingCircle(
            if (state) x + 130 - 5 - 4 else x + 130 - 14,
            y + offset + 11.5F,
            4.5F,
            Color.white.rgb
        )
    }

    fun drawText() {
        font.drawString(name, x + 4, y + 6 + offset, Color.black.rgb)
    }

    fun isHovering(mouseX: Int, mouseY: Int): Boolean = mouseX > x && mouseX < x + 130 && mouseY > y && mouseY < y + 23

    open fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        if (mouseButton == 0) {
            mc.soundHandler.playSound("gui.button.press", 1F)
            if (mouseX >= x + 130 - 3 - 17 && mouseX <= x + 130 - 3 && mouseY >= y + offset + 6 && mouseY <= y + offset + 17)
                this.state = !this.state
            else
                ClickGUI.currentInfoButton = this
        }
    }
}