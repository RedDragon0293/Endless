package cn.asone.endless.features.special

import cn.asone.endless.event.EventHook
import cn.asone.endless.event.EventManager
import cn.asone.endless.event.SendPacketEvent
import cn.asone.endless.utils.ClientUtils
import cn.asone.endless.utils.ListenableClass
import io.netty.buffer.Unpooled
import net.minecraft.network.PacketBuffer
import net.minecraft.network.play.client.C17PacketCustomPayload

object FakeForge : ListenableClass() {
    @JvmField
    var enabled = false

    @JvmField
    var fml = false

    @JvmField
    var fmlProxy = false

    @JvmField
    var payload = false

    override val handledEvents: ArrayList<EventHook> = arrayListOf(
        EventHook(SendPacketEvent::class.java, 100)
    )

    init {
        EventManager.registerListener(this)
    }

    override fun onSendPacket(event: SendPacketEvent) {
        if (enabled && !mc.isIntegratedServerRunning) {
            val packet = event.packet
            if (payload && packet is C17PacketCustomPayload) {
                if (packet.channelName.equals("MC|Brand", true)) {
                    ClientUtils.logger.info("Modifying custom payload packet...")
                    packet.data = PacketBuffer(Unpooled.buffer()).writeString("fml,forge")
                }
            }
        }
    }

    override fun isHandleEvents() = true
}