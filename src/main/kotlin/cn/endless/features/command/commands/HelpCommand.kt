package cn.endless.features.command.commands

import cn.endless.features.command.Command
import cn.endless.features.command.ParameterType
import cn.endless.features.command.SubCommand
import cn.endless.manager.managers.CommandManager
import cn.endless.utils.io.chat
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

object HelpCommand : Command(
        name = "Help",
        description = "Show tips of commands."
) {
    init {
        var page: Int
        val count = 8
        subCommands.add(
                SubCommand(
                        name = "Page",
                        parameterType = ParameterType.NULLABLE,
                        onExecute = { arg ->

                            val maxPage = ceil(CommandManager.elements.size.toDouble() / count).toInt()
                            page = max(1, min(arg?.toInt() ?: 1, maxPage))

                            CommandManager.chat("§c§lCommands:")

                            chat(" §9Page: < $page / $maxPage §9> ")

                            CommandManager.elements.filterIndexed { i, _ ->
                                (page - 1) * count <= i && i < page * count
                            }.forEach { command ->
                                chat("> .${command.name} ${
                                    if (command.subCommands.isNotEmpty())
                                        "<${command.subCommands.joinToString(separator = "/") { it.name }}>"
                                    else ""
                                } ${command.description}")
                            }
                        }
                )
        )
    }
}