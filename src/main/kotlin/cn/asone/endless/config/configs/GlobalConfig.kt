package cn.asone.endless.config.configs

import cn.asone.endless.config.AbstractConfig
import cn.asone.endless.config.ConfigManager
import cn.asone.endless.features.special.FakeForge
import cn.asone.endless.ui.font.Fonts
import cn.asone.endless.utils.ClientUtils
import cn.asone.endless.value.ValueRegister
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import viamcp.ViaMCP
import java.io.File

class GlobalConfig : AbstractConfig(File(ConfigManager.rootDir, "global.json")) {
    val register: HashMap<String, ValueRegister> = hashMapOf(
        Pair("FakeForge", FakeForge),
        Pair("CustomFont", Fonts),
        Pair("ViaVersion", ViaMCP.getInstance())
    )

    override fun configParser(entry: Map.Entry<String, JsonElement>) {
        if (register[entry.key] == null) {
            ClientUtils.logger.error("找不到 ${entry.key}. 跳过此类的解析.")
            return
        }
        for (jsonValue in entry.value.asJsonObject.entrySet()) {
            val value = register[entry.key]!!.getValue(jsonValue.key)
            if (value == null)
                ClientUtils.logger.error("找不到 ${entry.key} 的选项 ${jsonValue.key}.跳过此选项的解析.")
            else
                value.fromJson(jsonValue.value)
        }
    }

    override fun serializer(): JsonObject {
        val jsonObject = JsonObject()
        for (entry in register) {
            val subObject = JsonObject()
            for (value in entry.value.values)
                subObject.add(value.name, value.toJson())
            jsonObject.add(entry.key, subObject)
        }
        return jsonObject
    }
}