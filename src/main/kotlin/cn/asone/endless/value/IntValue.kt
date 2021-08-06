package cn.asone.endless.value

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive

open class IntValue(name: String, value: Int, val range: IntRange = 0..Int.MAX_VALUE) :
    AbstractValue<Int>(name, value) {

    fun set(newValue: Number) {
        super.set(newValue.toInt())
    }

    override fun toJson() = JsonPrimitive(value)

    override fun fromJson(element: JsonElement) {
        if (element.isJsonPrimitive)
            value = element.asInt
        else
            super.fromJson(element)
    }
}