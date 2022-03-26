package cn.asone.endless.config.configs

import cn.asone.endless.config.AbstractConfig
import cn.asone.endless.config.ConfigManager
import cn.asone.endless.ui.font.Fonts
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import java.io.File

class FontCacheConfig : AbstractConfig(File(ConfigManager.rootDir, "fontCache.json")) {
    override fun configParser(entry: Map.Entry<String, JsonElement>) {
        val array = entry.value.asJsonArray
        for ((i, bool) in array.withIndex()) {
            Fonts.caches[i] = bool.asBoolean
        }
    }

    override fun serializer(): JsonObject {
        val jsonObject = JsonObject()
        val jsonArray = JsonArray()
        for (i in Fonts.caches) {
            jsonArray.add(JsonPrimitive(i))
        }
        jsonObject.add("caches", jsonArray)
        return jsonObject
    }

}