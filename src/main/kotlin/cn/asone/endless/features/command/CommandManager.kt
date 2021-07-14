package cn.asone.endless.features.command

import cn.asone.endless.features.command.commands.BindCommand
import cn.asone.endless.features.command.commands.TestCommand
import cn.asone.endless.features.module.Module
import cn.asone.endless.utils.ClientUtils

object CommandManager {
    val commands = mutableListOf<Command>()
    var prefix = "."

    init {
        arrayOf(
                BindCommand,
                TestCommand
        ).forEach {
            commands.add(it)
        }
    }

    fun registerModuleCommand(module: Module) {
        commands.add(ModuleCommand(module))
    }

    /*
     * 举例：聊天栏输入
     * .bind KillAura r
     */
    fun executeCommand(args: String) {
        /**
         * args = "bind KillAura r"
         */

        /**
         * 分割命令名
         * name = "bind"
         */
        val spaceIndex = args.indexOf(' ')
        val name: String
        name = if (spaceIndex != -1)
            args.substring(0, spaceIndex)
        else
            args
        for (command in commands) {
            if (command.name.equals(name, true)) {
                /**
                 * onExecute("KillAura r")
                 */
                command.onExecute(
                        if (spaceIndex == -1)
                            ""
                        else
                            args.substring(args.indexOf(' ') + 1, args.length)
                )
                return
            }
            for (alias in command.alias) {
                if (alias.equals(name, true)) {
                    command.onExecute(
                            if (spaceIndex == -1)
                                ""
                            else
                                args.substring(args.indexOf(' ') + 1, args.length)
                    )
                    return
                }
            }
        }

        ClientUtils.chatError("找不到命令。输入 §f${prefix}help §c获取所有命令。")
    }
}