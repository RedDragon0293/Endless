package cn.endless.manager.managers

import cn.endless.event.EventListener
import cn.endless.event.SendMessageEvent
import cn.endless.event.handler
import cn.endless.features.command.Command
import cn.endless.features.command.ParameterType
import cn.endless.features.command.commands.*
import cn.endless.manager.Manager

object CommandManager : Manager<Command>("Command"), EventListener {
    init {
        add(
            BindCommand,
            HelpCommand,
            ToggleCommand
        )
    }

    @JvmStatic
    val prefix = "."
    var hasError = false

    val chatHandler = handler<SendMessageEvent> { event ->
        if (event.message.startsWith(prefix) && event.addToChat) {
            event.isCancelled = true
            execute(event.message)
        }
    }

    private fun execute(string: String) {
        val args = string.removePrefix(prefix).split(" ")
        var command = this[args[0]]
        var arg: String?
        var i = 0
        var lastCommand = command
        var history = "."
        if (command == null) {
            error("Unknown command <§n${args[0]}§4>!")
        } else while (command != null) {
            /*参数*/
            arg = if (args.size > i) args[i] else null
            runCatching {
                command!!.onExecute(arg)
            }.onSuccess {
                if (hasError) {
                    hasError = false
                    return@execute
                }
                lastCommand = command
                history += "$arg "
            }.onFailure {
                if (lastCommand!!.subCommands.isNotEmpty()) {
                    chat("§3§lSyntax:")
                    chat("§7  $history<${lastCommand!!.subCommands.joinToString(separator = "/") { it.name }}>", false)
                    return@execute
                }
            }

            /*搜索下一级指令*/
            command = command.subCommands.find {
                it.name == arg && it.parameterType == ParameterType.OPTION ||
                        it.parameterType == ParameterType.NULLABLE ||
                        arg != null && it.parameterType == ParameterType.NON_NULL
            }
            i++
        }
    }
}