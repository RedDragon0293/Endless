package cn.asone.endless.ui.gui.accounts

import cn.asone.endless.ui.font.Fonts
import cn.asone.endless.ui.gui.GuiPasswordField
import cn.asone.endless.utils.account.LoginUtils
import cn.asone.endless.utils.account.MinecraftAccount
import com.thealtening.auth.TheAlteningAuthentication
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.GuiTextField
import net.minecraft.client.resources.I18n
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
    private var status = "§7Idle..."

    override fun initGui() {
        loginButton = GuiButton(1, width / 2 - 100, height - 72, "Login")
        loginButton.enabled = false
        backButton = GuiButton(0, width / 2 - 100, height - 48, I18n.format("gui.back"))
        username = GuiTextField(2, fontRendererObj, width / 2 - 100, 90, 200, 20)
        username.maxStringLength = 1024
        password = GuiPasswordField(3, fontRendererObj, width / 2 - 100, 115, 200, 20)
        password.maxStringLength = 1024
        buttonList.add(loginButton)
        buttonList.add(backButton)
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawDefaultBackground()
        Fonts.medium44.drawCenteredString("Accounts", (width / 2).toFloat(), 34F, Color(0, 111, 255).rgb)
        Fonts.regular24.drawCenteredString(
            "Status: $status",
            (width / 2).toFloat(),
            34F + Fonts.medium44.height + 8,
            Color.white.rgb
        )
        username.drawTextBox()
        password.drawTextBox()
        if (username.text.isEmpty() && !username.isFocused)
            Fonts.regular20.drawCenteredString("Username / Email", width / 2 - 55F, 96F, Color.gray.rgb)
        if (password.text.isEmpty() && !password.isFocused)
            Fonts.regular20.drawCenteredString("Password", width / 2 - 74F, 121F, Color.gray.rgb)
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
                        status = "§aLogging in..."
                        status =
                            if (password.text.isEmpty())
                                LoginUtils.login(MinecraftAccount(username.text))
                            else LoginUtils.login(MinecraftAccount(username.text, password.text))
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
        if (username.isFocused) username.textboxKeyTyped(typedChar, keyCode)

        if (password.isFocused) password.textboxKeyTyped(typedChar, keyCode)

        loginButton.enabled = username.text.isNotEmpty()
        super.keyTyped(typedChar, keyCode)
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        username.mouseClicked(mouseX, mouseY, mouseButton)
        password.mouseClicked(mouseX, mouseY, mouseButton)
        super.mouseClicked(mouseX, mouseY, mouseButton)
    }

    override fun updateScreen() {
        username.updateCursorCounter()
        password.updateCursorCounter()
        super.updateScreen()
    }
}