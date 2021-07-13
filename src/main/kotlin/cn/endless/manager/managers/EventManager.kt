package cn.endless.manager.managers

import cn.endless.event.Event
import cn.endless.event.EventHook
import cn.endless.manager.Manager
import java.util.*

object EventManager : Manager<Event>("Event") {
    private val eventHashMap = hashMapOf<Class<Event>, TreeSet<EventHook>>()

    @JvmStatic
    fun <T : Event> callEvent(event: T): T {
        eventHashMap[event.javaClass]?.forEach {
            if (it.eventListener.isListening)
                it.eventHandler(event)
            if (event.isCancelled) return event
        }
        return event
    }

    fun register(clazz: Class<Event>, hook: EventHook) {
        if (eventHashMap[clazz] == null)
            eventHashMap[clazz] = TreeSet { a, b -> a.priority.compareTo(b.priority) }
        eventHashMap[clazz]!!.add(hook)
    }

    fun unregister(clazz: Class<Event>, hook: EventHook) {
        eventHashMap[clazz]?.remove(hook)
    }
}