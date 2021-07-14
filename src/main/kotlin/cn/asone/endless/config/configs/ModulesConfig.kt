package cn.asone.endless.config.configs

import cn.asone.endless.config.AbstractConfig
import cn.asone.endless.config.ConfigManager
import cn.asone.endless.features.module.AbstractModule
import cn.asone.endless.features.module.ModuleManager
import cn.asone.endless.utils.ClientUtils
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.*

class ModulesConfig : AbstractConfig(File(ConfigManager.rootDir, "modules.json")) {
    override fun loadConfig() {
        lateinit var jsonElement: JsonElement
        lateinit var entryIterator: Iterator<Map.Entry<String, JsonElement>>
        runCatching {
            jsonElement = JsonParser().parse(BufferedReader(FileReader(file)))
            entryIterator = jsonElement.asJsonObject.entrySet().iterator()
        }.onFailure {
            ClientUtils.logger.error("无法加载功能配置文件!")
            it.printStackTrace()
            if (file.exists())
                file.renameTo(File(ConfigManager.rootDir, "modules.json_bak"))
            saveConfig()
            return
        }.onSuccess {
            while (entryIterator.hasNext()) {
                val entry = entryIterator.next()
                val module: AbstractModule = ModuleManager.getModule(entry.key) ?: continue
                val jsonModule = entry.value as JsonObject
                module.state = jsonModule["State"].asBoolean
                module.keyBind = jsonModule["KeyBind"].asInt
            }
        }
    }

    override fun saveConfig() {
        val jsonObject = JsonObject()

        for (module in ModuleManager.modules) {
            val jsonParameter = JsonObject()
            jsonParameter.addProperty("State", module.state)
            jsonParameter.addProperty("KeyBind", module.keyBind)
            jsonObject.add(module.name, jsonParameter)
        }
        val printWriter = PrintWriter(FileWriter(file))
        printWriter.println(prettyGSON.toJson(jsonObject))
        printWriter.close()
    }
}