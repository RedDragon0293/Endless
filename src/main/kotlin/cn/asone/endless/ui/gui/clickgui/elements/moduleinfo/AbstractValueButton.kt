package cn.asone.endless.ui.gui.clickgui.elements.moduleinfo

import cn.asone.endless.ui.font.CFontRenderer
import cn.asone.endless.ui.font.Fonts
import cn.asone.endless.ui.gui.clickgui.ClickGUI
import cn.asone.endless.utils.RenderUtils
import cn.asone.endless.value.BoolValue
import cn.asone.endless.value.Value
import java.awt.Color

abstract class AbstractValueButton(open val value: Value<*>, val isSub: Boolean) {
    companion object {
        fun valueToButton(value: Value<*>, isSub: Boolean): AbstractValueButton {
            return when (value) {
                is BoolValue -> BoolButton(value, isSub)
                //is IntValue ->
                else -> TODO()
            }
        }
    }

    protected var x = 0F
    protected var y = 0F
        get() = field + offset
    open val boundingBoxHeight = 20F
    protected var offset = 0F
    val visible: Boolean
        get() = y < ClickGUI.windowYStart + ClickGUI.guiHeight - 6 && y + boundingBoxHeight > ClickGUI.windowYStart + 44 + 6
    private val nameFont = CFontRenderer(Fonts.getAssetsFont("Roboto-Light.ttf", 24), true, true)

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

    open fun drawText() {
        nameFont.drawString(value.name, x + 5F, y + 5F, Color.black.rgb)
    }

    open fun drawBox() {
        RenderUtils.drawAntiAliasingRoundedRect(x, y, if (isSub) 212F else 232F, 20F, 4F, Color.white.rgb)
    }

    open fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {}
    open fun mouseDragged(mouseX: Int, mouseY: Int, mouseButton: Int, duration: Long) {}
}