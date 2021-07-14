package cn.asone.endless.features.module.modules.misc

import cn.asone.endless.event.Event
import cn.asone.endless.event.KeyEvent
import cn.asone.endless.event.SendPacketEvent
import cn.asone.endless.features.module.Module
import cn.asone.endless.features.module.ModuleCategory
import cn.asone.endless.utils.ClientUtils
import net.minecraft.network.play.client.C01PacketChatMessage
import org.lwjgl.input.Keyboard

object TestModule : Module(
        "TestModule",
        "Hello, world!",
        ModuleCategory.MISC,
        Keyboard.KEY_R) {

    override fun handledEvents(): List<Class<out Event>> = arrayListOf(
            SendPacketEvent::class.java,
            KeyEvent::class.java
    )

    override fun onSendPacket(event: SendPacketEvent) {
        val packet = event.packet
        if (packet is C01PacketChatMessage) {
            ClientUtils.displayChatMessage("WDNMD C01!")
            packet.message = "WDNMD"
        }
    }

    override fun onKey(event: KeyEvent) {
        ClientUtils.displayChatMessage("${event.key} ${event.state}")
    }
}