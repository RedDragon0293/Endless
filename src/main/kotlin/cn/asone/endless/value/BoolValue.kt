package cn.asone.endless.value

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive

open class BoolValue(name: String, value: Boolean) : AbstractValue<Boolean>(name, value) {
    var subValue: ArrayList<AbstractValue<*>> = arrayListOf()

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
            set(element.asBoolean || element.asString.equals("true", ignoreCase = true))
        else if (element is JsonObject) { //有SubValue
            val jsonValue: JsonElement? = element["value"]
            if (jsonValue != null) { //有没有保存父value的值
                if (jsonValue.isJsonPrimitive) {
                    set(jsonValue.asBoolean || jsonValue.asString.equals("true", ignoreCase = true))
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