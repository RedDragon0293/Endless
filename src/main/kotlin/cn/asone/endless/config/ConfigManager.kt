package cn.asone.endless.config

import cn.asone.endless.Endless
import cn.asone.endless.config.configs.ModulesConfig
import cn.asone.endless.utils.mc
import java.io.File

object ConfigManager {
    val rootDir = File(mc.mcDataDir, "${Endless.CLIENT_NAME}-${Endless.MINECRAFT_VERSION}").apply {
        if (!exists())
            if (!mkdir())
                throw Exception("无法创建配置文件夹!")
    }
    private val configs: MutableList<AbstractConfig> = mutableListOf()

    init {
        configs.add(ModulesConfig())
    }

    fun loadAllConfigs() = configs.forEach {
        it.loadConfig()
    }

    fun saveAllConfigs() = configs.forEach {
        it.saveConfig()
    }
}