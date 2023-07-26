package cn.asone.endless.ui.gui.accounts

import cn.asone.endless.ui.font.Fonts
import cn.asone.endless.utils.ColorUtils
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.resources.I18n

class GuiLoginSelect(val parent: GuiScreen) : GuiScreen() {
    private lateinit var mojangButton: GuiButton
    private lateinit var theAlteningButton: GuiButton
    private lateinit var microsoftButton: GuiButton
    private lateinit var backButton: GuiButton
    override fun initGui() {
        backButton = GuiButton(0, width / 2 - 100, height - 48, I18n.format("gui.back"))
        mojangButton = GuiButton(1, width / 2 - 100, 90, "Mojang")
        theAlteningButton = GuiButton(2, width / 2 - 100, 115, "TheAltening")
        microsoftButton = GuiButton(3, width / 2 - 100, 140, "Microsoft")
        buttonList.add(backButton)
        buttonList.add(mojangButton)
        buttonList.add(theAlteningButton)
        buttonList.add(microsoftButton)
        super.initGui()
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawDefaultBackground()
        Fonts.medium44.drawCenteredString("Accounts", (width / 2).toFloat(), 34F, ColorUtils.getColorInt(0, 111, 255))
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun actionPerformed(button: GuiButton) {
        if (!button.enabled) return
        when (button.id) {
            0 -> mc.displayGuiScreen(parent)
            1 -> mc.displayGuiScreen(GuiMojangLogin(this))
            2 -> mc.displayGuiScreen(GuiTheAlteningLogin(this))
        }
        super.actionPerformed(button)
    }
}