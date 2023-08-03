package cn.asone.endless.features.module.modules.visual

import cn.asone.endless.Endless
import cn.asone.endless.event.EventHook
import cn.asone.endless.event.ReceivePacketEvent
import cn.asone.endless.event.SendPacketEvent
import cn.asone.endless.features.module.AbstractModule
import cn.asone.endless.features.module.ModuleCategory
import cn.asone.endless.option.AbstractOption
import cn.asone.endless.option.ListOption
import cn.asone.endless.ui.gui.clickgui.GuiClickGUI
import cn.asone.endless.ui.gui.clickgui.elements.CategoryButton
import cn.asone.endless.ui.gui.clickgui.elements.moduleslist.AbstractButton
import cn.asone.endless.utils.ClientUtils
import net.minecraft.network.play.client.C0DPacketCloseWindow
import net.minecraft.network.play.server.S2EPacketCloseWindow
import org.lwjgl.input.Keyboard
import java.awt.Color

object ModuleClickGUI : AbstractModule(
    "ClickGUI",
    "Opens the ClickGUI.",
    ModuleCategory.VISUAL,
    Keyboard.KEY_RSHIFT,
    false
) {
    private val themeValue = object : ListOption("Theme", arrayOf("Light", "Dark"), "Light") {
        override fun changeValue(newValue: String) {
            super.changeValue(newValue)
            if (this.get() == "Light") {
                GuiClickGUI.backgroundColor = Color(236, 236, 236).rgb
                GuiClickGUI.elementColor = Color.white.rgb
                GuiClickGUI.textColor = Color.black.rgb
                CategoryButton.normalColor = Color(50, 50, 50).rgb
                CategoryButton.chosenColor = Color.white.rgb
                AbstractButton.boxColor = Color.white.rgb
                AbstractButton.buttonColor = Color.white.rgb
            } else {
                GuiClickGUI.backgroundColor = Color(25, 25, 25).rgb
                GuiClickGUI.elementColor = Color(40, 40, 40).rgb
                GuiClickGUI.textColor = Color.white.rgb
                CategoryButton.normalColor = Color(235, 235, 235).rgb
                CategoryButton.chosenColor = Color(50, 50, 50).rgb
                AbstractButton.boxColor = Color(55, 55, 55).rgb
                AbstractButton.buttonColor = Color(240, 240, 240).rgb
            }
        }
    }
    override val handledEvents: ArrayList<EventHook> = arrayListOf(
        EventHook(SendPacketEvent::class.java, 100),
        EventHook(ReceivePacketEvent::class.java)
    )

    override val options: ArrayList<AbstractOption<*>> = arrayListOf(themeValue)

    override fun onEnable() {
        mc.displayGuiScreen(Endless.clickGUI)
    }

    override fun onSendPacket(event: SendPacketEvent) {
        if (event.packet is C0DPacketCloseWindow && mc.currentScreen is GuiClickGUI) {
            ClientUtils.displayChatMessage("Cancel CPacketCloseWindow.")
            event.cancelEvent()
        }
    }

    override fun onReceivePacket(event: ReceivePacketEvent) {
        if (event.packet is S2EPacketCloseWindow && mc.currentScreen is GuiClickGUI) {
            ClientUtils.displayChatMessage("Cancel SPacketCloseWindow.")
            event.cancelEvent()
        }
    }

    override fun isHandlingEvents() = true
}