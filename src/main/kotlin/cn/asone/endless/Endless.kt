package cn.asone.endless

import cn.asone.endless.config.ConfigManager
import cn.asone.endless.event.EventManager
import cn.asone.endless.features.command.CommandManager
import cn.asone.endless.features.module.ModuleManager
import cn.asone.endless.ui.font.Fonts
import cn.asone.endless.ui.gui.clickgui.GuiClickGUI
import net.minecraft.client.main.Main
import org.apache.logging.log4j.LogManager
import org.lwjgl.opengl.Display

object Endless {
    const val CLIENT_NAME = "Endless"
    const val CLIENT_VERSION = "Beta 1.0"
    const val MINECRAFT_VERSION = "1.8.9"
    lateinit var clickGUI: GuiClickGUI

    val logger = LogManager.getLogger(CLIENT_NAME)!!

    fun startClient() {
        logger.info("正在启动 $CLIENT_NAME $CLIENT_VERSION...")

        ConfigManager
        Fonts
        ConfigManager.loadConfig("global.json")
        Fonts.loadFonts()
        EventManager
        CommandManager
        ModuleManager
        EventManager.sort()
        clickGUI = GuiClickGUI()
        ConfigManager.loadAllConfigs()

        Display.setTitle("$CLIENT_NAME $CLIENT_VERSION | 1.8.9 - Cracked by AsOne & RedDragon0293")
        logger.info("成功加载 $CLIENT_NAME!")
    }

    fun stopClient() {
        ConfigManager.saveAllConfigs()
        Main.safelyQuit = true
    }
}