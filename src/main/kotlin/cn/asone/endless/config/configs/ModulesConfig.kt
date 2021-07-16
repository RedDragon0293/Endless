package cn.asone.endless.config.configs

import cn.asone.endless.config.AbstractConfig
import cn.asone.endless.config.ConfigManager
import cn.asone.endless.features.module.AbstractModule
import cn.asone.endless.features.module.ModuleManager
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.io.File

class ModulesConfig : AbstractConfig(File(ConfigManager.rootDir, "modules.json")) {
    override fun configParser(entry: Map.Entry<String, JsonElement>) {
        val module: AbstractModule = ModuleManager.getModule(entry.key) ?: return
        val jsonModule = entry.value as JsonObject
        module.state = jsonModule["State"].asBoolean
        module.keyBind = jsonModule["KeyBind"].asInt
    }

    override fun serializer(): JsonObject {
        val jsonObject = JsonObject()

        for (module in ModuleManager.modules) {
            val jsonParameter = JsonObject()
            jsonParameter.addProperty("State", module.state)
            jsonParameter.addProperty("KeyBind", module.keyBind)
            jsonObject.add(module.name, jsonParameter)
        }
        return jsonObject
    }
}