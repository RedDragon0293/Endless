package cn.asone.endless

import cn.asone.endless.event.EventManager
import cn.asone.endless.features.command.CommandManager
import cn.asone.endless.features.module.ModuleManager
import cn.asone.endless.features.module.modules.misc.TestModule
import org.apache.logging.log4j.LogManager
import org.lwjgl.opengl.Display

object Endless {
    const val CLIENT_NAME = "Endless"
    private const val CLIENT_VERSION = "Beta 1.0"

    val logger = LogManager.getLogger(CLIENT_NAME)!!

    fun startClient() {
        logger.info("Launching $CLIENT_NAME $CLIENT_VERSION...")
        EventManager
        CommandManager
        ModuleManager

        TestModule.state = true
        logger.info("Successfully loaded!")
        Display.setTitle("$CLIENT_NAME $CLIENT_VERSION | 1.8.9 - Cracked by AsOne & RedDragon0293")
    }

    fun stopClient() {
        logger.debug("Shutting down the client!")
    }
}