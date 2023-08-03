package cn.asone.endless.features.module

import cn.asone.endless.Endless
import cn.asone.endless.event.EventHook
import cn.asone.endless.event.ListenableClass
import cn.asone.endless.option.AbstractOption
import cn.asone.endless.option.OptionRegister
import cn.asone.endless.utils.extensions.playSound
import org.lwjgl.input.Keyboard

abstract class AbstractModule(
    @JvmField
    val name: String,
    @JvmField
    val description: String,
    @JvmField
    val category: Int,
    @JvmField
    var keyBind: Int = Keyboard.KEY_NONE,
    @JvmField
    var canEnable: Boolean = true
) : ListenableClass(), OptionRegister {
    var state = false
        set(value) {
            if (field == value)
                return
            if (Endless.inited) {
                mc.soundHandler.playSound("random.click", 1F)
            }
            if (value) {
                if (Endless.inited) {
                    onEnable()
                } else {
                    mc.moduleEventList.add { onEnable() }
                }
            } else {
                if (Endless.inited) {
                    onDisable()
                }
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

    abstract override val handledEvents: ArrayList<EventHook>

    abstract override val options: ArrayList<AbstractOption<*>>

    fun toggle() {
        state = !state
    }

    open fun tag(): String = ""

    open fun onEnable() {}
    open fun onDisable() {}

    override fun isHandlingEvents() = state
}