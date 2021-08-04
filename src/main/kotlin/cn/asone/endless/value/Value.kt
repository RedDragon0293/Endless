package cn.asone.endless.value

import cn.asone.endless.utils.ClientUtils
import com.google.gson.JsonElement
import com.google.gson.JsonObject
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

    override fun toJson(): JsonElement? {
        return if (subValue.isEmpty()) {
            JsonPrimitive(value)
        } else {
            val jsonObject = JsonObject()
            jsonObject.addProperty("value", value)
            val subObject = JsonObject()
            for (sub in subValue)
                subObject.add(sub.name, sub.toJson())
            jsonObject.add("subValues", subObject)
            jsonObject
        }
    }

    override fun fromJson(element: JsonElement) {
        //没有subValue或丢失subValue数据
        if (element.isJsonPrimitive)
            value = element.asBoolean || element.asString.equals("true", ignoreCase = true)
        else if (element is JsonObject) { //有SubValue
            val jsonValue: JsonElement? = element["value"]
            if (jsonValue != null) { //有没有保存父value的值
                if (jsonValue.isJsonPrimitive) {
                    value = jsonValue.asBoolean || jsonValue.asString.equals("true", ignoreCase = true)
                } else
                    super.fromJson(jsonValue)
            }
            //无论有没有保存父value的值都要解析subValue
            if (subValue.isNotEmpty()) {
                val subJsonValue: JsonElement? = element["subValues"]
                if (subJsonValue != null) { //是否保存subValue
                    if (subJsonValue is JsonObject) { //是否正确保存subValue
                        /**
                         * 循环配置文件里的subValues
                         * 格式: "SubValueName": Value(JsonPrimitive)
                         */
                        for (sub in subJsonValue.entrySet()) {
                            /**
                             * 循环所有subValue
                             */
                            for (subValue_ in subValue) {
                                if (subValue_.name.equals(sub.key, true))
                                    subValue_.fromJson(sub.value)
                            }
                        }
                    }
                }
            }
        } else //啥也不是
            super.fromJson(element)
    }
}

open class IntValue(name: String, value: Int, val range: IntRange = 0..Int.MAX_VALUE) :
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

open class FloatValue(name: String, value: Float, val range: ClosedRange<Float> = 0F..Float.MAX_VALUE) :
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

//open class BlockValue(name: String, value: Int) : IntValue(name, value, 1..197)

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

    override fun toJson(): JsonElement? {
        return if (subValue.any { it.value.isNotEmpty() }) {
            val jsonObject = JsonObject()
            jsonObject.addProperty("value", value)
            val subObject = JsonObject()
            subValue.forEach {
                val object2 = JsonObject()
                it.value.forEach { sub ->
                    object2.add(sub.name, sub.toJson())
                }
                subObject.add(it.key, object2)
            }
            jsonObject.add("subValues", subObject)
            jsonObject
        } else JsonPrimitive(value)
    }

    override fun fromJson(element: JsonElement) {
        if (element.isJsonPrimitive)
            changeValue(element.asString)
        else if (element is JsonObject) {
            val jsonValue: JsonElement? = element["value"]
            if (jsonValue != null) { //有没有保存父value的值
                if (jsonValue.isJsonPrimitive) {
                    changeValue(jsonValue.asString)
                } else
                    super.fromJson(jsonValue)
            }
            if (subValue.any { it.value.isNotEmpty() }) {
                val subJsonValue: JsonElement? = element["subValues"]
                if (subJsonValue != null) {
                    if (subJsonValue is JsonObject) { //是否正确保存subValue
                        /**
                         * 循环所有subValue
                         * 格式: "String"(One of [values]) : JsonObject
                         */
                        for (subEntry in subJsonValue.entrySet()) {
                            /**
                             * Key是否在 [values]里 && 对应JsonElement正确保存
                             */
                            if (subEntry.key in this && subEntry.value.isJsonObject) {
                                /**
                                 * 循环此key下的所有SubValue
                                 * 格式: "SubValueName" : JsonPrimitive
                                 */
                                for (subValue_ in (subEntry.value as JsonObject).entrySet()) {
                                    /**
                                     * 循环注册的SubValue
                                     */
                                    for (sub in subValue[subEntry.key]!!) {
                                        if (sub.name.equals(subValue_.key, true))
                                            sub.fromJson(subValue_.value)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else
            super.fromJson(element)
    }
}