package cn.asone.endless.ui.gui.clickgui.elements.moduleinfo

import cn.asone.endless.ui.font.Fonts
import cn.asone.endless.ui.gui.clickgui.GuiClickGUI
import cn.asone.endless.ui.gui.clickgui.elements.moduleslist.AbstractButton
import cn.asone.endless.utils.RenderUtils
import cn.asone.endless.option.*

/**
 * 232 or 212 × 20 button
 */
abstract class AbstractOptionButton(open val value: AbstractOption<*>, val isSub: Boolean) {
    companion object {
        fun optionToButton(value: AbstractOption<*>, isSub: Boolean): AbstractOptionButton {
            return when (value) {
                is BoolOption -> BoolButton(value, isSub)
                is IntOption -> IntButton(value, isSub)
                is FloatOption -> FloatButton(value, isSub)
                is ListOption -> ListButton(value, isSub)
                is TextOption -> TextButton(value, isSub)
                else -> throw Exception("内部错误!")
            }
        }
    }

    protected var x = 0F
    protected var y = 0F
        get() = field + offset
    open val boundingBoxHeight = 20F
    protected var offset = 0F
    val visible: Boolean
        get() = y < GuiClickGUI.windowYStart + GuiClickGUI.guiHeight - 6 && y + boundingBoxHeight > GuiClickGUI.windowYStart + 44 + 6

    fun isHovering(mouseX: Int, mouseY: Int): Boolean =
        mouseX >= x && mouseX <= x + (if (isSub) 212 else 232) && mouseY >= y && mouseY <= y + boundingBoxHeight

    open fun updateX(x: Float) {
        this.x = x
    }

    open fun updateY(y: Float) {
        this.y = y
    }

    open fun updateOffset(offset: Float) {
        this.offset = offset
    }

    open fun drawText(mouseX: Int, mouseY: Int) {
        Fonts.light24.drawString(value.name, x + 5F, y + 5F, GuiClickGUI.textColor)
    }

    open fun drawBox(mouseX: Int, mouseY: Int) {
        //if (!isHovering(mouseX, mouseY))
        //    RenderUtils.drawRect(x + 5F, y + 5F, nameFont.getStringWidth(value.name).toFloat(), nameFont.height.toFloat(), Color.white.rgb)
        RenderUtils.drawAntiAliasingRoundedRect(x, y, if (isSub) 212F else 232F, 20F, 4F, AbstractButton.boxColor)
    }

    open fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {}
    open fun mouseDragged(mouseX: Int, mouseY: Int, mouseButton: Int, duration: Long) {}
}