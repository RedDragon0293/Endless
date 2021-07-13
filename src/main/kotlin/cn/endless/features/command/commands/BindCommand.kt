package cn.endless.features.command.commands

import cn.endless.features.command.Command
import cn.endless.features.command.SubCommand
import cn.endless.features.module.Module
import cn.endless.manager.managers.ModuleManager
import cn.endless.utils.io.chat

object BindCommand : Command(
        name = "Bind",
        description = "Bind a module to a key."
) {
    init {
        var module: Module? = null
        subCommands.add(
                SubCommand(
                        name = "Module",
                        {
                            module = ModuleManager[it!!]
                            if (module == null)
                                error("§cModule §a§l$it§c not found.")
                        },
                        SubCommand(
                                name = "Key",
                                {
                                    val key = it!!
                                    runCatching {
                                        chat("Bound ${module!!.name} to key $key")
                                    }.onFailure {
                                        error("§cKey §a§l$key§c not found.")
                                    }
                                }
                        )
                )
        )
    }
}