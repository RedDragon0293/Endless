package cn.asone.endless.features.command.commands

import cn.asone.endless.features.command.AbstractCommand
import cn.asone.endless.utils.ClientUtils
import cn.asone.endless.utils.extensions.mc

class CommandSession : AbstractCommand("session") {
    override fun onExecute(command: String) {
        ClientUtils.displayChatMessage(mc.session.sessionID)
    }
}