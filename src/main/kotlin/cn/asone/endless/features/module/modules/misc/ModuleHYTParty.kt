package cn.asone.endless.features.module.modules.misc

import cn.asone.endless.event.EventHook
import cn.asone.endless.event.ReceivePacketEvent
import cn.asone.endless.features.module.AbstractModule
import cn.asone.endless.features.module.ModuleCategory
import cn.asone.endless.utils.ClientUtils
import com.google.gson.JsonElement
import com.google.gson.JsonObject
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
            /**
             * 获取Payload传输的字符串
             */
            val packetString: String = try {
                decode(bytes)
            } catch (e: Exception) {
                val stream = ByteArrayOutputStream()
                stream.write(bytes)
                stream.toString("UTF-8")
            }
            val packetReader = VexViewPacketReader(packetString)
            if (packetReader.packetType == "hud") {
                //ClientUtils.logger.info(packetString)
                event.cancelEvent()
                return
            }
            ClientUtils.logger.info(packetString)
            if (packetReader.packetType == "ver") {
                if (packetReader.packetSubType == "get") {
                    ClientUtils.chatInfo("发送VexView版本数据包.")
                    //sendDebugPacket(arrayListOf("post", "2.6.10", "ver"))
                } else if (packetReader.packetSubType == "ok") {
                    ClientUtils.chatSuccess("VexView版本效验成功!")
                }
                return
            }
            if (packetReader.packetType == "gui") {
                ClientUtils.chatInfo("======VexView尝试打开一个GUI======")
                val data = Unpooled.wrappedBuffer(encode(JsonOpenGUI))
                mc.netHandler.addToSendQueue(C17PacketCustomPayload("VexView", PacketBuffer(data)))
                for (i in packetReader.buttonList)
                    ClientUtils.chatInfo("${i.key}: ${i.value}")
            }
        } else if (p is FMLProxyPacket) {
            ClientUtils.chatInfo("FMLProxy!")
            ClientUtils.displayChatMessage(p.channel())
            ClientUtils.displayChatMessage(decode(p.payload().array()))
        }
    }

    fun sendDebugPacket(params: List<String>) {
        if (params.size < 3) {
            ClientUtils.chatError("参数长度错误!")
            return
        }
        val data =
            Unpooled.wrappedBuffer(encode("{\"packet_sub_type\":\"${params[0]}\",\"packet_data\":\"${params[1]}\",\"packet_type\":\"${params[2]}\"}"))
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

    class VexViewPacketReader(val json: String) {
        val packetSubType: String
        val packetType: String
        val packetData: JsonElement?
        val buttonList: HashMap<String, String> = hashMapOf()

        init {
            val jsonObject = jsonParser.parse(json).asJsonObject
            packetSubType = jsonObject.get("packet_sub_type").asString
            packetType = jsonObject.get("packet_type").asString
            packetData = run {
                val data = jsonObject.get("packet_data")
                try {
                    return@run jsonParser.parse(data.asString).asJsonObject
                } catch (e: Exception) {
                    return@run data
                }
            }
            if (packetData is JsonObject) {
                ClientUtils.logger.info("Processing packet data...")
                val data = packetData.entrySet()
                for (it in data) {
                    when (it.key) {
                        "base" -> {
                            val elements = it.value.asString.split("<#>")
                            var index = 0
                            while (index < elements.size) {
                                if (elements[index].contains("[but]")) {
                                    val sub = elements[index].split("<&>")
                                    buttonList[sub[0].drop(5)] = sub[6]
                                }
                                index++
                            }
                        }
                        "scrollinglist" -> {

                        }
                    }
                }
            }
        }
    }
}