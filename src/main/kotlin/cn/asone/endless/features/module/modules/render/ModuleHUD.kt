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
import net.minecraft.util.EnumChatFormatting
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
        Fonts.regular20.drawString("Hello, world! 你好，世界！这是一个美好的开始，期待与你的再次相遇", 100F, 100F, Color.white.rgb)
        Fonts.regular20.drawString(
            "${EnumChatFormatting.STRIKETHROUGH}Combat Player Movement Render World Misc",
            100F,
            120F,
            Color.white.rgb
        )
        RenderUtils.pre2D()
        RenderUtils.drawBorder(
            100F,
            100F,
            100F + Fonts.regular20.getStringWidth("Hello, world! 你好，世界！这是一个美好的开始，期待与你的再次相遇"),
            100F + Fonts.regular20.FONT_HEIGHT,
            1F,
            Color.red.rgb
        )
        RenderUtils.post2D()
    }

    override fun onUpdate() {

    }
}