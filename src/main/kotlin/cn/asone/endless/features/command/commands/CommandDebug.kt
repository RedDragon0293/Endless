package cn.asone.endless.features.command.commands

import cn.asone.endless.features.command.AbstractCommand
import cn.asone.endless.features.module.modules.misc.ModuleHYTParty

class CommandDebug : AbstractCommand("debug") {
    override fun onExecute(command: String) {
        ModuleHYTParty.sendDebugPacket(command)
    }
}