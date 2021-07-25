package cn.asone.endless.features.command

import cn.asone.endless.utils.ClientUtils
import cn.asone.endless.utils.mc
import cn.asone.endless.utils.playSound
import java.util.*

abstract class AbstractCommand(val name: String, vararg val alias: String) {
    abstract fun onExecute(command: String)

    //open fun tabComplete(args: Array<String>): List<String> = emptyList()
    protected fun chatSyntax(syntax: String) = ClientUtils.chatInfo(
        "用法: §7${CommandManager.prefix}$name ${syntax.lowercase(Locale.getDefault())}"
    )

    protected fun chatSyntax(syntaxes: Array<String>) {
        ClientUtils.chatInfo("用法: ")

        for (syntax in syntaxes)
            ClientUtils.displayChatMessage("§8> §7${CommandManager.prefix}$name $syntax")
    }

    protected fun playEditSound() = mc.soundHandler.playSound("random.anvil_use", 1F)
}