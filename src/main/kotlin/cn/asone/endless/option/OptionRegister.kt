package cn.asone.endless.option

interface OptionRegister {
    val options: ArrayList<AbstractOption<*>>
    //fun getAllValue(): ArrayList<Value<*>>

    fun getOption(value: String) = options.find { it.name.equals(value, true) }
}