package cn.asone.endless.features.module

import cn.asone.endless.event.Event
import cn.asone.endless.event.EventManager
import cn.asone.endless.event.KeyEvent
import cn.asone.endless.features.command.CommandManager
import cn.asone.endless.features.module.modules.misc.TestModule
import cn.asone.endless.utils.ClientUtils
import cn.asone.endless.utils.ListenableManager
import java.util.*

object ModuleManager : ListenableManager() {
    override fun handledEvents(): List<Class<out Event>> = arrayListOf(KeyEvent::class.java)
    private val modules = TreeSet<Module> { module1, module2 -> module1.name.compareTo(module2.name) }

    init {
        EventManager.registerListener(this)
        arrayOf(
                TestModule
        ).forEach {
            registerModule(it)
            CommandManager.registerModuleCommand(it)
        }
    }

    private fun registerModule(module: Module) {
        modules.add(module)
        EventManager.registerListener(module)
    }

    fun getModule(name: String) = modules.find { it.name.equals(name, true) }

    override fun onKey(event: KeyEvent) {
        if (event.state)
            modules.filter { it.keyBind == event.key }
                    .forEach {
                        ClientUtils.displayChatMessage("Toggle module ${it.name}")
                        it.toggle()
                    }
    }

    override fun isHandleEvents() = true
}