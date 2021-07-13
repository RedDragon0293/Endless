package cn.endless.event

class EventHook(val eventListener: EventListener, val eventHandler: EventHandler<Event>, val priority: Int = 0)