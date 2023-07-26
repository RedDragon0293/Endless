package cn.asone.endless.script

import cn.asone.endless.event.EventHook
import cn.asone.endless.features.module.AbstractModule
import cn.asone.endless.option.AbstractOption

abstract class AbstractScriptClass(
    name: String,
    description: String,
    category: Int
) : AbstractModule(
    name, description, category
) {
    abstract val scriptName: String
    abstract val scriptVersion: String
    abstract val scriptAuthor: String
    override val handledEvents: ArrayList<EventHook> = arrayListOf()
    override val options: ArrayList<AbstractOption<*>> = arrayListOf()
}