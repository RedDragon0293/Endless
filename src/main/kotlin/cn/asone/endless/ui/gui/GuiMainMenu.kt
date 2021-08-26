package cn.asone.endless.ui.gui

import cn.asone.endless.ui.font.Fonts
import net.minecraft.client.gui.GuiScreen

class GuiMainMenu : GuiScreen() {
    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {

        var s = "Minecraft 1.8.9"
        if (mc.isDemo) {
            s = "$s Demo"
        }
        drawString(Fonts.mcRegular18, s, 2, height - 10, -1)
        val s2 = "Copyright Mojang AB. Do not distribute!"
        drawString(Fonts.mcRegular18, s2, width - Fonts.mcRegular18.getStringWidth(s2) - 2, height - 10, -1)
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun initGui() {

    }
}