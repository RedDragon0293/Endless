package cn.asone.endless.features.module.modules.misc

import cn.asone.endless.features.module.AbstractModule
import cn.asone.endless.features.module.ModuleCategory
import cn.asone.endless.ui.font.Fonts

object ModuleTest : AbstractModule(
    "Test",
    "Just used while developing.",
    ModuleCategory.MISC
) {
    override fun onEnable() {
        Fonts.mcRegular18.test()
    }
}