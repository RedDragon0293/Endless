package cn.asone.endless.features.module.modules.render

import cn.asone.endless.event.EventHook
import cn.asone.endless.event.Render2DEvent
import cn.asone.endless.event.UpdateEvent
import cn.asone.endless.features.module.AbstractModule
import cn.asone.endless.features.module.ModuleCategory
import cn.asone.endless.value.AbstractValue
import cn.asone.endless.value.BoolValue

object ModuleHUD : AbstractModule("HUD", "Toggles visibility of the HUD.", ModuleCategory.RENDER) {
    val blackHotbarValue = BoolValue("BlackHotbar", true)
    val smoothHotbarValue = BoolValue("Smooth", false)
    private val arraylistValue = BoolValue("Arraylist", true)
    override val values: ArrayList<AbstractValue<*>> = arrayListOf(
        blackHotbarValue,
        arraylistValue
    )
    override val handledEvents: ArrayList<EventHook> = arrayListOf(
        EventHook(Render2DEvent::class.java, 100),
        EventHook(UpdateEvent::class.java)
    )

    init {
        blackHotbarValue.subValue.add(smoothHotbarValue)
    }

    override fun onRender2D(event: Render2DEvent) {
    }

    override fun onUpdate() {

    }
}