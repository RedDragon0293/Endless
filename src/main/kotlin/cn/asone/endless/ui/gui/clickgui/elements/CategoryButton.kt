package cn.asone.endless.ui.gui.clickgui.elements

import cn.asone.endless.ui.font.Fonts
import cn.asone.endless.ui.gui.clickgui.GuiClickGUI
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import java.awt.Color

class CategoryButton(id: Int, x: Int, y: Int, text: String) : GuiButton(id, x, y, 62, 18, text) {
    companion object {
        var chosenColor: Int = Color.white.rgb
        var normalColor: Int = Color(50, 50, 50).rgb
    }

    override fun drawButton(mc: Minecraft?, mouseX: Int, mouseY: Int) {
        Fonts.medium22.drawCenteredString(
            displayString,
            xPosition + 30F,
            yPosition + 4.5F,
            if (GuiClickGUI.categoryIndex == this.id)
                chosenColor
            else
                normalColor
        )
    }
}