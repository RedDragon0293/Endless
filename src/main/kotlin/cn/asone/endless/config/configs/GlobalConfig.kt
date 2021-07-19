package cn.asone.endless.config.configs

import cn.asone.endless.config.AbstractConfig
import cn.asone.endless.config.ConfigManager
import cn.asone.endless.features.special.FakeForge
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.io.File

class GlobalConfig : AbstractConfig(File(ConfigManager.rootDir, "global.json")) {
    override fun configParser(entry: Map.Entry<String, JsonElement>) {
        when (entry.key) {
            "FakeForge" -> FakeForge.enabled = entry.value.asBoolean

            "FakeFML" -> FakeForge.fml = entry.value.asBoolean

            "FakePayload" -> FakeForge.payload = entry.value.asBoolean
        }
    }

    override fun serializer(): JsonObject {
        val jsonObject = JsonObject()
        jsonObject.addProperty("FakeForge", FakeForge.enabled)
        jsonObject.addProperty("FakeFML", FakeForge.fml)
        jsonObject.addProperty("FakePayload", FakeForge.payload)
        return jsonObject
    }
}