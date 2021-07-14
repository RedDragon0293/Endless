package cn.asone.endless.features.command.commands

import cn.asone.endless.features.command.AbstractCommand
import cn.asone.endless.features.module.ModuleManager
import cn.asone.endless.utils.ClientUtils

object CommandToggle : AbstractCommand("Toggle", "t") {
    override fun onExecute(command: String) {
        val args = command.split(' ').toTypedArray()
        if (args.isEmpty() || (args.size == 1 && args[0] == "")) {
            chatSyntax(arrayOf("<功能>", "<功能> [on/off]"))
            return
        }
        val module = ModuleManager.getModule(args[0])
        if (module == null) {
            ClientUtils.chatError("找不到功能 §9§l${command}§c.")
            return
        }
        if (args.size == 1) {
            module.toggle()
        } else {
            val newState = args[1].toLowerCase()
            if (newState == "on" || newState == "off")
                module.state = newState == "on"
            else {
                chatSyntax("${module.name} [on/off]")
                return
            }
        }
        ClientUtils.chatSuccess("${if (module.state) "开启" else "关闭"}功能 §9§l${module.name}§a.")
    }
}