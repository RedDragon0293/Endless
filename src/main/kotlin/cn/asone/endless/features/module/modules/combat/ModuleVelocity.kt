package cn.asone.endless.features.module.modules.combat

import cn.asone.endless.event.EventHook
import cn.asone.endless.features.module.AbstractModule
import cn.asone.endless.features.module.ModuleCategory
import cn.asone.endless.option.AbstractOption

object ModuleVelocity : AbstractModule(
    "Velocity",
    "Allows you to modify the amount of knockback you take.",
    ModuleCategory.COMBAT
) {
    override val handledEvents: ArrayList<EventHook> = arrayListOf()
    override val options: ArrayList<AbstractOption<*>> = arrayListOf()
}