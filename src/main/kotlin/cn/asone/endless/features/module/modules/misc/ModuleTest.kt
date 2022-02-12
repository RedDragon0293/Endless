package cn.asone.endless.features.module.modules.misc

import cn.asone.endless.event.EventHook
import cn.asone.endless.event.ReceivePacketEvent
import cn.asone.endless.event.Render2DEvent
import cn.asone.endless.features.module.AbstractModule
import cn.asone.endless.features.module.ModuleCategory
import cn.asone.endless.ui.font.Fonts
import cn.asone.endless.utils.ClientUtils
import cn.asone.endless.utils.RenderUtils
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.network.play.server.S2DPacketOpenWindow
import net.minecraft.network.play.server.S30PacketWindowItems
import net.minecraft.util.EnumChatFormatting
import java.awt.Color

object ModuleTest : AbstractModule(
    "Test",
    "Just used while developing.",
    ModuleCategory.MISC
) {
    override val handledEvents: ArrayList<EventHook> = arrayListOf(
        EventHook(ReceivePacketEvent::class.java)
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
        GlStateManager.disableTexture2D()
        RenderUtils.drawRect(
            10F,
            10F,
            Fonts.mcRegular18.getStringWidth("AAAAAAAAAAAAAAAAAAAAAAAAA").toFloat(),
            Fonts.mcRegular18.FONT_HEIGHT.toFloat(),
            Color.black.rgb
        )
        GlStateManager.enableTexture2D()
        val var0 = Fonts.mcRegular18.drawString(
            "${EnumChatFormatting.UNDERLINE}AAAAAAAAAAAAAAAAAAAAAAAAA",
            10F,
            10F,
            Color.white.rgb
        )
        RenderUtils.pre2D()
        RenderUtils.drawRect(
            var0.toFloat(), 10F, 1F, 10F, Color(100, 100, 100, 100).rgb
        )
        RenderUtils.post2D()
    }
}