package cn.asone.endless.utils.misc

class MSTimer(private var time: Long) {

    constructor() : this(System.currentTimeMillis())

    fun hasTimePassed(ms: Long): Boolean {
        return System.currentTimeMillis() >= time + ms
    }

    fun reset() {
        time = System.currentTimeMillis()
    }
}