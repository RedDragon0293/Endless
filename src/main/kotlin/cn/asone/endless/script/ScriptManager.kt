package cn.asone.endless.script

import cn.asone.endless.config.ConfigManager
import cn.asone.endless.features.command.CommandManager
import cn.asone.endless.features.module.ModuleManager
import cn.asone.endless.utils.ClientUtils
import com.itranswarp.compiler.JavaStringCompiler
import java.io.File
import java.util.*

object ScriptManager {
    private val scriptDir = File(ConfigManager.rootDir, "scripts").apply {
        if (!exists())
            if (!mkdir())
                throw Exception("无法创建脚本文件夹!")
    }

    @Suppress("unchecked_cast")
    fun loadAllScripts() {
        val compiler = JavaStringCompiler()
        scriptDir.listFiles()?.forEach { it ->
            if (it.name.endsWith(".java") && it.isFile) {
                val sourceCode = it.readText()
                var className = sourceCode.takeWhile { it != '\r' && it != '\n' }
                if (!className.startsWith("//")) {
                    ClientUtils.logger.error("无法加载脚本 ${it.name}, 请在文件第一行添加完整类名.")
                    return@forEach
                } else {
                    className = className.drop(2)
                }
                ClientUtils.logger.info("正在编译$className")
                val results = compiler.compile(it.name, sourceCode)
                val clazz = compiler.loadClass(className, results)
                if (clazz != null) {
                    if (Objects.equals(clazz.superclass, AbstractScriptClass::class.java)) {
                        val instance = (clazz as Class<AbstractScriptClass>).newInstance()
                        loadScriptModule(instance)
                        ClientUtils.logger.info("成功加载脚本 ${instance.scriptName}, 版本 ${instance.scriptVersion}, 作者 ${instance.scriptAuthor}.")
                    } else {
                        ClientUtils.logger.error("无法加载脚本 ${it.name}, 请确保其正确继承父类\"AbstractScriptClass\".")
                    }
                }
            }
        }
    }

    private fun loadScriptModule(module: AbstractScriptClass) {
        ModuleManager.registerModule(module)
        if (module.options.isNotEmpty()) {
            CommandManager.registerModuleCommand(module)
        }
    }
}