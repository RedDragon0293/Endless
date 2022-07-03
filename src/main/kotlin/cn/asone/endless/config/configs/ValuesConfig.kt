package cn.asone.endless.config.configs

import cn.asone.endless.config.AbstractConfig
import cn.asone.endless.config.ConfigManager
import cn.asone.endless.features.module.ModuleManager
import cn.asone.endless.utils.ClientUtils
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.io.File

class ValuesConfig : AbstractConfig(File(ConfigManager.rootDir, "values.json")) {
    override fun configParser(entry: Map.Entry<String, JsonElement>) {
        val module = ModuleManager.getModule(entry.key)
        if (module == null) {
            ClientUtils.logger.warn("找不到功能 ${entry.key}. 跳过此功能的选项解析!")
            return
        }
        /**
         * 格式：
         * {"ValueName": Value, "ValueName": Value}
         */
        val jsonModule = entry.value as JsonObject
        /*
         *  jsonValue格式:
         *  "ValueName": Value
         */
        for (jsonValue in jsonModule.entrySet()) {
            val value = module.getOption(jsonValue.key)
            if (value == null) {
                ClientUtils.logger.error("找不到功能 ${entry.key} 的选项 ${jsonValue.key}.跳过此选项的解析.")
            } else
                value.fromJson(jsonValue.value)
        }
    }

    override fun serializer(): JsonObject {
        val jsonObject = JsonObject()

        for (module in ModuleManager.modules) {
            if (module.options.isNotEmpty()) {
                val jsonModule = JsonObject()
                for (value in module.options) {
                    jsonModule.add(value.name, value.toJson())
                }
                jsonObject.add(module.name, jsonModule)
            }
        }
        return jsonObject
    }
}