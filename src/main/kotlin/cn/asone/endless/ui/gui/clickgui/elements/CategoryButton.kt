package cn.asone.endless.ui.gui.clickgui.elements

import cn.asone.endless.ui.font.Fonts
import cn.asone.endless.ui.gui.clickgui.GuiClickGUI
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton

class CategoryButton(id: Int, x: Int, y: Int, text: String) : GuiButton(id, x, y, 62, 18, text) {
    companion object {
        var chosenColor: Int = 0
        var normalColor: Int = 0
    }

    override fun drawButton(mc: Minecraft?, mouseX: Int, mouseY: Int) {
        Fonts.medium21.drawCenteredString(
            displayString,
            xPosition + 30.5F,
            yPosition + 5F,
            if (GuiClickGUI.categoryIndex == this.id)
                chosenColor
            else
                normalColor
        )
    }
}