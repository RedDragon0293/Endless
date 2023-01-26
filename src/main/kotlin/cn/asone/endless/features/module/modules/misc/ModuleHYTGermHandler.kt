package cn.asone.endless.features.module.modules.misc

import cn.asone.endless.event.EventHook
import cn.asone.endless.event.ReceivePacketEvent
import cn.asone.endless.features.module.AbstractModule
import cn.asone.endless.features.module.ModuleCategory
import cn.asone.endless.utils.ClientUtils
import io.netty.buffer.Unpooled
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.network.PacketBuffer
import net.minecraft.network.play.client.C17PacketCustomPayload
import net.minecraft.network.play.server.S3FPacketCustomPayload
import java.util.*

object ModuleHYTGermHandler : AbstractModule(
    "HYTGermHandler",
    ".",
    ModuleCategory.MISC
) {
    @JvmStatic
    fun sendGermPacket(id: Int, args: List<String>) {
        val buffer = PacketBuffer(Unpooled.buffer())
        val packet = C17PacketCustomPayload("germmod-netease", buffer)
        buffer.writeInt(id)
        try {
            when (id) {
                4 -> {
                    buffer.writeInt(args[0].toInt())
                    buffer.writeInt(args[1].toInt())
                    buffer.writeString(args[2])
                    buffer.writeString(args[3])
                    buffer.writeString(args[4])
                }
                7 -> {
                    buffer.writeUuid(UUID.fromString(args[0]))
                    buffer.writeInt(args[1].toInt())
                }
                11 -> {
                    buffer.writeString(args[0])
                }
                12 -> {
                    buffer.writeString(args[0])
                    buffer.writeString(args[1])
                    buffer.writeString(args[2])
                    buffer.writeInt(args[3].toInt())
                    buffer.writeInt(args[4].toInt())
                    buffer.writeInt(args[5].toInt())
                }
                14 -> {
                    val resolution = ScaledResolution(mc)
                    buffer.writeDouble(resolution.scaledWidth.toDouble())
                    buffer.writeDouble(resolution.scaledHeight.toDouble())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }
        ClientUtils.logger.info("Germ")
        mc.netHandler.addToSendQueue(packet)
    }

    override val handledEvents: ArrayList<EventHook>
        get() = arrayListOf(EventHook(ReceivePacketEvent::class.java))

    override fun onReceivePacket(event: ReceivePacketEvent) {
        val p = event.packet
        if (p is S3FPacketCustomPayload) {
            val bytes = ByteArray(p.bufferData.readableBytes())
            p.bufferData.readBytes(bytes)
            //germplugin-netease
            if (p.channelName.contains("germ", ignoreCase = true)) {
                ClientUtils.displayChatMessage("Receive ${p.channelName}\n${String(bytes)}")
            }
        }
    }
}