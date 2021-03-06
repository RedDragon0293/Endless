package cn.asone.endless.features.command.commands

import cn.asone.endless.features.command.AbstractCommand
import cn.asone.endless.features.module.ModuleManager
import cn.asone.endless.utils.ClientUtils

class CommandToggle : AbstractCommand("Toggle", "t") {
    override fun onExecute(command: String) {
        if (command == "") {
            chatSyntax(arrayOf("<功能>", "<功能> [on/off]"))
            return
        }
        val args = command.split(' ').toTypedArray()
        val module = ModuleManager.getModule(args[0])
        if (module == null) {
            ClientUtils.chatError("找不到功能 §9§l${command}§c.")
            return
        }
        if (args.size == 1) {
            module.toggle()
        } else {
            val newState = args[1].lowercase()
            if (newState == "on" || newState == "off")
                module.state = newState == "on"
            else {
                chatSyntax("${module.name} [on/off]")
                return
            }
        }
        if (module.canEnable)
            ClientUtils.chatSuccess("${if (module.state) "开启" else "关闭"}功能 §9§l${module.name}§a.")
    }
}