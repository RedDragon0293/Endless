package cn.asone.endless.features.module.modules.visual

import cn.asone.endless.event.EventHook
import cn.asone.endless.event.Render2DEvent
import cn.asone.endless.features.module.AbstractModule
import cn.asone.endless.features.module.ModuleCategory
import cn.asone.endless.features.module.ModuleManager
import cn.asone.endless.option.AbstractOption
import cn.asone.endless.ui.font.Fonts
import cn.asone.endless.utils.RenderUtils
import java.awt.Color

object ModuleSimpleArrayList : AbstractModule(
    "SimpleArrayList",
    "A pretty simple arraylist just to be continued",
    ModuleCategory.VISUAL
) {
    private val list: List<AbstractModule> by lazy {
        ModuleManager.modules.sortedBy { -Fonts.regular24.getStringWidth(it.arrayName) }
    }
    override val handledEvents: ArrayList<EventHook> = arrayListOf(
        EventHook(Render2DEvent::class.java, 100)
    )
    override val options: ArrayList<AbstractOption<*>> = arrayListOf()

    override fun onRender2D(event: Render2DEvent) {
        var offsetY = 0
        for (module in list) {
            if (module.state) {
                Fonts.regular24.drawString(
                    module.arrayName,
                    RenderUtils.scaledWidth - 2F - Fonts.regular24.getStringWidth(module.arrayName),
                    2F + offsetY,
                    Color.blue.rgb,
                    true
                )
                offsetY += 12
            }
        }
    }
}