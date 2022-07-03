package cn.asone.endless.option

import cn.asone.endless.utils.ClientUtils
import com.google.gson.JsonElement

abstract class AbstractOption<T>(val name: String, value: T) {
    protected var value: T
        private set

    init {
        this.value = value
    }

    fun set(newValue: T) {
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

//open class BlockValue(name: String, value: Int) : IntOption(name, value, 1..197)
