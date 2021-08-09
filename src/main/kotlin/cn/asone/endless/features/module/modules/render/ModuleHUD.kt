package cn.asone.endless.features.module.modules.render

import cn.asone.endless.event.EventHook
import cn.asone.endless.event.Render2DEvent
import cn.asone.endless.event.UpdateEvent
import cn.asone.endless.features.module.AbstractModule
import cn.asone.endless.features.module.ModuleCategory
import cn.asone.endless.ui.font.Fonts
import cn.asone.endless.utils.RenderUtils
import cn.asone.endless.value.AbstractValue
import cn.asone.endless.value.BoolValue
import java.awt.Color

object ModuleHUD : AbstractModule("HUD", "Toggles visibility of the HUD.", ModuleCategory.RENDER) {
    val blackHotbarValue = BoolValue("BlackHotbar", true)
    val smoothHotbarValue = BoolValue("Smooth", false)
    override val values: ArrayList<AbstractValue<*>> = arrayListOf(blackHotbarValue)
    override val handledEvents: ArrayList<EventHook> = arrayListOf(
        EventHook(Render2DEvent::class.java, 100),
        EventHook(UpdateEvent::class.java)
    )

    init {
        blackHotbarValue.subValue.add(smoothHotbarValue)
    }

    override fun onRender2D(event: Render2DEvent) {
        Fonts.test.drawString("Hello, world! 你好，世界！", 100F, 100F, Color.white.rgb)
        Fonts.regular20.drawString("Hello, world!", 100F, 110F, Color.white.rgb)
        RenderUtils.drawBorder(
            100F,
            100F,
            100F + Fonts.test.getStringWidth("Hello, world! 你好，世界！"),
            100F + Fonts.test.FONT_HEIGHT,
            1F,
            Color.red.rgb
        )
    }

    override fun onUpdate() {

    }
}