package cn.asone.endless.event

class EventHook(val event: Class<out Event>, val priority: Int = 0)

class EventTarget(val targetClass: Listenable, val priority: Int = 0)