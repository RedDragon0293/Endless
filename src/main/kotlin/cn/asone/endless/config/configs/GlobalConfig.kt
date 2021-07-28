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
            "FakeForge" -> FakeForge.enabled.set(entry.value.asBoolean)

            "FakeFML" -> FakeForge.fml.set(entry.value.asBoolean)

            "FakePayload" -> FakeForge.payload.set(entry.value.asBoolean)
        }
    }

    override fun serializer(): JsonObject {
        val jsonObject = JsonObject()
        jsonObject.addProperty("FakeForge", FakeForge.enabled.get())
        jsonObject.addProperty("FakeFML", FakeForge.fml.get())
        jsonObject.addProperty("FakePayload", FakeForge.payload.get())
        return jsonObject
    }
}