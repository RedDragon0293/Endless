package cn.endless.event

import cn.endless.manager.managers.EventManager

interface EventListener {
    val isListening: Boolean get() = parent?.isListening ?: true
    val parent: EventListener? get() = null
}

inline fun <reified T : Event> EventListener.handler(priority: Int = 0, noinline eventHandler: EventHandler<T>) {
    EventManager.register(T::class.java as Class<Event>, EventHook(this, eventHandler as EventHandler<Event>, priority))
}
