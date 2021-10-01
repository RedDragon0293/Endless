package cn.asone.endless.ui.gui.clickgui.elements.moduleinfo

import cn.asone.endless.utils.RenderUtils
import cn.asone.endless.utils.extensions.mc
import cn.asone.endless.utils.extensions.playSound
import cn.asone.endless.value.BoolValue
import org.lwjgl.opengl.GL11
import java.awt.Color

class BoolButton(override val value: BoolValue, isSub: Boolean) : AbstractValueButton(value, isSub) {
    private val subButtons: ArrayList<AbstractValueButton> = arrayListOf()
    override val boundingBoxHeight: Float
        get() = if (this.value.get()) super.boundingBoxHeight + subButtons.size * 24 else super.boundingBoxHeight

    init {
        if (value.subValue.isNotEmpty()) {
            value.subValue.forEach {
                subButtons.add(valueToButton(it, true))
            }
        }
    }

    override fun updateX(x: Float) {
        super.updateX(x)
        if (subButtons.isNotEmpty()) {
            subButtons.forEach { it.updateX(x + 20) }
        }
    }

    override fun updateY(y: Float) {
        super.updateY(y)
        if (subButtons.isNotEmpty()) {
            var var0 = 0
            subButtons.forEach {
                var0 += 24
                it.updateY(y + var0)
            }
        }
    }

    override fun updateOffset(offset: Float) {
        super.updateOffset(offset)
        if (subButtons.isNotEmpty()) {
            subButtons.forEach { it.updateOffset(offset) }
        }
    }

    override fun drawBox(mouseX: Int, mouseY: Int) {
        super.drawBox(mouseX, mouseY)
        /**
         * Boolean background
         */
        RenderUtils.drawAntiAliasingCircle(
            x + (if (isSub) 212 else 232) - 12,
            y + 10,
            5F,
            if (value.get()) Color(0, 111, 255).rgb else Color(140, 140, 140).rgb
        )
        /**
         * Draw √
         */
        if (value.get()) {
            RenderUtils.quickGLColor(Color.white.rgb)
            GL11.glEnable(GL11.GL_LINE_SMOOTH)
            GL11.glLineWidth(2F)
            GL11.glBegin(GL11.GL_LINE_STRIP)
            GL11.glVertex2f(x + (if (isSub) 212 else 232) - 15, y + 11)
            GL11.glVertex2f(x + (if (isSub) 212 else 232) - 12, y + 13)
            GL11.glVertex2f(x + (if (isSub) 212 else 232) - 10, y + 7)
            GL11.glEnd()
            GL11.glDisable(GL11.GL_LINE_SMOOTH)
        }
        if (subButtons.isNotEmpty() && this.value.get()) {
            RenderUtils.drawLine(x + 15, y + 21, x + 15, y + 20 + subButtons.size * 24, 6F, Color(0, 111, 255).rgb)
            subButtons.forEach { it.drawBox(mouseX, mouseY) }
        }
    }

    override fun drawText(mouseX: Int, mouseY: Int) {
        super.drawText(mouseX, mouseY)
        if (subButtons.isNotEmpty() && this.value.get()) {
            subButtons.forEach { it.drawText(mouseX, mouseY) }
        }
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        if (mouseButton == 0) {
            if (mouseX >= (x + (if (isSub) 212 else 232) - 12 - 5) &&
                mouseX <= (x + (if (isSub) 212 else 232) - 12 + 5) &&
                mouseY >= y + 5 && mouseY <= y + 15
            ) {
                mc.soundHandler.playSound("gui.button.press", 1F)
                this.value.set(!this.value.get())
            } else if (this.value.get())
                for (button in subButtons)
                    if (button.isHovering(mouseX, mouseY)) {
                        button.mouseClicked(mouseX, mouseY, mouseButton)
                        break
                    }

        }
    }

    override fun mouseDragged(mouseX: Int, mouseY: Int, mouseButton: Int, duration: Long) {
        if (this.value.get())
            for (button in subButtons)
                if (button.isHovering(mouseX, mouseY)) {
                    button.mouseDragged(mouseX, mouseY, mouseButton, duration)
                    break
                }
    }
}