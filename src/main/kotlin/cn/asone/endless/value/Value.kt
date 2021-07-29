package cn.asone.endless.value

import cn.asone.endless.utils.ClientUtils
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import java.util.*

abstract class Value<T>(val name: String, protected var value: T) {
    open fun set(newValue: T) {
        if (newValue == value)
            return
        changeValue(newValue)
    }

    open fun get() = value

    protected open fun changeValue(newValue: T) {
        value = newValue
    }

    abstract fun toJson(): JsonElement?
    open fun fromJson(element: JsonElement) {
        ClientUtils.logger.error("在选项 $name 的配置文件中找到不可识别的值 ${element.asString}.跳过此选项的解析.")
    }
}

open class BoolValue(name: String, value: Boolean) : Value<Boolean>(name, value) {
    var subValue: ArrayList<Value<*>> = arrayListOf()

    override fun toJson() = JsonPrimitive(value)

    override fun fromJson(element: JsonElement) {
        if (element.isJsonPrimitive)
            value = element.asBoolean || element.asString.equals("true", ignoreCase = true)
        else
            super.fromJson(element)
    }
}

open class IntValue(name: String, value: Int, minValue: Int = 0, maxValue: Int = Integer.MAX_VALUE) :
    Value<Int>(name, value) {

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

open class FloatValue(name: String, value: Float, val minValue: Float = 0F, val maxValue: Float = Float.MAX_VALUE) :
    Value<Float>(name, value) {

    fun set(newValue: Number) {
        super.set(newValue.toFloat())
    }

    override fun toJson() = JsonPrimitive(value)

    override fun fromJson(element: JsonElement) {
        if (element.isJsonPrimitive)
            value = element.asFloat
        else
            super.fromJson(element)
    }
}

open class TextValue(name: String, value: String) : Value<String>(name, value) {

    override fun toJson() = JsonPrimitive(value)

    override fun fromJson(element: JsonElement) {
        if (element.isJsonPrimitive)
            value = element.asString
        else
            super.fromJson(element)
    }
}

/*class FontValue(valueName: String, value: FontRenderer) : Value<FontRenderer>(valueName, value) {

    override fun toJson(): JsonElement? {
        val fontDetails = Fonts.getFontDetails(value) ?: return null
        val valueObject = JsonObject()
        valueObject.addProperty("fontName", fontDetails.name)
        valueObject.addProperty("fontSize", fontDetails.fontSize)
        return valueObject
    }

    override fun fromJson(element: JsonElement) {
        if (!element.isJsonObject) return
        val valueObject = element.asJsonObject
        value = Fonts.getFontRenderer(valueObject["fontName"].asString, valueObject["fontSize"].asInt)
    }
}*/

open class BlockValue(name: String, value: Int) : IntValue(name, value, 1, 197)

open class ListValue(name: String, val values: Array<String>, value: String) : Value<String>(name, value) {
    var subValue: MutableMap<String, ArrayList<Value<*>>> = mutableMapOf()

    init {
        this.value = value
        for (currentValue in values)
            subValue[currentValue] = arrayListOf()
    }

    operator fun contains(string: String?): Boolean {
        return Arrays.stream(values).anyMatch { s: String -> s.equals(string, ignoreCase = true) }
    }

    override fun changeValue(newValue: String) {
        for (element in values) {
            if (element.equals(newValue, ignoreCase = true)) {
                this.value = element
                return
            }
        }
        ClientUtils.logger.error("在选项 $name 的配置文件中找到不存在的值 $newValue.跳过此选项的解析.")
    }

    override fun toJson() = JsonPrimitive(value)

    override fun fromJson(element: JsonElement) {
        if (element.isJsonPrimitive)
            changeValue(element.asString)
        else
            super.fromJson(element)
    }
}