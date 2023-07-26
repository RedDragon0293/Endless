package cn.asone.endless.features.module.modules.misc

import cn.asone.endless.event.EventHook
import cn.asone.endless.event.UpdateEvent
import cn.asone.endless.features.module.AbstractModule
import cn.asone.endless.features.module.ModuleCategory
import cn.asone.endless.option.AbstractOption
import cn.asone.endless.utils.misc.MSTimer

object ModuleAntiAFK : AbstractModule(
    "AntiAFK",
    "Anti AFK",
    ModuleCategory.MISC
) {
    private val jumpTimer = MSTimer(

    )
    override val handledEvents: ArrayList<EventHook> = arrayListOf(EventHook(UpdateEvent::class.java))
    override val options: ArrayList<AbstractOption<*>> = arrayListOf()
    override fun onUpdate() {
        if (jumpTimer.hasTimePassed(60000)) {
            mc.thePlayer.jump()
            jumpTimer.reset()
        }
    }

    override fun onEnable() {
        jumpTimer.reset()
    }
}