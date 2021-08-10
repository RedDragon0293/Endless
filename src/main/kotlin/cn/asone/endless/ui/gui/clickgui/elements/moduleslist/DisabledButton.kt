package cn.asone.endless.ui.gui.clickgui.elements.moduleslist

import cn.asone.endless.ui.gui.clickgui.GuiClickGUI
import cn.asone.endless.utils.RenderUtils
import cn.asone.endless.utils.mc
import cn.asone.endless.utils.playSound

abstract class DisabledButton(name: String) : AbstractButton(name) {
    override fun drawBox(mouseX: Int, mouseY: Int) {
        RenderUtils.drawAntiAliasingRoundedRect(x, y, 150F, 23F, 5F, boxColor)
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        if (mouseButton == 0) {
            mc.soundHandler.playSound("gui.button.press", 1F)
            GuiClickGUI.currentInfoButton = this
            if (infoButtons.isNotEmpty()) {
                infoButtons.forEach {
                    it.updateX(GuiClickGUI.windowXStart + 231 + 6F)
                }
            }
        }
    }
}