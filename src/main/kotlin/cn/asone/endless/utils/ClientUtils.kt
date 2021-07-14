package cn.asone.endless.utils

import cn.asone.endless.Endless
import net.minecraft.util.ChatComponentText
import org.apache.logging.log4j.LogManager

object ClientUtils : MinecraftInstance() {
    val logger = LogManager.getLogger("Endless")!!
    private const val clientLoggerPrefix = "§8[§9§l${Endless.CLIENT_NAME}§8]"

    @JvmStatic
    fun displayChatMessage(message: String) {
        if (mc.thePlayer == null) {
            logger.info("(MCChat)$message")
            return
        }
        mc.thePlayer.addChatMessage(ChatComponentText(message))
    }

    @JvmStatic
    fun chatError(message: String) {
        mc.thePlayer.addChatMessage(ChatComponentText("$clientLoggerPrefix §c$message"))
    }

    @JvmStatic
    fun chatInfo(message: String) {
        mc.thePlayer.addChatMessage(ChatComponentText("$clientLoggerPrefix §3$message"))
    }

    @JvmStatic
    fun chatSuccess(message: String) {
        mc.thePlayer.addChatMessage(ChatComponentText("$clientLoggerPrefix §a$message"))
    }
}