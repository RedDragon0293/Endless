package cn.asone.endless.utils.animation

import cn.asone.endless.utils.RenderUtils

class SmoothHelper(
    var currentValue: Float = 0F,
    var lastValue: Float = 0F,
    var delta: Float = 0F,
    var offset: Float = 0F,
    var speed: Float = 0.01F,
    var width: Float = 1F
) {
    fun tick() {
        /**
        实现原理:
        当currentValue更改时, delta将会保存更改后的位置与更改前的位置的x轴像素差
        offset会随着时间的增加而增加
        注意: delta并不是简单的保存currentValue表示的x轴位置
        它是当前元素的实时位置 (包含缓动过程中的偏移量)
         */
        if (lastValue != currentValue) {
            /**
            在表达式中:
            delta * (1 - offset) 表示更改后的x轴位置向上一位置的偏移量
            当更改currentValue后一段时间使offset为1时, 此表达式为0
            若更改currentValue后极短时间内继续更改至第二位置, 则此表达式表示当前元素位置距离第一位置的距离
            在delta的计算中加上此表达式以实现中断动画衔接
             */
            delta = (lastValue - currentValue) * width + (delta * (1 - offset)).toInt()
            offset = 0F
            lastValue = currentValue
        }
        if (offset <= 1F) {
            offset += RenderUtils.deltaTime * speed
            if (offset > 1F) {
                offset = 1F
            }
        }
    }

    fun reset(value: Float) {
        currentValue = value
        lastValue = value
        offset = 1F
    }

    fun get(): Float = ((currentValue * width) + (delta * (1 - offset)).toInt())
}