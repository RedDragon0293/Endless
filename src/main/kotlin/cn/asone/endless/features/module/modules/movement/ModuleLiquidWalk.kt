package cn.asone.endless.features.module.modules.movement

import cn.asone.endless.event.EventHook
import cn.asone.endless.features.module.AbstractModule
import cn.asone.endless.features.module.ModuleCategory
import cn.asone.endless.option.AbstractOption

object ModuleLiquidWalk : AbstractModule(
    "LiquidWalk",
    "",
    ModuleCategory.MOVEMENT
) {
    override val handledEvents: ArrayList<EventHook> = arrayListOf()
    override val options: ArrayList<AbstractOption<*>> = arrayListOf()
}