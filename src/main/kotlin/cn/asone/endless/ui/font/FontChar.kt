package cn.asone.endless.ui.font

import net.minecraft.client.renderer.texture.DynamicTexture
import java.awt.image.BufferedImage

/**
 * @param char 对应的字符
 * @param bufferedImage 渲染出的字符图片
 */
class FontChar(val char: Char, val bufferedImage: BufferedImage) {
    val width = bufferedImage.width
    val tex = DynamicTexture(bufferedImage)
}
