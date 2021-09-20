package cn.asone.endless.ui.gui.clickgui.elements.moduleinfo

import cn.asone.endless.ui.font.Fonts
import cn.asone.endless.ui.gui.clickgui.GuiClickGUI
import cn.asone.endless.utils.RenderUtils
import cn.asone.endless.utils.mc
import cn.asone.endless.utils.playSound
import cn.asone.endless.value.ListValue
import java.awt.Color

class ListButton(override val value: ListValue, isSub: Boolean) : AbstractValueButton(value, isSub) {
    private val subButtons: MutableMap<String, ArrayList<AbstractValueButton>> = mutableMapOf()
    override val boundingBoxHeight: Float
        get() = super.boundingBoxHeight +
                if (!subButtons[this.value.get()].isNullOrEmpty())
                    subButtons[this.value.get()]!!.size * 24
                else 0

    init {
        for (currentValue in value.values)
            subButtons[currentValue] = arrayListOf()
        for (currentValue in value.subValue) {
            if (currentValue.value.isNotEmpty()) {
                currentValue.value.forEach {
                    subButtons[currentValue.key]!!.add(valueToButton(it, true))
                }
            }
        }
    }

    override fun updateX(x: Float) {
        super.updateX(x)
        if (subButtons[this.value.get()]!!.isNotEmpty()) {
            subButtons[this.value.get()]!!.forEach { it.updateX(x + 20) }
        }
    }

    override fun updateY(y: Float) {
        super.updateY(y)
        if (subButtons[this.value.get()]!!.isNotEmpty()) {
            var var0 = 0
            subButtons[this.value.get()]!!.forEach {
                var0 += 24
                it.updateY(y + var0)
            }
        }
    }

    override fun updateOffset(offset: Float) {
        super.updateOffset(offset)
        if (subButtons[this.value.get()]!!.isNotEmpty()) {
            subButtons[this.value.get()]!!.forEach {
                it.updateOffset(offset)
            }
        }
    }

    override fun drawBox(mouseX: Int, mouseY: Int) {
        super.drawBox(mouseX, mouseY)
        /**
         * Grey box around the value text
         */
        if (mouseX >= x && mouseX <= x + (if (isSub) 212 else 232) && mouseY >= y && mouseY <= y + 20)
            RenderUtils.drawBorder(
                x + (if (isSub) 212 else 232) - 5 - Fonts.condensedLight18.getStringWidth(value.get()) - 2,
                y + 3,
                x + (if (isSub) 212 else 232) - 5 + 2,
                y + 7 + Fonts.condensedLight18.FONT_HEIGHT + 2,
                1F,
                Color(140, 140, 140).rgb
            )
        if (subButtons[this.value.get()]!!.isNotEmpty()) {
            RenderUtils.drawLine(
                x + 15,
                y + 21,
                x + 15,
                y + 20 + subButtons[this.value.get()]!!.size * 24,
                6F,
                Color(0, 111, 255).rgb
            )
            subButtons[this.value.get()]!!.forEach { it.drawBox(mouseX, mouseY) }
        }
    }

    override fun drawText(mouseX: Int, mouseY: Int) {
        super.drawText(mouseX, mouseY)
        Fonts.condensedLight18.drawString(
            value.get(),
            x + (if (isSub) 212 else 232) - 5 - Fonts.condensedLight18.getStringWidth(value.get()),
            y + 7,
            GuiClickGUI.textColor
        )
        if (subButtons[this.value.get()]!!.isNotEmpty()) {
            subButtons[this.value.get()]!!.forEach { it.drawText(mouseX, mouseY) }
        }
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        if (mouseButton == 0) {
            if (mouseX >= x + (if (isSub) 212 else 232) - 5 - Fonts.condensedLight18.getStringWidth(value.get()) - 2
                && mouseX <= x + (if (isSub) 212 else 232) - 5 + 2
                && mouseY >= y + 3
                && mouseY <= y + 7 + Fonts.condensedLight18.FONT_HEIGHT + 2
            ) {
                mc.soundHandler.playSound("gui.button.press", 1F)
                GuiClickGUI.listButton = this
            } else if (subButtons[this.value.get()]!!.isNotEmpty()) {
                for (button in subButtons[this.value.get()]!!) {
                    if (button.isHovering(mouseX, mouseY)) {
                        button.mouseClicked(mouseX, mouseY, mouseButton)
                        break
                    }
                }
            }
        }
    }

    override fun mouseDragged(mouseX: Int, mouseY: Int, mouseButton: Int, duration: Long) {
        if (subButtons[this.value.get()]!!.isNotEmpty()) {
            for (button in subButtons[this.value.get()]!!) {
                if (button.isHovering(mouseX, mouseY)) {
                    button.mouseDragged(mouseX, mouseY, mouseButton, duration)
                    break
                }
            }
        }
    }
}