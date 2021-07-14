package cn.asone.endless.features.command.commands

import cn.asone.endless.features.command.Command
import cn.asone.endless.utils.ClientUtils

object TestCommand : Command("TestCommand", "Test", "TestC") {
    override fun onExecute(command: String) {
        ClientUtils.displayChatMessage(command)
    }
}