package cn.asone.endless.features.module.modules.misc

import cn.asone.endless.event.EventHook
import cn.asone.endless.event.ReceivePacketEvent
import cn.asone.endless.features.module.AbstractModule
import cn.asone.endless.features.module.ModuleCategory
import cn.asone.endless.utils.ClientUtils
import com.google.gson.JsonParser
import io.netty.buffer.Unpooled
import net.minecraft.network.PacketBuffer
import net.minecraft.network.play.client.C17PacketCustomPayload
import net.minecraft.network.play.server.S3FPacketCustomPayload
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream


object ModuleHYTParty : AbstractModule(
    "HYTParty",
    ".",
    ModuleCategory.MISC
) {
    override val handledEvents: ArrayList<EventHook>
        get() = arrayListOf(
            EventHook(ReceivePacketEvent::class.java)
        )
    private val jsonParser = JsonParser()
    private const val JsonCloseGUI =
        "{\"packet_sub_type\":\"null\",\"packet_data\":\"null\",\"packet_type\":\"gui_close\"}"

    private const val JsonOpenGUI =
        "{\"packet_sub_type\":\"null\",\"packet_data\":\"null\",\"packet_type\":\"opengui\"}"

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
            val jsonObject = jsonParser.parse(packetString).asJsonObject
            ClientUtils.logger.info(packetString)
            if (jsonObject.get("packet_type")?.asString == "hud") {
                //ClientUtils.logger.info(packetString)
                event.cancelEvent()
                //return
            }
            if (jsonObject.get("packet_type")?.asString == "gui") {
                ClientUtils.chatInfo("======VexView尝试打开一个GUI======")
                val data = Unpooled.wrappedBuffer(encode(JsonOpenGUI))
                mc.netHandler.addToSendQueue(C17PacketCustomPayload("VexView", PacketBuffer(data)))
                val reader = VexViewDataReader(jsonObject.get("packet_data").asString)
                for (i in reader.buttonList)
                    ClientUtils.chatInfo("${i.key}: ${i.value}")
            }
        } else if (p is FMLProxyPacket) {
            ClientUtils.chatInfo("FMLProxy!")
            ClientUtils.displayChatMessage(p.channel())
            ClientUtils.displayChatMessage(decode(p.payload().array()))
        }
    }

    fun sendDebugPacket(id: String) {
        val data =
            Unpooled.wrappedBuffer(encode("{\"packet_sub_type\":\"$id\",\"packet_data\":\"null\",\"packet_type\":\"button\"}"))
        //data = Unpooled.wrappedBuffer(encode(JsonCloseGUI))
        mc.netHandler.addToSendQueue(C17PacketCustomPayload("VexView", PacketBuffer(data)))
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

    @Throws(IOException::class)
    private fun encode(json: String): ByteArray {
        val arrayInputStream = ByteArrayInputStream(json.toByteArray(charset("UTF-8")))
        val bout = ByteArrayOutputStream()
        val out = GZIPOutputStream(bout)
        val array = ByteArray(256)
        var read: Int
        while (arrayInputStream.read(array).also { read = it } >= 0) out.write(array, 0, read)
        out.close()
        out.finish()
        return bout.toByteArray()
    }

    class VexViewDataReader(val json: String) {
        val buttonList: HashMap<String, String> = hashMapOf()

        init {
            val data = jsonParser.parse(json).asJsonObject.entrySet()
            val elements = json.split("<#>")
            var index = 0
            while (index < elements.size) {
                if (elements[index].contains("[but]")) {
                    val sub = elements[index].split("<&>")
                    buttonList[sub[0].drop(5)] = sub[6]
                }
                index++
            }
        }
    }
}