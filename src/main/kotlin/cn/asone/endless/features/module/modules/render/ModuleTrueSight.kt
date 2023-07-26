package cn.asone.endless.features.module.modules.render

import cn.asone.endless.event.EventHook
import cn.asone.endless.features.module.AbstractModule
import cn.asone.endless.features.module.ModuleCategory
import cn.asone.endless.option.AbstractOption
import cn.asone.endless.option.BoolOption

object ModuleTrueSight : AbstractModule(
    "TrueSight",
    "火眼金睛",
    ModuleCategory.VISUAL
) {
    val barriers = BoolOption("Barriers", true)
    val entities = BoolOption("Entities", true)

    override val options: ArrayList<AbstractOption<*>> = arrayListOf(
        barriers, entities
    )
    override val handledEvents: ArrayList<EventHook> = arrayListOf()
}