package cn.asone.endless.features.command

import cn.asone.endless.utils.ClientUtils

abstract class Command(val name: String, vararg val alias: String) {
    abstract fun onExecute(command: String)

    //open fun tabComplete(args: Array<String>): List<String> = emptyList()
    protected fun chatSyntax(syntax: String) = ClientUtils.chatInfo("用法：§7${CommandManager.prefix}$name ${syntax.toLowerCase()}")

    protected fun chatSyntax(syntaxes: Array<String>) {
        ClientUtils.chatInfo("用法：")

        for (syntax in syntaxes)
            ClientUtils.displayChatMessage("§8> §7${CommandManager.prefix}$name $syntax")
    }
}