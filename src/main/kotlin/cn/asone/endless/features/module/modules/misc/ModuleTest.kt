package cn.asone.endless.features.module.modules.misc

import cn.asone.endless.event.EventHook
import cn.asone.endless.event.PreMotionEvent
import cn.asone.endless.event.Render2DEvent
import cn.asone.endless.features.module.AbstractModule
import cn.asone.endless.features.module.ModuleCategory
import cn.asone.endless.option.AbstractOption
import net.minecraft.entity.item.EntityArmorStand
import java.awt.Color

object ModuleTest : AbstractModule(
    "Test",
    "Just used while developing.",
    ModuleCategory.MISC
) {
    private var closestEntity: EntityArmorStand? = null
    override val handledEvents: ArrayList<EventHook> = arrayListOf(
        EventHook(PreMotionEvent::class.java),
        EventHook(Render2DEvent::class.java)
    )
    override val options: ArrayList<AbstractOption<*>> = arrayListOf()

    override fun onPreMotion(event: PreMotionEvent) {
        val list = mc.theWorld.loadedEntityList
        var min = Float.MAX_VALUE
        for (i in list) {
            if (i !is EntityArmorStand || i.getCurrentArmor(3) == null) {
                continue
            }
            val dis = i.getDistanceToEntity(mc.thePlayer)
            if (dis < min) {
                min = dis
                closestEntity = i
            }
        }
    }

    override fun onRender2D(event: Render2DEvent) {
        mc.fontRendererObj.drawString(closestEntity?.displayName?.unformattedText ?: "NULL", 100, 100, Color.BLUE.rgb)
        if (closestEntity != null) {
            mc.fontRendererObj.drawString(
                closestEntity!!.getDistanceToEntity(mc.thePlayer).toString(),
                100,
                110,
                Color.BLUE.rgb
            )
            if (closestEntity!!.getCurrentArmor(3) != null) {
                mc.fontRendererObj.drawString(closestEntity!!.getCurrentArmor(3).displayName, 100, 120, Color.GREEN.rgb)
            }
        }
    }
}