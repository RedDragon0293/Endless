package cn.asone.endless.event

/**
 * ListenableClass用此类申明需要注册一个事件
 */
class EventHook(val event: Class<out Event>, val priority: Int = 0) {
    constructor(event: Class<out Event>) : this(event, 0)
}

/**
 * EventManager用此类管理所有类提交的申请
 */
class EventTarget(val targetClass: Listenable, val priority: Int = 0)