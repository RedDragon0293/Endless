package cn.asone.endless.ui.gui.accounts

import cn.asone.endless.ui.font.Fonts
import cn.asone.endless.ui.gui.GuiPasswordField
import cn.asone.endless.utils.ColorUtils
import cn.asone.endless.utils.account.LoginUtils
import cn.asone.endless.utils.account.MinecraftAccount
import com.thealtening.auth.TheAlteningAuthentication
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.GuiTextField
import net.minecraft.client.resources.I18n
import net.minecraft.util.EnumChatFormatting
import org.lwjgl.input.Keyboard
import java.awt.Color

class GuiAccounts(val parent: GuiScreen) : GuiScreen() {
    companion object {
        val auth: TheAlteningAuthentication = TheAlteningAuthentication.mojang()
    }

    private lateinit var loginButton: GuiButton
    private lateinit var backButton: GuiButton
    private lateinit var username: GuiTextField
    private lateinit var password: GuiPasswordField
    private lateinit var thealtening: GuiTextField
    private var status = "§7Idle..."

    override fun initGui() {
        Keyboard.enableRepeatEvents(true)
        loginButton = GuiButton(1, width / 2 - 100, height - 72, "Login")
        loginButton.enabled = false
        backButton = GuiButton(0, width / 2 - 100, height - 48, I18n.format("gui.back"))
        username = GuiTextField(2, width / 2 - 100, 90, 200, 20)
        username.maxStringLength = 1024
        username.text = mc.session.username
        password = GuiPasswordField(3, width / 2 - 100, 115, 200, 20)
        password.maxStringLength = 1024
        thealtening = GuiTextField(4, width / 2 - 100, 140, 200, 20)
        thealtening.maxStringLength = 100
        buttonList.add(loginButton)
        buttonList.add(backButton)
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawDefaultBackground()
        Fonts.medium44.drawCenteredString("Accounts", (width / 2).toFloat(), 34F, ColorUtils.getColorInt(0, 111, 255))
        Fonts.regular24.drawCenteredString(
            "Status: $status",
            (width / 2).toFloat(),
            34F + Fonts.medium44.FONT_HEIGHT + 8,
            Color.white.rgb
        )
        Fonts.mcRegular18.drawString("Username: ${EnumChatFormatting.GREEN}${mc.session.username}", 4, 4, Color.white.rgb)
        Fonts.mcRegular18.drawString(
            "Session: ${EnumChatFormatting.GREEN}${mc.session.sessionID}",
            4,
            14,
            Color.white.rgb
        )
        Fonts.mcRegular18.drawString("Token: ${EnumChatFormatting.GREEN}${mc.session.token}", 4, 24, Color.white.rgb)
        username.drawTextBox()
        password.drawTextBox()
        thealtening.drawTextBox()
        if (username.text.isEmpty() && !username.focused)
            Fonts.regular20.drawString("Username / Email", width / 2 - 100 + 6F, 96F, Color.gray.rgb)
        if (password.text.isEmpty() && !password.focused)
            Fonts.regular20.drawString("Password", width / 2 - 100 + 6F, 121F, Color.gray.rgb)
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
                        if (username.text.isNotEmpty()) {
                            status = "§aLogging in..."
                            status =
                                if (password.text.isEmpty())
                                    LoginUtils.login(MinecraftAccount(username.text))
                                else
                                    LoginUtils.login(MinecraftAccount(username.text, password.text))
                        } else {
                            status = "§cLogging in..."
                            status = LoginUtils.loginTheAltening(thealtening.text)
                        }
                        loginButton.enabled = true
                    }, "Mojang account auth thread"
                ).start()
            }
        }
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
        if (username.focused) username.textBoxKeyTyped(typedChar, keyCode)

        if (password.focused) password.textBoxKeyTyped(typedChar, keyCode)

        if (thealtening.focused) thealtening.textBoxKeyTyped(typedChar, keyCode)

        loginButton.enabled =
            (username.text.isNotEmpty() || thealtening.text.isNotEmpty()) && !(username.text.isNotEmpty() && thealtening.text.isNotEmpty())
        super.keyTyped(typedChar, keyCode)
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        username.mouseClicked(mouseX, mouseY, mouseButton)
        password.mouseClicked(mouseX, mouseY, mouseButton)
        thealtening.mouseClicked(mouseX, mouseY, mouseButton)
        super.mouseClicked(mouseX, mouseY, mouseButton)
    }

    override fun updateScreen() {
        username.updateCursorCounter()
        password.updateCursorCounter()
        thealtening.updateCursorCounter()
        super.updateScreen()
    }

    override fun onGuiClosed() {
        Keyboard.enableRepeatEvents(false)
    }
}