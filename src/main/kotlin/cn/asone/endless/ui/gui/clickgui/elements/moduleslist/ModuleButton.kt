package cn.asone.endless.ui.gui.clickgui.elements.moduleslist

import cn.asone.endless.features.module.AbstractModule

class ModuleButton(val module: AbstractModule) :
    AbstractButton(module.name, module.state) {
    override fun mouseClicked(mouseX: Int, mouseY: Int) {
        TODO("Not yet implemented")
    }
}