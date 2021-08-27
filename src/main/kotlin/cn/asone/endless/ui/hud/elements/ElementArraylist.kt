package cn.asone.endless.ui.hud.elements

import cn.asone.endless.features.module.AbstractModule
import cn.asone.endless.features.module.ModuleManager
import cn.asone.endless.ui.font.Fonts
import cn.asone.endless.ui.hud.AbstractElement
import cn.asone.endless.ui.hud.Side
import cn.asone.endless.utils.StringUtils
import cn.asone.endless.value.AbstractValue
import cn.asone.endless.value.BoolValue
import java.awt.Color

class ElementArraylist(name: String, x: Float, y: Float, side: Side = Side(Side.Horizontal.RIGHT, Side.Vertical.UP)) :
    AbstractElement(name, x, y, side) {
    private val nameBreakValue = object : BoolValue("NameBreak", false) {
        override fun changeValue(newValue: Boolean) {
            super.changeValue(newValue)
            if (newValue) {
                ModuleManager.modules.forEach { it.arrayName = StringUtils.getBreakName(it.arrayName) }
            } else {
                ModuleManager.modules.forEach { it.arrayName = it.arrayName }
            }
        }
    }
    override val values: ArrayList<AbstractValue<*>> = arrayListOf(
        nameBreakValue
    )

    private val list: List<AbstractModule> by lazy {
        ModuleManager.modules.sortedBy { -Fonts.regular24.getStringWidth(it.arrayName) }
    }

    override fun drawElement() {
        var offsetY = 0
        for (module in this.list) {
            if (module.state) {
                Fonts.regular24.drawString(
                    module.arrayName,
                    renderX - if (side.horizontal == Side.Horizontal.LEFT) 0 else Fonts.regular24.getStringWidth(module.arrayName),
                    renderY + offsetY,
                    Color.blue.rgb,
                    true
                )
                offsetY += 12
            }
        }
    }
}