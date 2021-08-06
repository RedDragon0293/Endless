package cn.asone.endless.value

import cn.asone.endless.utils.ClientUtils
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import java.util.*

open class ListValue(name: String, val values: Array<String>, value: String) : AbstractValue<String>(name, value) {
    var subValue: MutableMap<String, ArrayList<AbstractValue<*>>> = mutableMapOf()

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
        ClientUtils.logger.error("选项 $name 不支持设置值为 $newValue. 此值将被抛弃.")
    }

    override fun toJson(): JsonElement? {
        return if (subValue.any { it.value.isNotEmpty() }) {
            val jsonObject = JsonObject()
            jsonObject.addProperty("value", value)
            val subObject = JsonObject()
            subValue.forEach {
                if (it.value.isNotEmpty()) {
                    val object2 = JsonObject()
                    it.value.forEach { sub ->
                        object2.add(sub.name, sub.toJson())
                    }
                    subObject.add(it.key, object2)
                }
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