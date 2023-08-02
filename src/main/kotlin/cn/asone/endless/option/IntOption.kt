package cn.asone.endless.option

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive

open class IntOption(name: String, value: Int, val range: IntRange = 0..Int.MAX_VALUE) :
    AbstractOption<Int>(name, value) {
    constructor(name: String, value: Int, min: Int, max: Int) : this(name, value, min..max)

    fun set(newValue: Number) {
        super.set(newValue.toInt())
    }

    override fun toJson() = JsonPrimitive(value)

    override fun fromJson(element: JsonElement) {
        if (element.isJsonPrimitive)
            set(element.asInt)
        else
            super.fromJson(element)
    }
}