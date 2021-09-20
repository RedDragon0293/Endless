package cn.asone.endless.ui.font

import net.minecraft.client.renderer.texture.DynamicTexture
import java.awt.image.BufferedImage

/**
 * @param char 对应的字符
 * @param bufferedImage 渲染出的字符图片
 */
class FontChar(val char: Char, bufferedImage: BufferedImage) {
    /**
     * 最终用于渲染的图片
     * @see net.minecraft.client.renderer.GlStateManager.bindTexture
     */
    val tex = DynamicTexture(bufferedImage)
}
