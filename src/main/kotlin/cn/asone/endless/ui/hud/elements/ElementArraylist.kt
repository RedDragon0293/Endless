package cn.asone.endless.ui.hud.elements

import cn.asone.endless.features.module.ModuleManager
import cn.asone.endless.ui.font.Fonts
import cn.asone.endless.ui.hud.AbstractElement
import cn.asone.endless.ui.hud.Side
import cn.asone.endless.utils.StringUtils
import cn.asone.endless.value.AbstractValue
import cn.asone.endless.value.BoolValue

class ElementArraylist(name: String, x: Float, y: Float, side: Side = Side(Side.Horizontal.RIGHT, Side.Vertical.UP)) :
    AbstractElement(name, x, y, side) {
    private val nameBreakValue = object : BoolValue("NameBreak", false) {
        override fun changeValue(newValue: Boolean) {
            super.changeValue(newValue)
            if (newValue) {
                ModuleManager.modules.forEach { it.arrayName = StringUtils.getBreakName(it.name) }
            } else {
                ModuleManager.modules.forEach { it.arrayName = it.name }
            }
        }
    }
    override val values: ArrayList<AbstractValue<*>> = arrayListOf(
        nameBreakValue
    )

    override fun drawElement() {
        val fonts = Fonts.regular24
        for (module in ModuleManager.modules) {
            if (!module.state || module.arrayName == "") continue
            var displayString = module.arrayName
            if (module.tag() != "")
                displayString += " §7${module.tag()}"
            val width = fonts.getStringWidth(displayString)

        }
    }

    override fun updateBorder() {
        TODO("Not yet implemented")
    }
}