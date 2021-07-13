package cn.endless.manager.managers

import cn.endless.Endless
import cn.endless.file.ConfigFile
import cn.endless.manager.Manager
import cn.endless.utils.mc
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.io.File


object FileManager : Manager<ConfigFile>("File") {
    val dir = File(mc.mcDataDir, "${Endless.CLIENT_NAME}-${Endless.MINECRAFT_VERSION}").apply { if (!exists()) mkdir() }
    val file = File(dir, "Test.json").apply { if (!exists()) createNewFile() }

    init {
        val json = Json {
            prettyPrint = true
        }

        val map = mutableMapOf<String, JsonElement>()
        var a = JsonPrimitive(1);
        map["2"] = JsonObject(mapOf(Pair("123", a)))
        a = JsonPrimitive(true)
        map["1"] = JsonObject(mapOf(Pair("422", JsonPrimitive(23))))
        val jsonObject = JsonObject(map)
        map["7"] = JsonObject(mapOf(Pair("825", JsonPrimitive("Hello World"))))
        file.printWriter().run {
            println(json.encodeToString(jsonObject))
            close()
        }

//        map.clear()
//        json.decodeFromJsonElement<MutableMap<String, JsonElement>>(json.parseToJsonElement("{\n" +
//                "    \"6\": {\n" +
//                "        \"123\": false\n" +
//                "    },\n" +
//                "    \"1\": {\n" +
//                "        \"462\": 123\n" +
//                "    },\n" +
//                "    \"7\": {\n" +
//                "        \"125\": \"Hello World\"\n" +
//                "    }\n" +
//                "}\n").jsonObject).forEach {
//            map[it.key] = it.value
//        }

        file.printWriter().run {
            println(json.encodeToString(jsonObject))
            close()
        }
        println(map)
    }
}