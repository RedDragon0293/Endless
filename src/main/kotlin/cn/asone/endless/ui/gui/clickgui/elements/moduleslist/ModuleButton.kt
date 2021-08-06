package cn.asone.endless.ui.gui.clickgui.elements.moduleslist

import cn.asone.endless.features.module.AbstractModule
import cn.asone.endless.ui.gui.clickgui.ClickGUI
import cn.asone.endless.ui.gui.clickgui.elements.moduleinfo.AbstractValueButton
import cn.asone.endless.utils.mc
import cn.asone.endless.utils.playSound

class ModuleButton(val module: AbstractModule) :
    AbstractButton(module.name) {
    override var state: Boolean
        get() = module.state
        set(value) {
            module.state = value
        }

    init {
        if (module.values.isNotEmpty()) {
            module.values.forEach {
                infoButtons.add(AbstractValueButton.valueToButton(it, false))
            }
        }
    }
    //override val infoValues: ArrayList<Value<*>>
    //get() = module.values

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        if (mouseButton == 1) {
            mc.soundHandler.playSound("random.bow", 1F)
            ClickGUI.keyBindModule = module
            ClickGUI.settingKeyBind()
        } else
            super.mouseClicked(mouseX, mouseY, mouseButton)
    }
}