package cn.asone.endless.config

import cn.asone.endless.Endless
import cn.asone.endless.config.configs.GlobalConfig
import cn.asone.endless.config.configs.ModulesConfig
import cn.asone.endless.config.configs.ValuesConfig
import cn.asone.endless.features.module.modules.render.ModuleHUD
import cn.asone.endless.utils.ClientUtils
import cn.asone.endless.utils.mc
import java.io.File

object ConfigManager {
    val rootDir = File(mc.mcDataDir, "${Endless.CLIENT_NAME}-${Endless.MINECRAFT_VERSION}").apply {
        if (!exists()) {
            ClientUtils.logger.warn("配置文件夹不存在. 创建新的配置文件夹.")
            ModuleHUD.state = true
            if (!mkdir())
                throw Exception("无法创建配置文件夹!")
        }
    }
    private val configs: MutableList<AbstractConfig> = mutableListOf()

    init {
        configs.add(ModulesConfig())
        configs.add(ValuesConfig())
        configs.add(GlobalConfig())
    }

    fun loadConfig(configFile: String) = configs.find { it.file == File(rootDir, configFile) }?.loadConfig()

    fun loadAllConfigs() = configs.forEach {
        it.loadConfig()
    }

    fun saveAllConfigs() = configs.forEach {
        it.saveConfig()
    }
}