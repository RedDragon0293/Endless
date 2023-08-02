package cn.asone.endless.features.command

import cn.asone.endless.features.command.commands.CommandBind
import cn.asone.endless.features.command.commands.CommandDebug
import cn.asone.endless.features.command.commands.CommandSession
import cn.asone.endless.features.command.commands.CommandToggle
import cn.asone.endless.features.module.AbstractModule
import cn.asone.endless.utils.ClientUtils

object CommandManager {
    val commands = mutableListOf<AbstractCommand>()

    /**
     * 用于使用指令调整 module 的参数
     */
    private val moduleCommands = mutableListOf<ModuleCommand>()
    var prefix = "."

    init {
        arrayOf(
            CommandBind(),
            CommandToggle(),
            CommandDebug(),
            CommandSession()
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
    /**
     * 此方法在发包前hook, 参数为聊天栏输入的字符串, 而聊天栏的字符串在发包前已经将开头和结尾的空格删除, 故不用做特殊处理
     * @see java.lang.String.trim
     */
    fun executeCommand(args: String) {
        /*
         * args = "bind KillAura r"
         */

        /*
         * 分割命令名
         * name = "bind"
         */
        /**
         * 主要用于判断是否有参数
         */
        val spaceIndex = args.indexOf(' ')
        val name: String = if (spaceIndex != -1)
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
                        args.substring(spaceIndex + 1, args.length)
                )
                return
            }
            for (alias in command.alias) {
                if (alias.equals(name, true)) {
                    command.onExecute(
                        if (spaceIndex == -1)
                            ""
                        else
                            args.substring(spaceIndex + 1, args.length)
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
                        args.substring(spaceIndex + 1, args.length)
                )
                return
            }
        }

        ClientUtils.chatError("找不到命令. 输入 §f${prefix}help §c获取所有命令.")
    }
}