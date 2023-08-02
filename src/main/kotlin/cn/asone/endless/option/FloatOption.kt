package cn.asone.endless.option

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive

open class FloatOption(name: String, value: Float, val range: ClosedRange<Float> = 0F..Float.MAX_VALUE) :
    AbstractOption<Float>(name, value) {
    constructor(name: String, value: Float, min: Float, max: Float) : this(name, value, min..max)

    fun set(newValue: Number) {
        super.set(newValue.toFloat())
    }

    override fun toJson() = JsonPrimitive(value)

    override fun fromJson(element: JsonElement) {
        if (element.isJsonPrimitive)
            set(element.asFloat)
        else
            super.fromJson(element)
    }
}