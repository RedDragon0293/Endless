package cn.asone.endless.features.command.commands

import cn.asone.endless.features.command.AbstractCommand
import cn.asone.endless.features.module.ModuleManager
import cn.asone.endless.utils.ClientUtils
import org.lwjgl.input.Keyboard

class CommandBind : AbstractCommand("bind") {
    override fun onExecute(command: String) {
        val args = command.split(' ').toTypedArray()
        if (args.isEmpty() || (args.size == 1 && args[0] == "")) {
            chatSyntax(arrayOf("<功能> <快捷键>", "<功能> none"))
            return
        }
        val module = ModuleManager.getModule(args[0])
        if (module == null) {
            ClientUtils.chatError("找不到功能 §9§l${args[0]}§c.")
            return
        }
        if (args.size == 1) {
            chatSyntax(arrayOf("${module.name} <key>", "${module.name} none"))
            return
        }
        module.keyBind = Keyboard.getKeyIndex(args[1].toUpperCase())
        ClientUtils.chatSuccess("绑定快捷键 §9§l${Keyboard.getKeyName(module.keyBind)} §a至功能 §9§l${module.name}§a.")
        playEditSound()
    }
}