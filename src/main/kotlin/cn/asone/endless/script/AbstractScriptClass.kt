package cn.asone.endless.script

import cn.asone.endless.features.module.AbstractModule

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
}