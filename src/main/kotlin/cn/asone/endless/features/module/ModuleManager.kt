package cn.asone.endless.features.module

import cn.asone.endless.event.EventHook
import cn.asone.endless.event.EventManager
import cn.asone.endless.event.KeyEvent
import cn.asone.endless.event.ListenableClass
import cn.asone.endless.features.command.CommandManager
import cn.asone.endless.features.module.modules.combat.ModuleAura
import cn.asone.endless.features.module.modules.combat.ModuleVelocity
import cn.asone.endless.features.module.modules.misc.ModuleAntiBot
import cn.asone.endless.features.module.modules.misc.ModuleTest
import cn.asone.endless.features.module.modules.movement.ModuleSprint
import cn.asone.endless.features.module.modules.render.ModuleClickGUI
import cn.asone.endless.features.module.modules.render.ModuleHUD
import cn.asone.endless.features.module.modules.render.ModuleSimpleArrayList
import cn.asone.endless.utils.ClientUtils
import java.util.*

object ModuleManager : ListenableClass() {
    override val handledEvents: ArrayList<EventHook> = arrayListOf(EventHook(KeyEvent::class.java, 100))
    val modules = TreeSet<AbstractModule> { module1, module2 -> module1.name.compareTo(module2.name) }

    init {
        EventManager.registerListener(this)
        arrayOf(
            ModuleTest,
            ModuleSimpleArrayList,
            ModuleSprint,
            ModuleClickGUI,
            ModuleAura,
            ModuleVelocity,
            ModuleAntiBot,
            ModuleHUD
        ).forEach {
            registerModule(it)
            if (it.values.isNotEmpty())
                CommandManager.registerModuleCommand(it)
        }
        ClientUtils.logger.info("成功初始化 ${modules.size} 个功能.")
    }

    private fun registerModule(module: AbstractModule) {
        modules.add(module)
        EventManager.registerListener(module)
    }

    fun getModule(name: String) = modules.find { it.name.equals(name, true) }

    override fun onKey(event: KeyEvent) {
        if (event.state)
            modules.filter { it.keyBind == event.key }
                .forEach {
                    ClientUtils.displayChatMessage("[ModuleManager] Toggle module ${it.name}")
                    it.toggle()
                }
    }

    override fun isHandleEvents() = true
}