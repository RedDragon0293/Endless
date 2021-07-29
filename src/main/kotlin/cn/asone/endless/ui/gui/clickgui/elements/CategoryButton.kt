package cn.asone.endless.ui.gui.clickgui.elements

import cn.asone.endless.ui.font.CFontRenderer
import cn.asone.endless.ui.font.Fonts
import cn.asone.endless.ui.gui.clickgui.ClickGUI
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import java.awt.Color

class CategoryButton(id: Int, x: Int, y: Int, text: String) : GuiButton(id, x, y, 62, 18, text) {
    companion object {
        private val categoryFont = CFontRenderer(Fonts.getAssetsFont("Roboto-Medium.ttf", 21), true, true)
    }

    override fun drawButton(mc: Minecraft?, mouseX: Int, mouseY: Int) {
        categoryFont.drawCenteredString(
            displayString,
            xPosition + 30.5F,
            yPosition + 5F,
            if (ClickGUI.categoryIndex == this.id)
                Color(255, 255, 255).rgb
            else
                Color(50, 50, 50).rgb
        )
    }
}