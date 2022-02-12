package cn.asone.endless.features.module.modules.misc

import cn.asone.endless.event.EventHook
import cn.asone.endless.event.ReceivePacketEvent
import cn.asone.endless.features.module.AbstractModule
import cn.asone.endless.features.module.ModuleCategory
import cn.asone.endless.utils.ClientUtils
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.minecraft.network.play.server.S3FPacketCustomPayload
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.zip.GZIPInputStream


object ModuleHYTParty : AbstractModule(
    "HYTParty",
    ".",
    ModuleCategory.MISC
) {
    override val handledEvents: ArrayList<EventHook>
        get() = arrayListOf(
            EventHook(ReceivePacketEvent::class.java)
        )

    override fun onReceivePacket(event: ReceivePacketEvent) {
        val p = event.packet
        if (p is S3FPacketCustomPayload && p.channelName == "VexView") {
            val bytes = ByteArray(p.bufferData.readableBytes())
            p.bufferData.readBytes(bytes)
            val packetString: String = try {
                decode(bytes)
            } catch (e: Exception) {
                val stream = ByteArrayOutputStream()
                stream.write(bytes)
                stream.toString("UTF-8")
            }
            val jsonObject = JsonParser().parse(packetString)
            if (jsonObject is JsonObject && jsonObject.get("packet_type")?.asString == "hud") {
                ClientUtils.logger.info(packetString)
                event.cancelEvent()
            }
            ClientUtils.displayChatMessage(jsonObject.asString)
            //ClientUtils.displayChatMessage(packetString)
            if (packetString.contains("[but]创建队伍") && packetString.contains("[but]申请入队")) {

            }
        } else if (p is FMLProxyPacket) {
            ClientUtils.chatInfo("FMLProxy!")
            ClientUtils.displayChatMessage(p.channel())
            ClientUtils.displayChatMessage(decode(p.payload().array()))
        }
    }

    @Throws(IOException::class)
    private fun decode(IiIiIIiIII: ByteArray): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val gzipInputStream = GZIPInputStream(ByteArrayInputStream(IiIiIIiIII))
        val array = ByteArray(256)
        var read: Int
        while (gzipInputStream.read(array).also { read = it } >= 0) {
            byteArrayOutputStream.write(array, 0, read)
        }
        return byteArrayOutputStream.toString("UTF-8")
    }
}