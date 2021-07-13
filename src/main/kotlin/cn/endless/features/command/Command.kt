package cn.endless.features.command

import cn.endless.features.Element
import cn.endless.manager.managers.CommandManager

open class Command(
        name: String,
        val description: String = "",
        open val onExecute: (String?) -> Unit = {},
        vararg subCommands: SubCommand
) : Element(name) {
    open val subCommands = subCommands.toMutableList()
    fun error(error: String) {
        CommandManager.chat("§4§lError§4: $error")
        CommandManager.hasError = true
    }
}