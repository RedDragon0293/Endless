package cn.asone.endless.features.module.modules.misc

import cn.asone.endless.event.EventHook
import cn.asone.endless.features.module.AbstractModule
import cn.asone.endless.features.module.ModuleCategory
import cn.asone.endless.option.AbstractOption

object ModuleAntiBot : AbstractModule(
    "AntiBot",
    "Detects AntiCheat bots.",
    ModuleCategory.MISC
) {
    override val handledEvents: ArrayList<EventHook> = arrayListOf()
    override val options: ArrayList<AbstractOption<*>> = arrayListOf()
}