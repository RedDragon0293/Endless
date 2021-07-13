package cn.endless.manager

import cn.endless.features.Element
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger


abstract class Manager<E : Element>(name: String) {

    private val name = "${name}Manager"
    val elements = mutableSetOf<E>()
    val logger: Logger = LogManager.getLogger(this.name)

    fun chat(message: String) = cn.endless.utils.io.chat("§2[§o$name§2] §f$message")

    fun error(message: String) = chat("§4§lError§4: §4$message")

    operator fun get(name: String) = elements.find { it.name.equals(name, ignoreCase = true) }

    fun add(vararg element: E) = elements.addAll(element)

    fun remove(vararg element: E) = elements.removeAll(element)

    fun clear() = elements.clear()

}