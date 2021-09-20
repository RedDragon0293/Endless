package cn.asone.endless.ui.gui

import net.minecraft.client.gui.GuiTextField

class GuiPasswordField(id: Int, x: Int, y: Int, width: Int, height: Int) :
    GuiTextField(id, x, y, width, height) {
    override fun drawTextBox() {
        val realText = text
        text = ""
        for (i in realText.indices)
            text += '*'

        super.drawTextBox()
        text = realText
    }
}