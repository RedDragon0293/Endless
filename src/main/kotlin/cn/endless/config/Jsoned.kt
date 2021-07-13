package cn.endless.config

import kotlinx.serialization.json.JsonElement


interface Jsoned {
    var parent: Jsoned?
    var map: MutableMap<String, JsonElement>

    fun writeTo(parent: Jsoned?)
    fun readFrom(parent: Jsoned?)
}
