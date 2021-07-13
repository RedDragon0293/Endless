package cn.endless.event

import cn.endless.features.Element

abstract class Event(name: String, private val cancelable: Boolean = false) : Element(name) {
    var isCancelled = false
        set(value) {
            if (!cancelable) println("$name is not cancelable!")
            else field = value
        }
}



