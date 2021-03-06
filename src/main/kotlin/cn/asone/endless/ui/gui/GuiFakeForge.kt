package cn.asone.endless.ui.gui

import cn.asone.endless.features.special.FakeForge
import cn.asone.endless.ui.font.Fonts
import cn.asone.endless.utils.ColorUtils
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.resources.I18n

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
            "启用 FakeForge: ${I18n.format(if (FakeForge.enabled.get()) "options.on" else "options.off")}"
        )
        fmlButton = GuiButton(
            2,
            this.width / 2 - 100,
            this.height / 4 + 50 + 25,
            "伪造 C00 握手数据包FML: ${I18n.format(if (FakeForge.fml.get()) "options.on" else "options.off")}"
        )
        fmlProxyButton = GuiButton(
            3,
            this.width / 2 - 100,
            this.height / 4 + 50 + 25 * 2,
            "伪造 FML 代理数据包(未实现支持!): ${I18n.format(if (FakeForge.fmlProxy.get()) "options.on" else "options.off")}"
        )
        fmlProxyButton.enabled = false
        payloadButton = GuiButton(
            4,
            this.width / 2 - 100,
            this.height / 4 + 50 + 25 * 3,
            "伪造 Payload 数据包: ${I18n.format(if (FakeForge.payload.get()) "options.on" else "options.off")}"
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
                enableButton.displayString =
                    "启用 FakeForge: ${I18n.format(if (FakeForge.enabled.get()) "options.on" else "options.off")}"
            }

            2 -> {
                FakeForge.fml.set(!FakeForge.fml.get())
                fmlButton.displayString =
                    "伪造 C00 握手数据包FML: ${I18n.format(if (FakeForge.fml.get()) "options.on" else "options.off")}"
            }

            3 -> {
                FakeForge.fmlProxy.set(!FakeForge.fmlProxy.get())
                fmlProxyButton.displayString =
                    "伪造 FML 代理数据包(未实现支持!): ${I18n.format(if (FakeForge.fmlProxy.get()) "options.on" else "options.off")}"
            }

            4 -> {
                FakeForge.payload.set(!FakeForge.payload.get())
                payloadButton.displayString =
                    "伪造 Payload 数据包: ${I18n.format(if (FakeForge.payload.get()) "options.on" else "options.off")}"
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
            ColorUtils.getColorInt(71, 81, 192),
            false
        )
        super.drawScreen(mouseX, mouseY, partialTicks)
    }
}