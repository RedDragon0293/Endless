package cn.asone.endless.config.configs

import cn.asone.endless.config.AbstractConfig
import cn.asone.endless.config.ConfigManager
import cn.asone.endless.features.special.FakeForge
import cn.asone.endless.utils.ClientUtils
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.io.File

class GlobalConfig : AbstractConfig(File(ConfigManager.rootDir, "global.json")) {
    override fun configParser(entry: Map.Entry<String, JsonElement>) {
        when (entry.key) {
            "FakeForge" -> {
                for (jsonValue in entry.value.asJsonObject.entrySet()) {
                    val value = FakeForge.getValue(jsonValue.key)
                    if (value == null)
                        ClientUtils.logger.error("找不到 ${entry.key} 的选项 ${jsonValue.key}.跳过此选项的解析.")
                    else
                        value.fromJson(jsonValue.value)
                }
            }
        }
    }

    override fun serializer(): JsonObject {
        val jsonObject = JsonObject()
        val fakeForgeObject = JsonObject()
        for (value in FakeForge.values)
            fakeForgeObject.add(value.name, value.toJson())
        jsonObject.add("FakeForge", fakeForgeObject)
        return jsonObject
    }
}