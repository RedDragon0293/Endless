package cn.asone.endless.config

import cn.asone.endless.utils.ClientUtils
import cn.asone.endless.utils.io.FileUtils
import com.google.gson.*
import java.io.*

abstract class AbstractConfig(val file: File) {
    private val prettyGSON: Gson = GsonBuilder().setPrettyPrinting().create()

    @Throws(IOException::class)
    fun loadConfig() {
        if (!file.exists()) {
            ClientUtils.logger.warn("配置文件 ${file.name} 不存在. 创建新的配置文件.")
            file.createNewFile()
            saveConfig()
            return
        }
        lateinit var jsonElement: JsonElement
        lateinit var entryIterator: Iterator<Map.Entry<String, JsonElement>>
        lateinit var reader: BufferedReader
        runCatching {
            reader = BufferedReader(FileReader(file))
            jsonElement = JsonParser().parse(reader)
            entryIterator = jsonElement.asJsonObject.entrySet().iterator()
        }.onFailure {
            ClientUtils.logger.error("无法加载配置文件 ${file.name}!")
            it.printStackTrace()
            reader.close()
            FileUtils.createBackupFile(file)
            saveConfig()
            return
        }.onSuccess {
            while (entryIterator.hasNext()) {
                val entry = entryIterator.next()
                configParser(entry)
            }
            reader.close()
            ClientUtils.logger.info("成功加载配置文件 ${file.name}.")
        }
    }

    @Throws(IOException::class)
    fun saveConfig() {
        val printWriter = PrintWriter(FileWriter(file))
        printWriter.println(prettyGSON.toJson(serializer()))
        printWriter.close()
    }

    /**
     * @param entry 格式："Name" : {"JsonElement"}
     */
    abstract fun configParser(entry: Map.Entry<String, JsonElement>)

    abstract fun serializer(): JsonObject
}

