package cn.asone.endless

import cn.asone.endless.config.ConfigManager
import cn.asone.endless.event.EventManager
import cn.asone.endless.features.command.CommandManager
import cn.asone.endless.features.module.ModuleManager
import cn.asone.endless.script.ScriptManager
import cn.asone.endless.ui.font.Fonts
import cn.asone.endless.ui.gui.clickgui.GuiClickGUI
import com.google.common.util.concurrent.ThreadFactoryBuilder
import net.minecraft.client.main.Main
import org.apache.logging.log4j.LogManager
import org.lwjgl.opengl.Display
import viamcp.ViaMCP
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

object Endless {
    const val CLIENT_NAME = "Endless"
    const val CLIENT_VERSION = "Beta 0.1.0"
    const val MINECRAFT_VERSION = "1.8.9"
    lateinit var clickGUI: GuiClickGUI

    @JvmField
    var inboundBytes = 0

    @JvmField
    var outboundBytes = 0

    @JvmField
    var lastInbound = 0

    @JvmField
    var lastOutbound = 0

    @JvmField
    var inited = false

    @JvmField
    var disableVia = false

    val logger = LogManager.getLogger(CLIENT_NAME)!!

    fun startClient() {
        logger.info("正在启动 $CLIENT_NAME $CLIENT_VERSION...")
        val i = System.currentTimeMillis()
        val factory = ThreadFactoryBuilder()
            .setDaemon(true)
            .setNameFormat("Endless init thread #%d")
            .build()
        val asyncExec = Executors.newFixedThreadPool(5, factory)
        val viaFuture = CompletableFuture.runAsync({
            if (!disableVia) {
                try {
                    logger.info("正在加载 ViaVersion...")
                    ViaMCP.getInstance()!!.start()
                    ViaMCP.getInstance()!!.initAsyncSlider(120, 8, 110, 20)
                } catch (e: Exception) {
                    logger.error("无法加载 ViaVersion!")
                    e.printStackTrace()
                }
            }
        }, asyncExec)

        val initFuture = CompletableFuture.runAsync({
            ConfigManager
            EventManager
        }, asyncExec).thenRunAsync({
            CommandManager
            ModuleManager
            EventManager.sort()
        }, asyncExec)
        val scriptFuture = CompletableFuture.runAsync({
            ScriptManager.loadAllScripts()
        }, asyncExec)
        val configLoadingFuture = CompletableFuture.runAsync({
            ConfigManager.loadAllConfigs()
        }, asyncExec)
        val clickGUIFuture = CompletableFuture.runAsync({
            clickGUI = GuiClickGUI()
        }, asyncExec)

        Fonts.downloadFonts()
        Fonts.loadFonts()

        while (!(initFuture.isDone && scriptFuture.isDone && configLoadingFuture.isDone && clickGUIFuture.isDone && viaFuture.isDone)) {
            Thread.sleep(100L)
        }

        Display.setTitle("$CLIENT_NAME $CLIENT_VERSION | 1.8.9 - Cracked by AsOne & RedDragon0293")
        inited = true
        asyncExec.shutdown()
        System.gc()
        logger.info("成功加载 $CLIENT_NAME, 用时 ${(System.currentTimeMillis() - i) / 1000F}秒.")
    }

    fun stopClient() {
        ConfigManager.saveAllConfigs()
        Main.safelyQuit = true
    }
}