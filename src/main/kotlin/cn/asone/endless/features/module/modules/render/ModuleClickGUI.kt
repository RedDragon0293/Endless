package cn.asone.endless.features.module.modules.render

import cn.asone.endless.event.EventHook
import cn.asone.endless.event.SendPacketEvent
import cn.asone.endless.features.module.AbstractModule
import cn.asone.endless.features.module.ModuleCategory
import cn.asone.endless.ui.gui.ClickGUI
import cn.asone.endless.utils.ClientUtils
import net.minecraft.network.play.client.C0DPacketCloseWindow
import org.lwjgl.input.Keyboard

object ModuleClickGUI : AbstractModule(
    "ClickGUI",
    "Opens the ClickGUI.",
    ModuleCategory.RENDER,
    Keyboard.KEY_RSHIFT,
    false
) {
    private val clickGUI = ClickGUI()
    override val handledEvents: ArrayList<EventHook> = arrayListOf(
        EventHook(SendPacketEvent::class.java, 100)
    )

    override fun onEnable() {
        ClientUtils.displayChatMessage("OnEnable.")
        mc.displayGuiScreen(clickGUI)
    }

    override fun onSendPacket(event: SendPacketEvent) {
        if (event.packet is C0DPacketCloseWindow && mc.currentScreen is ClickGUI) {
            ClientUtils.displayChatMessage("Cancel CPacketCloseWindow.")
            event.cancelEvent()
        }
    }

    override fun isHandleEvents() = true
}