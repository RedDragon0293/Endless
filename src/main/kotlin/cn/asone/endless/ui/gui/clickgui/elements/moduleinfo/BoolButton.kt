package cn.asone.endless.ui.gui.clickgui.elements.moduleinfo

import cn.asone.endless.utils.RenderUtils
import cn.asone.endless.value.BoolValue
import org.lwjgl.opengl.GL11
import java.awt.Color

class BoolButton(override val value: BoolValue) : AbstractValueButton(value) {
    val subButtons: ArrayList<AbstractValueButton> = arrayListOf()
    override fun drawBox() {
        super.drawBox()
        RenderUtils.drawAntiAliasingCircle(
            x + 232 - 12,
            startY + 10,
            5F,
            if (value.get()) Color(0, 111, 255).rgb else Color(140, 140, 140).rgb
        )
        if (value.get()) {
            RenderUtils.quickGLColor(Color.white.rgb)
            GL11.glEnable(GL11.GL_LINE_SMOOTH)
            GL11.glLineWidth(2F)
            GL11.glBegin(GL11.GL_LINE_STRIP)
            GL11.glVertex2f(x + 232 - 15, startY + 11)
            GL11.glVertex2f(x + 232 - 12, startY + 13)
            GL11.glVertex2f(x + 232 - 10, startY + 7)
            GL11.glEnd()
            GL11.glDisable(GL11.GL_LINE_SMOOTH)
        }
    }
}