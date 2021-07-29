package cn.asone.endless.features.module.modules.combat

import cn.asone.endless.features.module.AbstractModule
import cn.asone.endless.features.module.ModuleCategory
import org.lwjgl.input.Keyboard

object ModuleAura : AbstractModule(
    "Aura",
    "Automatically attacks targets around you.",
    ModuleCategory.COMBAT,
    Keyboard.KEY_R
) {
}