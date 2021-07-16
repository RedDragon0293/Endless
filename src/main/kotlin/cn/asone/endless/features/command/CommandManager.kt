package cn.asone.endless.features.command

import cn.asone.endless.features.command.commands.CommandBind
import cn.asone.endless.features.command.commands.CommandToggle
import cn.asone.endless.features.module.AbstractModule
import cn.asone.endless.utils.ClientUtils

object CommandManager {
    val commands = mutableListOf<AbstractCommand>()
    private val moduleCommands = mutableListOf<ModuleCommand>()
    var prefix = "."

    init {
        arrayOf(
                CommandBind(),
                CommandToggle()
        ).forEach {
            commands.add(it)
        }
        ClientUtils.logger.info("成功初始化 ${commands.size} 个命令.")
    }

    fun registerModuleCommand(module: AbstractModule) {
        moduleCommands.add(ModuleCommand(module))
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
        for (command in moduleCommands) {
            if (command.name.equals(name, true)) {
                command.onExecute(
                        if (spaceIndex == -1)
                            ""
                        else
                            args.substring(args.indexOf(' ') + 1, args.length)
                )
                return
            }
        }

        ClientUtils.chatError("找不到命令. 输入 §f${prefix}help §c获取所有命令.")
    }
}