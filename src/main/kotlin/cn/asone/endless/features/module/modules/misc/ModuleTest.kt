package cn.asone.endless.features.module.modules.misc

import cn.asone.endless.event.EventHook
import cn.asone.endless.event.ReceivePacketEvent
import cn.asone.endless.event.Render2DEvent
import cn.asone.endless.features.module.AbstractModule
import cn.asone.endless.features.module.ModuleCategory
import cn.asone.endless.utils.ClientUtils
import cn.asone.endless.utils.RenderUtils
import net.minecraft.network.play.server.S2DPacketOpenWindow
import net.minecraft.network.play.server.S30PacketWindowItems
import java.awt.Color

object ModuleTest : AbstractModule(
    "Test",
    "Just used while developing.",
    ModuleCategory.MISC
) {
    override val handledEvents: ArrayList<EventHook> = arrayListOf(
        //EventHook(ReceivePacketEvent::class.java)
        EventHook(Render2DEvent::class.java)
    )

    override fun onReceivePacket(event: ReceivePacketEvent) {
        val p = event.packet
        if (p is S2DPacketOpenWindow) {
            ClientUtils.chatInfo("S2DPacketOpenWindow!")
            ClientUtils.chatInfo("guiType:${p.guiType}, windowId:${p.windowId}, title:${p.windowTitle}, slotCount:${p.slotCount}, entityId:${p.entityId}")
        } else if (p is S30PacketWindowItems) {
            ClientUtils.chatInfo("S30PacketWindowItems!")
            ClientUtils.chatInfo("size: ${p.itemStacks.size}, id:${p.windowId}")
            for (index in p.itemStacks.indices) {
                ClientUtils.chatInfo("Stack$index:${p.itemStacks[index]?.displayName ?: "NULL"}")
            }
        }
    }

    override fun onRender2D(event: Render2DEvent) {
        RenderUtils.pre2D()
        RenderUtils.drawRect(100F, 100F, 10F, 10F, Color.HSBtoRGB(mc.thePlayer.ticksExisted / 100F, 1F, 1F))
        RenderUtils.post2D()
    }
}