package cn.asone.endless.features.module.modules.misc

import cn.asone.endless.features.module.AbstractModule
import cn.asone.endless.features.module.ModuleCategory
import cn.asone.endless.option.BoolOption

object ModuleHYTPacketFixer : AbstractModule(
    "HYTPacketFixer",
    "Fix ViaVersion to 1.12.2.",
    ModuleCategory.MISC
) {
    val c08 = BoolOption("C08", true)
    val swing = BoolOption("Swing", true)
}