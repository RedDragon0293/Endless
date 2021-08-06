package cn.asone.endless.utils

import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation
import net.minecraft.util.Vec3
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.*

/*
glBegin:
GL11: GL_POINTS, GL_LINES, GL_LINE_LOOP, GL_LINE_STRIP, GL_TRIANGLES, GL_TRIANGLE_STRIP, GL_TRIANGLE_FAN, GL_QUADS, GL_QUAD_STRIP, GL_POLYGON
GL32: GL_LINES_ADJACENCY, GL_LINE_STRIP_ADJACENCY, GL_TRIANGLES_ADJACENCY, GL_TRIANGLE_STRIP_ADJACENCY
GL40: GL_PATCHES

    patches:补丁
    quad:四边
    triangles:三角形
    loop:环
    strip:带
    polygon:多边形
*/

object RenderUtils {
    /**
     * Returns the scaled width of the game
     */
    @JvmStatic
    val scaledWidth
        inline get() = ScaledResolution(mc).scaledWidth

    /**
     * Returns the scaled height of the game
     */
    @JvmStatic
    val scaledHeight
        inline get() = ScaledResolution(mc).scaledHeight

    @JvmStatic
    fun doScissor(x1: Int, y1: Int, x2: Int, y2: Int) {
        val width = abs(x1 - x2) * 2
        val height = abs(y1 - y2) * 2
        val xStart = min(x1, x2) * 2
        val yStart = (scaledHeight - max(y1, y2)) * 2
        GL11.glScissor(xStart, yStart, width, height)
    }

    @JvmStatic
    fun pre3D() {
        GlStateManager.enableBlend()
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GlStateManager.disableTexture2D()
        GlStateManager.disableDepth()
        GlStateManager.disableLighting()
        GlStateManager.depthMask(false)
    }

    @JvmStatic
    fun post3D() {
        GlStateManager.depthMask(true)
        GlStateManager.enableDepth()
        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()
        GlStateManager.color(1F, 1F, 1F, 1F)
    }

    @JvmStatic
    fun pre2D() {
        GlStateManager.enableBlend()
        GlStateManager.disableTexture2D()
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
    }

    @JvmStatic
    fun post2D() {
        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()
        GlStateManager.color(1F, 1F, 1F, 1F)
    }

    @JvmStatic
    fun drawRoundedRect(x: Float, y: Float, width: Float, height: Float, radius: Float, color: Int) {
        quickGLColor(color)

        val xStart = x + radius
        val yStart = y + radius
        val xEnd = x + width - radius
        val yEnd = y + height - radius

        GL11.glBegin(GL11.GL_POLYGON)
        for (i in 0..90) GL11.glVertex2d(
            xEnd + sin(Math.toRadians(i.toDouble())) * radius,
            yEnd + cos(Math.toRadians(i.toDouble())) * radius
        )
        for (i in 90..180) GL11.glVertex2d(
            xEnd + sin(Math.toRadians(i.toDouble())) * radius,
            yStart + cos(Math.toRadians(i.toDouble())) * radius
        )
        for (i in 180..270) GL11.glVertex2d(
            xStart + sin(Math.toRadians(i.toDouble())) * radius,
            yStart + cos(Math.toRadians(i.toDouble())) * radius
        )
        for (i in 270..360) GL11.glVertex2d(
            xStart + sin(Math.toRadians(i.toDouble())) * radius,
            yEnd + cos(Math.toRadians(i.toDouble())) * radius
        )
        GL11.glEnd()
    }

    @JvmStatic
    fun drawAntiAliasingRoundedRect(x: Float, y: Float, width: Float, height: Float, radius: Float, color: Int) {
        quickGLColor(color)

        val xStart = x + radius
        val yStart = y + radius
        val xEnd = x + width - radius
        val yEnd = y + height - radius

        GL11.glBegin(GL11.GL_POLYGON)
        for (i in 0..90) GL11.glVertex2d(
            xEnd + sin(Math.toRadians(i.toDouble())) * radius,
            yEnd + cos(Math.toRadians(i.toDouble())) * radius
        )
        for (i in 90..180) GL11.glVertex2d(
            xEnd + sin(Math.toRadians(i.toDouble())) * radius,
            yStart + cos(Math.toRadians(i.toDouble())) * radius
        )
        for (i in 180..270) GL11.glVertex2d(
            xStart + sin(Math.toRadians(i.toDouble())) * radius,
            yStart + cos(Math.toRadians(i.toDouble())) * radius
        )
        for (i in 270..360) GL11.glVertex2d(
            xStart + sin(Math.toRadians(i.toDouble())) * radius,
            yEnd + cos(Math.toRadians(i.toDouble())) * radius
        )
        GL11.glEnd()

        GL11.glLineWidth(1F)
        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST)
        GL11.glBegin(GL11.GL_LINE_LOOP)
        for (i in 0..90) GL11.glVertex2d(
            xEnd + sin(Math.toRadians(i.toDouble())) * radius,
            yEnd + cos(Math.toRadians(i.toDouble())) * radius
        )
        for (i in 90..180) GL11.glVertex2d(
            xEnd + sin(Math.toRadians(i.toDouble())) * radius,
            yStart + cos(Math.toRadians(i.toDouble())) * radius
        )
        for (i in 180..270) GL11.glVertex2d(
            xStart + sin(Math.toRadians(i.toDouble())) * radius,
            yStart + cos(Math.toRadians(i.toDouble())) * radius
        )
        for (i in 270..360) GL11.glVertex2d(
            xStart + sin(Math.toRadians(i.toDouble())) * radius,
            yEnd + cos(Math.toRadians(i.toDouble())) * radius
        )
        GL11.glEnd()
        GL11.glEnable(GL11.GL_LINE_SMOOTH)
    }

    @JvmStatic
    fun drawBorder(x1: Float, y1: Float, x2: Float, y2: Float, width: Float, color: Int) {
        quickGLColor(color)
        GL11.glLineWidth(width)
        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST)
        GL11.glBegin(GL11.GL_LINE_LOOP)
        GL11.glVertex2f(x1, y1)
        GL11.glVertex2f(x2, y1)
        GL11.glVertex2f(x2, y2)
        GL11.glVertex2f(x1, y2)
        GL11.glEnd()
        GL11.glDisable(GL11.GL_LINE_SMOOTH)
        GL11.glLineWidth(1F)
    }

    @JvmStatic
    fun drawRoundedBorder(x1: Float, y1: Float, x2: Float, y2: Float, radius: Float, width: Float, color: Int) {
        quickGLColor(color)

        val xStart = x1 + radius
        val yStart = y1 + radius
        val xEnd = x2 - radius
        val yEnd = y2 - radius

        GL11.glLineWidth(width)
        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        GL11.glBegin(GL11.GL_LINE_LOOP)
        for (i in 0..90) GL11.glVertex2d(
            xEnd + sin(Math.toRadians(i.toDouble())) * radius,
            yEnd + cos(Math.toRadians(i.toDouble())) * radius
        )
        for (i in 90..180) GL11.glVertex2d(
            xEnd + sin(Math.toRadians(i.toDouble())) * radius,
            yStart + cos(Math.toRadians(i.toDouble())) * radius
        )
        for (i in 180..270) GL11.glVertex2d(
            xStart + sin(Math.toRadians(i.toDouble())) * radius,
            yStart + cos(Math.toRadians(i.toDouble())) * radius
        )
        for (i in 270..360) GL11.glVertex2d(
            xStart + sin(Math.toRadians(i.toDouble())) * radius,
            yEnd + cos(Math.toRadians(i.toDouble())) * radius
        )
        GL11.glEnd()
        GL11.glDisable(GL11.GL_LINE_SMOOTH)
        GL11.glLineWidth(1F)
    }

    @JvmStatic
    fun drawRect(x: Float, y: Float, width: Float, height: Float, color: Int) {
        quickGLColor(color)

        GL11.glBegin(GL11.GL_QUADS)
        GL11.glVertex2f(x + width, y)
        GL11.glVertex2f(x, y)
        GL11.glVertex2f(x, y + height)
        GL11.glVertex2f(x + width, y + height)

        GL11.glEnd()
    }

    @JvmStatic
    fun drawCircle(x: Float, y: Float, radius: Float, color: Int) {
        quickGLColor(color)
        GL11.glBegin(GL11.GL_POLYGON)
        for (i in 0..360)
            GL11.glVertex2f(
                (x + sin(Math.toRadians(i.toDouble())) * radius).toFloat(),
                (y + cos(Math.toRadians(i.toDouble())) * radius).toFloat()
            )
        GL11.glEnd()
    }

    @JvmStatic
    fun drawAntiAliasingCircle(x: Float, y: Float, radius: Float, color: Int) {
        quickGLColor(color)
        GL11.glBegin(GL11.GL_POLYGON)
        for (i in 0..360)
            GL11.glVertex2f(
                (x + sin(Math.toRadians(i.toDouble())) * radius).toFloat(),
                (y + cos(Math.toRadians(i.toDouble())) * radius).toFloat()
            )
        GL11.glEnd()

        GL11.glLineWidth(1F)
        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST)
        GL11.glBegin(GL11.GL_LINE_LOOP)
        for (i in 0..360)
            GL11.glVertex2f(
                (x + sin(Math.toRadians(i.toDouble())) * radius).toFloat(),
                (y + cos(Math.toRadians(i.toDouble())) * radius).toFloat()
            )
        GL11.glEnd()
        GL11.glDisable(GL11.GL_LINE_SMOOTH)
    }

    @JvmStatic
    fun drawLine(x1: Float, y1: Float, x2: Float, y2: Float, width: Float, color: Int) {
        quickGLColor(color)
        GL11.glLineWidth(width)
        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST)
        GL11.glBegin(GL11.GL_LINES)
        GL11.glVertex2f(x1, y1)
        GL11.glVertex2f(x2, y2)
        GL11.glEnd()
        GL11.glDisable(GL11.GL_LINE_SMOOTH)
    }

    @JvmStatic
    fun drawImage(image: ResourceLocation, x: Int, y: Int, width: Int, height: Int) {
        /*GL11.glDisable(GL11.GL_DEPTH_TEST)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glDepthMask(false)
        GL14.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO)
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F)*/
        mc.textureManager.bindTexture(image)
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0f, 0f, width, height, width.toFloat(), height.toFloat())
        /*GL11.glDepthMask(true)
        GL11.glDisable(GL11.GL_BLEND)
        GL11.glEnable(GL11.GL_DEPTH_TEST)*/
    }

    @JvmStatic
    fun quickGLColor(hex: Int) {
        val alpha = (hex shr 24 and 0xFF) / 255F
        val red = (hex shr 16 and 0xFF) / 255F
        val green = (hex shr 8 and 0xFF) / 255F
        val blue = (hex and 0xFF) / 255F
        GlStateManager.color(red, green, blue, alpha)
    }

    @JvmStatic
    fun drawPath(vec: Vec3) {
        val renderManager = mc.renderManager
        val x: Double = vec.xCoord - renderManager.renderPosX
        val y: Double = vec.yCoord - renderManager.renderPosY
        val z: Double = vec.zCoord - renderManager.renderPosZ
        val width = 0.3
        val height = 1.8
        //GL11.glLoadIdentity()
        mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 0)
        val colors = intArrayOf(Color.black.rgb, Color.white.rgb)
        for (i in 0..1) {
            quickGLColor(colors[i])
            GL11.glLineWidth((3 - i * 2).toFloat())
            GL11.glBegin(GL11.GL_LINE_STRIP)
            GL11.glVertex3d(x - width, y, z - width)
            GL11.glVertex3d(x - width, y, z - width)
            GL11.glVertex3d(x - width, y + height, z - width)
            GL11.glVertex3d(x + width, y + height, z - width)
            GL11.glVertex3d(x + width, y, z - width)
            GL11.glVertex3d(x - width, y, z - width)
            GL11.glVertex3d(x - width, y, z + width)
            GL11.glEnd()
            GL11.glBegin(GL11.GL_LINE_STRIP)
            GL11.glVertex3d(x + width, y, z + width)
            GL11.glVertex3d(x + width, y + height, z + width)
            GL11.glVertex3d(x - width, y + height, z + width)
            GL11.glVertex3d(x - width, y, z + width)
            GL11.glVertex3d(x + width, y, z + width)
            GL11.glVertex3d(x + width, y, z - width)
            GL11.glEnd()
            GL11.glBegin(GL11.GL_LINE_STRIP)
            GL11.glVertex3d(x + width, y + height, z + width)
            GL11.glVertex3d(x + width, y + height, z - width)
            GL11.glEnd()
            GL11.glBegin(GL11.GL_LINE_STRIP)
            GL11.glVertex3d(x - width, y + height, z + width)
            GL11.glVertex3d(x - width, y + height, z - width)
            GL11.glEnd()
        }
    }
}