package cn.asone.endless.ui.gui

import cn.asone.endless.features.special.FakeForge
import cn.asone.endless.ui.font.Fonts
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.resources.I18n
import java.awt.Color

class GuiFakeForge(val parent: GuiScreen) : GuiScreen() {
    private lateinit var enableButton: GuiButton
    private lateinit var fmlButton: GuiButton
    private lateinit var fmlProxyButton: GuiButton
    private lateinit var payloadButton: GuiButton

    override fun initGui() {
        enableButton = GuiButton(
            1,
            this.width / 2 - 100,
            this.height / 4 + 35,
            "启用FakeForge: ${if (FakeForge.enabled.get()) "On" else "Off"}"
        )
        fmlButton = GuiButton(
            2,
            this.width / 2 - 100,
            this.height / 4 + 50 + 25,
            "伪造C01Packet握手FML: ${if (FakeForge.fml.get()) "On" else "Off"}"
        )
        fmlProxyButton = GuiButton(
            3,
            this.width / 2 - 100,
            this.height / 4 + 50 + 25 * 2,
            "伪造 FML 代理数据包(未实现支持!): ${if (FakeForge.fmlProxy.get()) "On" else "Off"}"
        )
        fmlProxyButton.enabled = false
        payloadButton = GuiButton(
            4,
            this.width / 2 - 100,
            this.height / 4 + 50 + 25 * 3,
            "伪造 Payload 数据包: ${if (FakeForge.payload.get()) "On" else "Off"}"
        )
        this.buttonList.add(enableButton)
        this.buttonList.add(fmlButton)
        this.buttonList.add(fmlProxyButton)
        this.buttonList.add(payloadButton)

        this.buttonList.add(
            GuiButton(
                0,
                this.width / 2 - 100,
                this.height / 4 + 50 + 25 * 4 + 10,
                I18n.format("gui.back")
            )
        )
    }

    override fun actionPerformed(button: GuiButton) {
        when (button.id) {
            1 -> {
                FakeForge.enabled.set(!FakeForge.enabled.get())
                enableButton.displayString = "启用FakeForge: ${if (FakeForge.enabled.get()) "On" else "Off"}"
            }

            2 -> {
                FakeForge.fml.set(!FakeForge.fml.get())
                fmlButton.displayString = "伪造C01Packet握手FML: ${if (FakeForge.fml.get()) "On" else "Off"}"
            }

            3 -> {
                FakeForge.fmlProxy.set(!FakeForge.fmlProxy.get())
                fmlProxyButton.displayString = "伪造 FML 代理数据包(未实现支持!): ${if (FakeForge.fmlProxy.get()) "On" else "Off"}"
            }

            4 -> {
                FakeForge.payload.set(!FakeForge.payload.get())
                payloadButton.displayString = "伪造 Payload 数据包: ${if (FakeForge.payload.get()) "On" else "Off"}"
            }

            0 -> mc.displayGuiScreen(parent)
        }
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawDefaultBackground()
        Fonts.medium44.drawCenteredString(
            "FakeForge",
            this.width / 2F,
            this.height / 8 + 10F,
            Color(71, 81, 192).rgb,
            false
        )
        super.drawScreen(mouseX, mouseY, partialTicks)
    }
}