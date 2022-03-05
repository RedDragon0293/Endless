package cn.asone.endless.ui.gui.clickgui.elements.moduleslist

import cn.asone.endless.ui.font.Fonts
import cn.asone.endless.ui.gui.clickgui.GuiClickGUI
import cn.asone.endless.ui.gui.clickgui.elements.moduleinfo.AbstractValueButton
import cn.asone.endless.utils.ColorUtils
import cn.asone.endless.utils.RenderUtils
import cn.asone.endless.utils.animation.SmoothHelper
import cn.asone.endless.utils.extensions.mc
import cn.asone.endless.utils.extensions.playSound
import java.awt.Color

/**
 * 150 × 23 button
 */
abstract class AbstractButton(open val name: String) {
    companion object {
        var boxColor: Int = Color.white.rgb
        var buttonColor: Int = Color.white.rgb
    }

    protected var x = 0F
    protected var y = 0F
        get() = field + offset
    var offset = 0F
    open var state = false
        set(value) {
            field = value
            buttonAnimationHelper.currentValue = if (value) 1F else 0F
            /*val enabledHSB = Color.RGBtoHSB(0, 111, 250, null)
            val disabledHSB = Color.RGBtoHSB(117, 117, 117, null)
            colorHueHelper.currentValue = if (value) enabledHSB[0] else disabledHSB[0]
            colorSaturationHelper.currentValue = if (value) enabledHSB[1] else disabledHSB[1]
            colorBrightnessHelper.currentValue = if (value) enabledHSB[2] else disabledHSB[2]*/
            colorRedHelper.currentValue = if (value) 0F else 117F
            colorGreenHelper.currentValue = if (value) 111F else 117F
            colorBlueHelper.currentValue = if (value) 250F else 117F
        }
    open val infoButtons: ArrayList<AbstractValueButton> = arrayListOf()
    val visible: Boolean
        get() = y < GuiClickGUI.windowYStart + GuiClickGUI.guiHeight - 7 && y + 23 > GuiClickGUI.windowYStart + 7
    protected val buttonAnimationHelper = SmoothHelper()
    protected val colorRedHelper = SmoothHelper()
    protected val colorGreenHelper = SmoothHelper()
    protected val colorBlueHelper = SmoothHelper()

    fun updateX(x: Float) {
        this.x = x
    }

    fun updateY(y: Float) {
        this.y = y
    }

    open fun drawBox(mouseX: Int, mouseY: Int) {
        //Background
        RenderUtils.drawAntiAliasingRoundedRect(x, y, 150F, 23F, 5F, boxColor)
        buttonAnimationHelper.tick()
        colorRedHelper.tick()
        colorGreenHelper.tick()
        colorBlueHelper.tick()
        //Button
        RenderUtils.drawAntiAliasingRoundedRect(
            x + 150 - 3 - 17,
            y + 6,
            17F,
            11F,
            5.5F,
            ColorUtils.getColorInt(
                colorRedHelper.get().toInt(),
                colorGreenHelper.get().toInt(),
                colorBlueHelper.get().toInt()
            )
        )
        RenderUtils.drawAntiAliasingCircle(
            x + 150 - 14 + (buttonAnimationHelper.get() * 5F), y + 11.5F, 4.5F, buttonColor
        )
    }

    open fun drawText(mouseX: Int, mouseY: Int) {
        Fonts.regular26.drawString(name, x + 4, y + 5, GuiClickGUI.textColor)
    }

    fun isHovering(mouseX: Int, mouseY: Int): Boolean =
        mouseX >= x && mouseX <= x + 150 && mouseY >= y && mouseY <= y + 23

    open fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        if (mouseButton == 0) {
            mc.soundHandler.playSound("gui.button.press", 1F)
            if (mouseX >= x + 150 - 3 - 17 && mouseX <= x + 150 - 3 && mouseY >= y + 6 && mouseY <= y + 17) this.state =
                !this.state
            else {
                GuiClickGUI.currentInfoButton = this
                if (infoButtons.isNotEmpty()) {
                    infoButtons.forEach {
                        it.updateX(GuiClickGUI.windowXStart + 231 + 6F)
                    }
                }
            }
        }
    }
}