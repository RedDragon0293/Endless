package cn.asone.endless.features.command

import cn.asone.endless.features.module.Module

class ModuleCommand(val module: Module) : Command(module.name) {
    override fun onExecute(command: String) {
        TODO("Not yet implemented")
    }
}