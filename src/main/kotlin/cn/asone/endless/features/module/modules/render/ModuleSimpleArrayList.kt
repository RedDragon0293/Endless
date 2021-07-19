package cn.asone.endless.features.module.modules.render

import cn.asone.endless.event.Event
import cn.asone.endless.event.Render2DEvent
import cn.asone.endless.features.module.AbstractModule
import cn.asone.endless.features.module.ModuleCategory
import cn.asone.endless.features.module.ModuleManager
import java.awt.Color

object ModuleSimpleArrayList : AbstractModule(
        "SimpleArrayList",
        "A pretty simple arraylist just to be continued",
        ModuleCategory.RENDER
) {
    override val handledEvents: ArrayList<Class<out Event>> = arrayListOf(
            Render2DEvent::class.java
    )

    override fun onRender2D(event: Render2DEvent) {
        var offsetY = 0
        for (module in ModuleManager.modules) {
            if (module.state) {
                mc.fontRendererObj.drawString(module.name, 10F, 10F + offsetY, Color.blue.rgb, false)
                offsetY += 10
            }
        }
    }
}