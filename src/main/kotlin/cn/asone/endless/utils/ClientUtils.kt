package cn.asone.endless.utils

import cn.asone.endless.Endless
import net.minecraft.util.ChatComponentText
import org.apache.logging.log4j.LogManager

object ClientUtils {
    @JvmField
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

    /**
     * 颜色代码：c
     * 颜色：0xFF5555 (Red)
     */
    @JvmStatic
    fun chatError(message: String) {
        mc.thePlayer.addChatMessage(ChatComponentText("$clientLoggerPrefix §c$message"))
    }

    /**
     * 颜色代码：3
     * 颜色：0x00AAAA (DarkAqua)
     */
    @JvmStatic
    fun chatInfo(message: String) {
        mc.thePlayer.addChatMessage(ChatComponentText("$clientLoggerPrefix §3$message"))
    }

    /**
     * 颜色代码：a
     * 颜色：0x55FF55 (Green)
     */
    @JvmStatic
    fun chatSuccess(message: String) {
        mc.thePlayer.addChatMessage(ChatComponentText("$clientLoggerPrefix §a$message"))
    }
}