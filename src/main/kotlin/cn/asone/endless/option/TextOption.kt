package cn.asone.endless.option

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive

open class TextOption(name: String, value: String) : AbstractOption<String>(name, value) {

    override fun toJson() = JsonPrimitive(value)

    override fun fromJson(element: JsonElement) {
        if (element.isJsonPrimitive)
            set(element.asString)
        else
            super.fromJson(element)
    }
}