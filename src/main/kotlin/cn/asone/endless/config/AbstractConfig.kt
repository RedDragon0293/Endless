package cn.asone.endless.config

import cn.asone.endless.utils.ClientUtils
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File
import java.io.IOException

abstract class AbstractConfig(val file: File) {
    protected val prettyGSON: Gson = GsonBuilder().setPrettyPrinting().create()

    init {
        if (!file.exists()) {
            ClientUtils.logger.warn("配置文件 ${file.name} 不存在. 创建新的配置文件.")
            file.createNewFile()
        }
    }

    @Throws(IOException::class)
    abstract fun loadConfig()

    @Throws(IOException::class)
    abstract fun saveConfig()
}