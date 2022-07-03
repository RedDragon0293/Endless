package cn.asone.endless.option

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive

class ColorOption(name: String, value: Int) : AbstractOption<Int>(name, value) {
    fun set(red: Int, green: Int, blue: Int, alpha: Int) {
        super.set((alpha shl 24 and 0xFF) + (red shl 16 and 0xFF) + (green shl 8 and 0xFF) + blue and 0xFF)
    }

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