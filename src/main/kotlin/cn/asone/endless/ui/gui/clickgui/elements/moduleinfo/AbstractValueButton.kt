package cn.asone.endless.ui.gui.clickgui.elements.moduleinfo

import cn.asone.endless.ui.font.CFontRenderer
import cn.asone.endless.ui.font.Fonts
import cn.asone.endless.ui.gui.clickgui.ClickGUI
import cn.asone.endless.utils.RenderUtils
import cn.asone.endless.value.Value
import java.awt.Color

abstract class AbstractValueButton(open val value: Value<*>) {
    protected var x = 0F
    protected var startY = 0F
        get() = field + offset
    protected val endY
        get() = startY + 20
    var offset = 0F
    val visible: Boolean
        get() = startY < ClickGUI.windowYStart + ClickGUI.guiHeight - 6 && startY + 20 > ClickGUI.windowYStart + 44 + 6
    private val nameFont = CFontRenderer(Fonts.getAssetsFont("Roboto-Light.ttf", 26), true, true)

    fun isHovering(mouseX: Int, mouseY: Int): Boolean =
        mouseX >= x && mouseX <= x + 232 && mouseY >= startY && mouseY <= startY + 20

    fun updateX(x: Float) {
        this.x = x
    }

    fun updateY(y: Float) {
        this.startY = y
    }

    open fun drawText() {
        nameFont.drawString(value.name, x + 6F, startY + 4F, Color.black.rgb)
    }

    open fun drawBox() {
        RenderUtils.drawAntiAliasingRoundedRect(x, startY, 232F, 20F, 4F, Color.white.rgb)
    }
}