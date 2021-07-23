package cn.asone.endless.features.module.modules.render

import cn.asone.endless.event.EventHook
import cn.asone.endless.event.SendPacketEvent
import cn.asone.endless.features.module.AbstractModule
import cn.asone.endless.features.module.ModuleCategory

class ModuleClickGUI() : AbstractModule(
    "ClickGUI",
    "Opens the ClickGUI.",
    ModuleCategory.RENDER,
    canEnable = false
) {
    override val handledEvents: ArrayList<EventHook> = arrayListOf(
        EventHook(SendPacketEvent::class.java)
    )
}