package cn.endless.features.module

import cn.endless.event.DisableModuleEvent
import cn.endless.event.EnableModuleEvent
import cn.endless.event.EventListener
import cn.endless.features.Element
import cn.endless.manager.managers.EventManager

abstract class Module(name: String) : Element(name), EventListener {
    var state = false
        set(value) {
            if (value != field) {
                if (value)
                    EventManager.callEvent(EnableModuleEvent(this))
                else
                    EventManager.callEvent(DisableModuleEvent(this))
            }
            field = value
        }

    fun toggle() {
        state = !state
    }
}