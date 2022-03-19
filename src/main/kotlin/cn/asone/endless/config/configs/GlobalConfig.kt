package cn.asone.endless.config.configs

import cn.asone.endless.config.AbstractConfig
import cn.asone.endless.config.ConfigManager
import cn.asone.endless.features.special.FakeForge
import cn.asone.endless.ui.font.Fonts
import cn.asone.endless.utils.ClientUtils
import cn.asone.endless.value.AbstractValue
import cn.asone.endless.value.ValueRegister
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import net.minecraft.client.gui.GuiMultiplayer
import viamcp.ViaMCP
import java.io.File

class GlobalConfig : AbstractConfig(File(ConfigManager.rootDir, "global.json")) {
    private val objectRegister: HashMap<String, ValueRegister> = hashMapOf(
        Pair("FakeForge", FakeForge),
        Pair("CustomFont", Fonts)
    )
    private val staticRegister: ArrayList<AbstractValue<*>> = arrayListOf(
        ViaMCP.getInstance().versionValue,
        GuiMultiplayer.authType
    )

    override fun configParser(entry: Map.Entry<String, JsonElement>) {
        if (objectRegister[entry.key] != null) {
            for (jsonValue in entry.value.asJsonObject.entrySet()) {
                val value = objectRegister[entry.key]!!.getValue(jsonValue.key)
                if (value == null)
                    ClientUtils.logger.error("找不到 ${entry.key} 的选项 ${jsonValue.key}.跳过此选项的解析.")
                else
                    value.fromJson(jsonValue.value)
            }
        } else {
            for (value in staticRegister) {
                if (value.name == entry.key) {
                    value.fromJson(entry.value)
                    return
                }
            }
            ClientUtils.logger.error("找不到 ${entry.key}. 跳过此类的解析.")
        }
    }

    override fun serializer(): JsonObject {
        val jsonObject = JsonObject()
        for (entry in objectRegister) {
            val subObject = JsonObject()
            for (value in entry.value.values)
                subObject.add(value.name, value.toJson())
            jsonObject.add(entry.key, subObject)
        }
        for (entry in staticRegister) {
            jsonObject.add(entry.name, entry.toJson())
        }
        return jsonObject
    }
}