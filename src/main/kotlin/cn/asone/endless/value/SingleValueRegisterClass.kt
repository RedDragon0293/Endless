package cn.asone.endless.value

abstract class SingleValueRegisterClass : ValueRegister {
    abstract val singleValue: AbstractValue<*>
    override val values: ArrayList<AbstractValue<*>>
        get() = arrayListOf(singleValue)
}