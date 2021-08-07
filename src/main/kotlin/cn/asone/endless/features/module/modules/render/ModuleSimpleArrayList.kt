package cn.asone.endless.features.module.modules.render

import cn.asone.endless.event.EventHook
import cn.asone.endless.event.Render2DEvent
import cn.asone.endless.features.module.AbstractModule
import cn.asone.endless.features.module.ModuleCategory
import cn.asone.endless.features.module.ModuleManager
import cn.asone.endless.ui.font.Fonts
import java.awt.Color

object ModuleSimpleArrayList : AbstractModule(
    "SimpleArrayList",
    "A pretty simple arraylist just to be continued",
    ModuleCategory.RENDER
) {
    override val handledEvents: ArrayList<EventHook> = arrayListOf(
        EventHook(Render2DEvent::class.java, 100)
    )

    override fun onRender2D(event: Render2DEvent) {
        var offsetY = 0
        for (module in ModuleManager.modules) {
            if (module.state) {
                Fonts.regular24.drawString(module.name, 2F, 2F + offsetY, Color.blue.rgb, false)
                offsetY += 10
            }
        }
    }
}