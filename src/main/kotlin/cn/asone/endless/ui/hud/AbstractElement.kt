package cn.asone.endless.ui.hud

import cn.asone.endless.utils.RenderUtils
import cn.asone.endless.value.ValueRegister

abstract class AbstractElement(
    var name: String,
    var x: Float = 2F, var y: Float = 2F, var side: Side = Side.default(),
) : ValueRegister {
    open var renderX: Float
        get() = when (side.horizontal) {
            Side.Horizontal.LEFT -> x
            Side.Horizontal.MIDDLE -> (RenderUtils.scaledWidth / 2) - x
            Side.Horizontal.RIGHT -> RenderUtils.scaledWidth - x
        }
        set(value) = when (side.horizontal) {
            Side.Horizontal.LEFT -> {
                x += value
            }
            Side.Horizontal.MIDDLE, Side.Horizontal.RIGHT -> {
                x -= value
            }
        }

    open var renderY: Float
        get() = when (side.vertical) {
            Side.Vertical.UP -> y
            Side.Vertical.MIDDLE -> (RenderUtils.scaledHeight / 2) - y
            Side.Vertical.DOWN -> RenderUtils.scaledHeight - y
        }
        set(value) = when (side.vertical) {
            Side.Vertical.UP -> {
                y += value
            }
            Side.Vertical.MIDDLE, Side.Vertical.DOWN -> {
                y -= value
            }
        }
    var width: Float = 0F
    var height: Float = 0F

    /**
     * Draw element
     */
    abstract fun drawElement()

    /**
     * Check if [x] and [y] is in element border
     */
    open fun isHovering(mouseX: Int, mouseY: Int): Boolean {
        val startX = if (side.horizontal == Side.Horizontal.LEFT)
                        this.x
                    else
                        this.x - width
        return mouseX >= startX && mouseX <= startX + width && mouseY >= this.y && mouseY <= this.y + height
    }
}