package cn.asone.endless.features.module.modules.visual

import cn.asone.endless.event.EventHook
import cn.asone.endless.event.Render2DEvent
import cn.asone.endless.features.module.AbstractModule
import cn.asone.endless.features.module.ModuleCategory
import cn.asone.endless.option.AbstractOption
import cn.asone.endless.option.BoolOption
import cn.asone.endless.ui.hud.AbstractElement
import cn.asone.endless.ui.hud.elements.ElementArraylist

object
ModuleHUD : AbstractModule("HUD", "Toggles visibility of the HUD.", ModuleCategory.VISUAL) {
    val blackHotbarValue = BoolOption("BlackHotbar", true)
    val smoothHotbarValue = BoolOption("Smooth", false)
    private val arraylistValue = BoolOption("Arraylist", true)
    override val options: ArrayList<AbstractOption<*>> = arrayListOf(
        blackHotbarValue,
        arraylistValue
    )
    private val elements: ArrayList<AbstractElement> = arrayListOf(
        ElementArraylist("Arraylist", 1F, 1F)
    )
    override val handledEvents: ArrayList<EventHook> = arrayListOf(
        EventHook(Render2DEvent::class.java, 100),
        //EventHook(UpdateEvent::class.java)
    )

    init {
        blackHotbarValue.subOptions.add(smoothHotbarValue)
    }

    override fun onRender2D(event: Render2DEvent) {
        elements.forEach { it.drawElement() }
    }

    override fun onUpdate() {
        //elements.forEach { it.updateBorder() }
    }
}