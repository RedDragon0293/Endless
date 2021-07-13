package cn.endless.features.command.commands

import cn.endless.features.command.Command
import cn.endless.features.command.SubCommand
import cn.endless.manager.managers.ModuleManager

object ToggleCommand : Command(
        name = "Toggle",
        description = "Toggle a module"
) {
    init {
        subCommands.add(
                SubCommand(
                        name = "Module",
                        {
                            ModuleManager[it!!]?.toggle() ?: error("§cModule §a§l$it§c not found.")
                        }
                )
        )
    }
}