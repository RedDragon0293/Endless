package cn.asone.endless.value

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive

open class FloatValue(name: String, value: Float, val range: ClosedRange<Float> = 0F..Float.MAX_VALUE) :
    AbstractValue<Float>(name, value) {

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