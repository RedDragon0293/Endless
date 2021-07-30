package cn.asone.endless.value

interface ValueRegister {
    val values: ArrayList<Value<*>>
    fun getAllValue(): ArrayList<Value<*>>

    fun getValue(value: String) = getAllValue().find { it.name.equals(value, true) }
}