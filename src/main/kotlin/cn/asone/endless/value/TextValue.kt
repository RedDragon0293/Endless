package cn.asone.endless.value

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive

open class TextValue(name: String, value: String) : AbstractValue<String>(name, value) {

    override fun toJson() = JsonPrimitive(value)

    override fun fromJson(element: JsonElement) {
        if (element.isJsonPrimitive)
            set(element.asString)
        else
            super.fromJson(element)
    }
}