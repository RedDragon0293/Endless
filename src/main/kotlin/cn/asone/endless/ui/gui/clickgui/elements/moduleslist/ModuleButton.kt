package cn.asone.endless.ui.gui.clickgui.elements.moduleslist

import cn.asone.endless.features.module.AbstractModule
import cn.asone.endless.ui.gui.clickgui.GuiClickGUI
import cn.asone.endless.ui.gui.clickgui.elements.moduleinfo.AbstractOptionButton
import cn.asone.endless.utils.extensions.mc
import cn.asone.endless.utils.extensions.playSound

class ModuleButton(val module: AbstractModule) : AbstractButton(module.name) {
    override var state: Boolean
        get() = module.state
        set(value) {
            module.state = value
        }

    init {
        if (module.options.isNotEmpty()) {
            module.options.forEach {
                infoButtons.add(AbstractOptionButton.optionToButton(it, false))
            }
        }
        /*val enabledHSB = Color.RGBtoHSB(0, 111, 250, null)
        val disabledHSB = Color.RGBtoHSB(117, 117, 117, null)
        colorHueHelper.reset(if (module.state) enabledHSB[0] else disabledHSB[0])
        colorSaturationHelper.reset(if (module.state) enabledHSB[1] else disabledHSB[1])
        colorBrightnessHelper.reset(if (module.state) enabledHSB[2] else disabledHSB[2])*/
        buttonAnimationHelper.reset(if (module.state) 1F else 0F)
        colorRedHelper.reset(if (module.state) 0F else 117F)
        colorGreenHelper.reset(if (module.state) 111F else 117F)
        colorBlueHelper.reset(if (module.state) 250F else 117F)
    }
    //override val infoValues: ArrayList<Value<*>>
    //get() = module.values

    override fun drawBox(mouseX: Int, mouseY: Int) {
        updateSmoothHelper(state)
        super.drawBox(mouseX, mouseY)
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        if (mouseButton == 1) {
            mc.soundHandler.playSound("random.bow", 1F)
            GuiClickGUI.keyBindModule = module
            GuiClickGUI.settingKeyBind()
        } else super.mouseClicked(mouseX, mouseY, mouseButton)
    }
}