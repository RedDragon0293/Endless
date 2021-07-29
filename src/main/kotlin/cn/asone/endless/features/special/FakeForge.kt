package cn.asone.endless.features.special

import cn.asone.endless.event.EventHook
import cn.asone.endless.event.EventManager
import cn.asone.endless.event.SendPacketEvent
import cn.asone.endless.utils.ClientUtils
import cn.asone.endless.utils.ListenableClass
import cn.asone.endless.value.BoolValue
import io.netty.buffer.Unpooled
import net.minecraft.network.PacketBuffer
import net.minecraft.network.play.client.C17PacketCustomPayload

object FakeForge : ListenableClass() {
    @JvmField
    val enabled = BoolValue("enabled", false)

    @JvmField
    val fml = BoolValue("fml", false)

    @JvmField
    val fmlProxy = BoolValue("fmlProxy", false)

    @JvmField
    val payload = BoolValue("payload", false)

    override val handledEvents: ArrayList<EventHook> = arrayListOf(
        EventHook(SendPacketEvent::class.java, 100)
    )

    init {
        EventManager.registerListener(this)
        enabled.subValue.add(fml)
        enabled.subValue.add(fmlProxy)
        enabled.subValue.add(payload)
    }

    override fun onSendPacket(event: SendPacketEvent) {
        if (enabled.get() && !mc.isIntegratedServerRunning) {
            val packet = event.packet
            if (payload.get() && packet is C17PacketCustomPayload) {
                if (packet.channelName.equals("MC|Brand", true)) {
                    ClientUtils.logger.info("Modifying custom payload packet...")
                    packet.data = PacketBuffer(Unpooled.buffer()).writeString("fml,forge")
                }
            }
        }
    }

    override fun isHandleEvents() = true
}