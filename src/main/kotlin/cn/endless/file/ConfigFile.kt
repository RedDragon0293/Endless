package cn.endless.file

import cn.endless.features.Element
import cn.endless.manager.managers.FileManager
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import java.io.File

abstract class ConfigFile(parent: File, name: String) : Element(name) {
    private val map = mapOf<String, JsonElement>()
    private val jsonObject = JsonObject(map)
    private val file = File(parent, "$name.json").apply {

    }

    fun save() {
        file.printWriter().runCatching {
            close()
        }.onSuccess {
            FileManager.logger.info("Successfully saved ${file.name}")
        }.onFailure {
            FileManager.logger.warn("Failed to save ${file.name}")
            FileManager.logger.error(it)
        }
    }

    fun load() {
        val time = System.currentTimeMillis()
        println(time - System.currentTimeMillis())
    }
}