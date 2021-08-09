package cn.asone.endless.ui.utf16font

import java.awt.image.BufferedImage

/**
 * @param char 对应的字符
 * @param bufImg 渲染出的字符图片
 * 为了支持UTF16，所以char是string
 */
class FontChar(val char: String, val bufImg: BufferedImage) {
    val width = bufImg.width
}

