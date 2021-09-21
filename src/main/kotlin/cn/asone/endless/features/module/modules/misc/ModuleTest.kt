package cn.asone.endless.features.module.modules.misc

import cn.asone.endless.event.EventHook
import cn.asone.endless.event.Render2DEvent
import cn.asone.endless.features.module.AbstractModule
import cn.asone.endless.features.module.ModuleCategory
import cn.asone.endless.ui.font.Fonts
import cn.asone.endless.utils.RenderUtils
import com.mojang.realmsclient.gui.ChatFormatting
import net.minecraft.client.renderer.GlStateManager
import java.awt.Color

object ModuleTest : AbstractModule(
    "Test",
    "Just used while developing.",
    ModuleCategory.MISC
) {
    override val handledEvents: ArrayList<EventHook> = arrayListOf(
        EventHook(Render2DEvent::class.java)
    )

    override fun onRender2D(event: Render2DEvent) {
        GlStateManager.disableTexture2D()
        RenderUtils.drawRect(
            10F,
            10F,
            Fonts.mcRegular18.getStringWidth("AAAAAAAAAAAAAAAAAAAAAAAAA").toFloat(),
            Fonts.mcRegular18.FONT_HEIGHT.toFloat(),
            Color.black.rgb
        )
        GlStateManager.enableTexture2D()
        val var0 = Fonts.mcRegular18.drawString(
            "${ChatFormatting.UNDERLINE}AAAAAAAAAAAAAAAAAAAAAAAAA",
            10F,
            10F,
            Color.white.rgb
        )
        RenderUtils.pre2D()
        RenderUtils.drawRect(
            var0.toFloat(), 10F, 1F, 10F, Color(100, 100, 100, 100).rgb
        )
        RenderUtils.post2D()
    }
}