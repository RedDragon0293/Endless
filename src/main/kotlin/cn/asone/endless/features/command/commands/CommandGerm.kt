package cn.asone.endless.features.command.commands

import cn.asone.endless.features.command.AbstractCommand
import cn.asone.endless.features.module.modules.misc.ModuleHYTGermHandler

class CommandGerm : AbstractCommand("germ") {
    override fun onExecute(command: String) {
        val args = command.split(' ')

        ModuleHYTGermHandler.sendGermPacket(args[0].toInt(), args.drop(1))
    }
}