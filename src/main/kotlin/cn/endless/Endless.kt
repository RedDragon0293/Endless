package cn.endless

import cn.endless.event.EventListener
import cn.endless.manager.managers.CommandManager
import cn.endless.manager.managers.EventManager
import cn.endless.manager.managers.FileManager

object Endless : EventListener {
    @JvmField
    var started = false

    @JvmField
    var isStarting = false

    //Client info
    const val CLIENT_NAME = "Endless"
    const val VERSION_MAJOR = 1
    const val VERSION_MINOR = 0
    const val VERSION_PATCH = 0
    const val MINECRAFT_VERSION = "1.8.9"

    @JvmStatic
    fun startClient() {
        Thread {
            isStarting = true
            System.out.println("Starting Endless client.")
            EventManager
            CommandManager
            FileManager
            println("Started Endless client.")
            started = true
            isStarting = false
        }.start()
    }

    @JvmStatic
    fun chat(message: String) = cn.endless.utils.io.chat("§a[§l$CLIENT_NAME§a] §f$message")
}