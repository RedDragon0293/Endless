package cn.asone.endless.ui.gui.accounts

import cn.asone.endless.ui.font.Fonts
import cn.asone.endless.utils.ColorUtils
import cn.asone.endless.utils.account.LoginUtils
import com.thealtening.auth.TheAlteningAuthentication
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.GuiTextField
import net.minecraft.client.resources.I18n
import net.minecraft.util.EnumChatFormatting
import org.lwjgl.input.Keyboard
import java.awt.Color

class GuiTheAlteningLogin(val parent: GuiScreen) : GuiScreen() {
    companion object {
        val auth: TheAlteningAuthentication = TheAlteningAuthentication.mojang()
    }

    private lateinit var loginButton: GuiButton
    private lateinit var backButton: GuiButton
    private lateinit var thealtening: GuiTextField
    private var status = "§7Idle..."
    override fun initGui() {
        Keyboard.enableRepeatEvents(true)
        loginButton = GuiButton(1, width / 2 - 100, height - 72, "Login")
        loginButton.enabled = false
        backButton = GuiButton(0, width / 2 - 100, height - 48, I18n.format("gui.back"))
        thealtening = GuiTextField(4, width / 2 - 100, 90, 200, 20)
        thealtening.maxStringLength = 100
        buttonList.add(loginButton)
        buttonList.add(backButton)
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawDefaultBackground()
        Fonts.medium44.drawCenteredString(
            "TheAltening Login",
            (width / 2).toFloat(),
            34F,
            ColorUtils.getColorInt(0, 111, 255)
        )
        Fonts.regular24.drawCenteredString(
            "Status: $status",
            (width / 2).toFloat(),
            34F + Fonts.medium44.FONT_HEIGHT + 8,
            Color.white.rgb
        )
        Fonts.mcRegular18.drawString(
            "Username: ${EnumChatFormatting.GREEN}${mc.session.username}",
            4,
            4,
            Color.white.rgb
        )
        thealtening.drawTextBox()
        if (thealtening.text.isEmpty() && !thealtening.focused)
            Fonts.regular20.drawString("TheAltening token", width / 2 - 100 + 6F, 146F, Color.gray.rgb)
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun actionPerformed(button: GuiButton) {
        if (!button.enabled) return
        when (button.id) {
            0 -> mc.displayGuiScreen(parent)
            1 -> {
                loginButton.enabled = false
                Thread(
                    {
                        status = "§cLogging in..."
                        status = LoginUtils.loginTheAltening(thealtening.text)
                        loginButton.enabled = true
                    }, "TheAltening account auth thread"
                ).start()
            }
        }
        super.actionPerformed(button)
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        when (keyCode) {
            Keyboard.KEY_ESCAPE -> {
                mc.displayGuiScreen(parent)
                return
            }

            Keyboard.KEY_RETURN -> {
                actionPerformed(loginButton)
                return
            }
        }
        if (thealtening.focused) thealtening.textBoxKeyTyped(typedChar, keyCode)
        super.keyTyped(typedChar, keyCode)
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        thealtening.mouseClicked(mouseX, mouseY, mouseButton)
        super.mouseClicked(mouseX, mouseY, mouseButton)
    }

    override fun updateScreen() {
        thealtening.updateCursorCounter()
        super.updateScreen()
    }

    override fun onGuiClosed() {
        Keyboard.enableRepeatEvents(false)
        super.onGuiClosed()
    }
}