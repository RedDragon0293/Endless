package cn.asone.endless.option

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive

open class BoolOption(name: String, value: Boolean) : AbstractOption<Boolean>(name, value) {
    val subOptions: ArrayList<AbstractOption<*>> = arrayListOf()

    override fun toJson(): JsonElement? {
        return if (subOptions.isEmpty()) {
            JsonPrimitive(value)
        } else {
            /*
            有子选项时, 储存的格式为:
            value: ***,
            subOptions: {}
             */
            val jsonObject = JsonObject()
            jsonObject.addProperty("value", value)
            val subObject = JsonObject()
            for (sub in subOptions)
                subObject.add(sub.name, sub.toJson())
            jsonObject.add("subOptions", subObject)
            jsonObject
        }
    }

    override fun fromJson(element: JsonElement) {
        //没有subOptions或丢失subOptions数据
        if (element.isJsonPrimitive)
            set(element.asBoolean || element.asString.equals("true", ignoreCase = true))
        else if (element is JsonObject) { //有SubOption
            val jsonValue: JsonElement? = element["value"]
            if (jsonValue != null) { //有没有保存父value的值
                if (jsonValue.isJsonPrimitive) {
                    set(jsonValue.asBoolean || jsonValue.asString.equals("true", ignoreCase = true))
                } else
                    super.fromJson(jsonValue)
            }
            //无论有没有保存父value的值都要解析subOptions
            if (subOptions.isNotEmpty()) {
                val subJsonValue: JsonElement? = element["subOptions"]
                if (subJsonValue != null) { //是否保存subOptions
                    if (subJsonValue is JsonObject) { //是否正确保存subOptions
                        /**
                         * 循环配置文件里的subOptions
                         * 格式: "SubOptionName": Value(JsonPrimitive)
                         */
                        for (sub in subJsonValue.entrySet()) {
                            /**
                             * 循环所有subValue
                             */
                            for (subValue_ in subOptions) {
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