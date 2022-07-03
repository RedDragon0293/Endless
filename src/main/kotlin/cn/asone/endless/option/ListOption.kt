package cn.asone.endless.option

import cn.asone.endless.utils.ClientUtils
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import java.util.*

/**
 * @param availableValues 可选值
 * @param value 初始值
 */
open class ListOption(name: String, val availableValues: Array<String>, value: String) : AbstractOption<String>(name, value) {
    /**
     * subOptions 的类型为 Map, 其中 Map.Key 代表 ListOption 中 availableValues 中的一个选项, Map.Value 代表其对应的子选项
     */
    var subOptions: MutableMap<String, ArrayList<AbstractOption<*>>> = mutableMapOf()

    init {
        set(value)
        for (currentValue in availableValues)
            subOptions[currentValue] = arrayListOf()
    }

    operator fun contains(string: String?): Boolean {
        return Arrays.stream(availableValues).anyMatch { s: String -> s.equals(string, ignoreCase = true) }
    }

    override fun changeValue(newValue: String) {
        for (element in availableValues) {
            if (element.equals(newValue, ignoreCase = true)) {
                super.changeValue(element)
                return
            }
        }
        ClientUtils.logger.error("选项 $name 不支持设置值为 $newValue. 此值将被抛弃.")
    }

    override fun toJson(): JsonElement? {
        return if (subOptions.any { it.value.isNotEmpty() }) {
            val jsonObject = JsonObject()
            jsonObject.addProperty("value", value)
            val subObject = JsonObject()
            subOptions.forEach {
                if (it.value.isNotEmpty()) {
                    val object2 = JsonObject()
                    it.value.forEach { sub ->
                        object2.add(sub.name, sub.toJson())
                    }
                    subObject.add(it.key, object2)
                }
            }
            jsonObject.add("subOptions", subObject)
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
            if (subOptions.any { it.value.isNotEmpty() }) {
                val subJsonValue: JsonElement? = element["subOptions"]
                if (subJsonValue != null) {
                    if (subJsonValue is JsonObject) { //是否正确保存subOptions
                        /**
                         * 循环所有subOptions
                         * 格式: "String"(One of [availableValues]) : JsonObject
                         */
                        for (subEntry in subJsonValue.entrySet()) {
                            /**
                             * Key是否在 [availableValues]里 && 对应 JsonElement 正确保存
                             */
                            if (subEntry.key in this && subEntry.value.isJsonObject) {
                                /**
                                 * 循环此key下的所有SubOptions
                                 * 格式: "SubOptionName" : JsonPrimitive
                                 */
                                for (subValue_ in (subEntry.value as JsonObject).entrySet()) {
                                    /**
                                     * 循环注册的SubOption
                                     */
                                    for (sub in subOptions[subEntry.key]!!) {
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