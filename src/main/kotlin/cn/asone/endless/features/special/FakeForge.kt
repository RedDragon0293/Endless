package cn.asone.endless.features.special

import cn.asone.endless.event.EventHook
import cn.asone.endless.event.EventManager
import cn.asone.endless.event.ListenableClass
import cn.asone.endless.event.SendPacketEvent
import cn.asone.endless.option.AbstractOption
import cn.asone.endless.option.BoolOption
import cn.asone.endless.option.OptionRegister
import cn.asone.endless.utils.ClientUtils
import io.netty.buffer.Unpooled
import net.minecraft.network.PacketBuffer
import net.minecraft.network.play.client.C17PacketCustomPayload

object FakeForge : ListenableClass(), OptionRegister {
    @JvmField
    val enabled = BoolOption("enabled", false)

    @JvmField
    val fml = BoolOption("fml", false)

    @JvmField
    val fmlProxy = BoolOption("fmlProxy", false)

    @JvmField
    val payload = BoolOption("payload", false)

    override val options: ArrayList<AbstractOption<*>> = arrayListOf(enabled)

    override val handledEvents: ArrayList<EventHook> = arrayListOf(
        //EventHook(SendPacketEvent::class.java, 100)
    )

    init {
        EventManager.registerListener(this)
        enabled.subOptions.add(fml)
        enabled.subOptions.add(fmlProxy)
        enabled.subOptions.add(payload)
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

    override fun isHandlingEvents() = true
}