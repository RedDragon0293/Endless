package cn.asone.endless.ui.gui

import cn.asone.endless.features.special.FakeForge
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import java.awt.Color

class GuiFakeForge(val parent: GuiScreen) : GuiScreen() {
    private lateinit var enableButton: GuiButton
    private lateinit var fmlButton: GuiButton
    private lateinit var fmlProxyButton: GuiButton
    private lateinit var payloadButton: GuiButton

    override fun initGui() {
        enableButton = GuiButton(1, this.width / 2 - 100, this.height / 4 + 35, "启用FakeForge: ${if (FakeForge.enabled) "On" else "Off"}")
        fmlButton = GuiButton(2, this.width / 2 - 100, this.height / 4 + 50 + 25, "伪造FML: ${if (FakeForge.fml) "On" else "Off"}")
        fmlProxyButton = GuiButton(3, this.width / 2 - 100, this.height / 4 + 50 + 25 * 2, "伪造 FML 代理数据包: ${if (FakeForge.fmlProxy) "On" else "Off"}")
        fmlProxyButton.enabled = false
        payloadButton = GuiButton(4, this.width / 2 - 100, this.height / 4 + 50 + 25 * 3, "伪造 Payload 数据包: ${if (FakeForge.payload) "On" else "Off"}")
        this.buttonList.add(enableButton)
        this.buttonList.add(fmlButton)
        this.buttonList.add(fmlProxyButton)
        this.buttonList.add(payloadButton)

        this.buttonList.add(GuiButton(0, this.width / 2 - 100, this.height / 4 + 50 + 25 * 4 + 10, "Back"))
    }

    override fun actionPerformed(button: GuiButton) {
        when (button.id) {
            1 -> {
                FakeForge.enabled = !FakeForge.enabled
                enableButton.displayString = "启用FakeForge: ${if (FakeForge.enabled) "On" else "Off"}"
            }

            2 -> {
                FakeForge.fml = !FakeForge.fml
                fmlButton.displayString = "伪造FML: ${if (FakeForge.fml) "On" else "Off"}"
            }

            3 -> {
                FakeForge.fmlProxy = !FakeForge.fmlProxy
                fmlProxyButton.displayString = "伪造 FML 代理数据包(未实现支持!): ${if (FakeForge.fmlProxy) "On" else "Off"}"
            }

            4 -> {
                FakeForge.payload = !FakeForge.payload
                payloadButton.displayString = "伪造 Payload 数据包: ${if (FakeForge.payload) "On" else "Off"}"
            }

            0 -> mc.displayGuiScreen(parent)
        }
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawDefaultBackground()
        fontRendererObj.drawCenteredString("FakeForge", this.width / 2F, this.height / 8 + 10F, Color(71, 81, 192).rgb, false)
        super.drawScreen(mouseX, mouseY, partialTicks)
    }
}