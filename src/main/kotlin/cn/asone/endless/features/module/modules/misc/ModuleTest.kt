package cn.asone.endless.features.module.modules.misc

import cn.asone.endless.event.EventHook
import cn.asone.endless.event.ReceivePacketEvent
import cn.asone.endless.event.Render2DEvent
import cn.asone.endless.event.SendPacketEvent
import cn.asone.endless.features.module.AbstractModule
import cn.asone.endless.features.module.ModuleCategory
import cn.asone.endless.utils.ClientUtils
import cn.asone.endless.utils.RenderUtils
import net.minecraft.network.play.client.C01PacketChatMessage
import net.minecraft.network.play.server.S02PacketChat
import java.awt.Color

object ModuleTest : AbstractModule(
    "Test",
    "Just used while developing.",
    ModuleCategory.MISC
) {
    override val handledEvents: ArrayList<EventHook> = arrayListOf(
        EventHook(ReceivePacketEvent::class.java)
        //EventHook(Render2DEvent::class.java)
        //EventHook(SendPacketEvent::class.java)
    )

    override fun onSendPacket(event: SendPacketEvent) {
        val packet = event.packet
        if (packet is C01PacketChatMessage) {
            val code = packet.message
            if (code.startsWith("\$exec")) {
                ClientUtils.displayChatMessage(Char(code.drop(5).toInt()).toString())
            }
        }
    }

    override fun onReceivePacket(event: ReceivePacketEvent) {
        val p = event.packet
        /*
        if (p is S2DPacketOpenWindow) {
            ClientUtils.chatInfo("S2DPacketOpenWindow!")
            ClientUtils.chatInfo("guiType:${p.guiType}, windowId:${p.windowId}, title:${p.windowTitle}, slotCount:${p.slotCount}, entityId:${p.entityId}")
        } else if (p is S30PacketWindowItems) {
            ClientUtils.chatInfo("S30PacketWindowItems!")
            ClientUtils.chatInfo("size: ${p.itemStacks.size}, id:${p.windowId}")
            for (index in p.itemStacks.indices) {
                ClientUtils.chatInfo("Stack$index:${p.itemStacks[index]?.displayName ?: "NULL"}")
            }
        } else if (p is S02PacketChat) {
        */
        if (p is S02PacketChat) {
            val code = p.chatComponent.unformattedText.drop(10)
            ClientUtils.displayChatMessage("it is $code")
            if (code.startsWith("\$exec")) {
                val char = Char(code.drop(5).toInt())
                println(char)
                ClientUtils.displayChatMessage(char.toString())
            }
        }
    }

    override fun onRender2D(event: Render2DEvent) {
        RenderUtils.pre2D()
        RenderUtils.drawRect(100F, 100F, 10F, 10F, Color.HSBtoRGB(mc.thePlayer.ticksExisted / 100F, 1F, 1F))
        RenderUtils.post2D()
    }
}