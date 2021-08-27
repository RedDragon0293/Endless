package cn.asone.endless.features.module

import cn.asone.endless.event.EventHook
import cn.asone.endless.event.ListenableClass
import cn.asone.endless.utils.playSound
import cn.asone.endless.value.AbstractValue
import cn.asone.endless.value.ValueRegister
import org.lwjgl.input.Keyboard

abstract class AbstractModule(
    val name: String,
    val description: String,
    val category: Int,
    var keyBind: Int = Keyboard.CHAR_NONE,
    var canEnable: Boolean = true
) : ListenableClass(), ValueRegister {
    var state = false
        set(value) {
            if (field == value)
                return
            if (mc.thePlayer != null && mc.theWorld != null) {
                mc.soundHandler.playSound("random.click", 1F)
                if (value)
                    onEnable()
                else
                    onDisable()
            }
            if (!canEnable) {
                field = false
                return
            }
            field = value
        }
    var arrayName = name
        get() {
            return if (this.tag() != "")
                field
            else
                "$field §7${tag()}"
        }

    override val handledEvents: ArrayList<EventHook> = arrayListOf()

    override val values: ArrayList<AbstractValue<*>> = arrayListOf()

    fun toggle() {
        state = !state
    }

    open fun tag(): String = ""

    open fun onEnable() {}
    open fun onDisable() {}

    override fun isHandleEvents() = state
}