package cn.endless.config

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlin.reflect.KProperty

open class Value<T : Any>(
    open val name: String,
    internal var value: T,
) {
    lateinit var jsonElement: JsonElement
    lateinit var parent: JsonElement
    lateinit var serializer: KSerializer<T>
    operator fun getValue(u: Any?, property: KProperty<*>) = get()

    operator fun setValue(u: Any?, property: KProperty<*>, t: T) {
        set(t)
    }

    fun get() = value

    fun set(t: T) {
        value = t
    }

}

class IntValue(name: String, value: Int) : Value<Int>(name, value) {
    init {
        val json = Json {  }
        jsonElement = json.encodeToJsonElement(serializer, value)
        set(json.decodeFromJsonElement(serializer, jsonElement))
    }
}