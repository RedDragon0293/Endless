package cn.asone.endless.ui.gui.clickgui.elements.moduleslist

import cn.asone.endless.ui.font.Fonts
import cn.asone.endless.ui.gui.clickgui.GuiClickGUI
import cn.asone.endless.ui.gui.clickgui.elements.moduleinfo.AbstractValueButton
import cn.asone.endless.utils.RenderUtils
import cn.asone.endless.utils.mc
import cn.asone.endless.utils.playSound
import java.awt.Color

/**
 * 150 × 23 button
 */
abstract class AbstractButton(open val name: String) {
    companion object {
        var boxColor: Int = 0
        var buttonColor: Int = 0
    }

    protected var x = 0F
    protected var y = 0F
        get() = field + offset
    var offset = 0F
    open var state = false
    open val infoButtons: ArrayList<AbstractValueButton> = arrayListOf()
    val visible: Boolean
        get() = y < GuiClickGUI.windowYStart + GuiClickGUI.guiHeight - 7 && y + 23 > GuiClickGUI.windowYStart + 7

    fun updateX(x: Float) {
        this.x = x
    }

    fun updateY(y: Float) {
        this.y = y
    }

    open fun drawBox(mouseX: Int, mouseY: Int) {
        //Background
        RenderUtils.drawAntiAliasingRoundedRect(x, y, 150F, 23F, 5F, boxColor)
        //Button
        RenderUtils.drawAntiAliasingRoundedRect(
            x + 150 - 3 - 17,
            y + 6,
            17F,
            11F,
            5.5F,
            if (state) Color(0, 111, 250).rgb else Color(117, 117, 117).rgb
        )
        RenderUtils.drawAntiAliasingCircle(
            if (state) x + 150 - 5 - 4 else x + 150 - 14,
            y + 11.5F,
            4.5F,
            buttonColor
        )
    }

    open fun drawText(mouseX: Int, mouseY: Int) {
        Fonts.regular26.drawString(name, x + 4, y + 6, GuiClickGUI.textColor)
    }

    fun isHovering(mouseX: Int, mouseY: Int): Boolean =
        mouseX >= x && mouseX <= x + 150 && mouseY >= y && mouseY <= y + 23

    open fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        if (mouseButton == 0) {
            mc.soundHandler.playSound("gui.button.press", 1F)
            if (mouseX >= x + 150 - 3 - 17 && mouseX <= x + 150 - 3 && mouseY >= y + 6 && mouseY <= y + 17)
                this.state = !this.state
            else {
                GuiClickGUI.currentInfoButton = this
                if (infoButtons.isNotEmpty()) {
                    infoButtons.forEach {
                        it.updateX(GuiClickGUI.windowXStart + 231 + 6F)
                    }
                }
            }
        }
    }
}