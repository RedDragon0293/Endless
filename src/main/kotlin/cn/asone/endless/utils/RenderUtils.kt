package cn.asone.endless.utils

import net.minecraft.client.gui.ScaledResolution
import net.minecraft.util.Vec3
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

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
    val scaledWidth
        inline get() = ScaledResolution(mc).scaledWidth

    /**
     * Returns the scaled height of the game
     */
    val scaledHeight
        inline get() = ScaledResolution(mc).scaledHeight

    @JvmStatic
    fun doScissor(x1: Int, y1: Int, x2: Int, y2: Int) {
        val width = abs(x1 - x2) * 2
        val height = abs(y1 - y2) * 2
        val xStart = x1.coerceAtMost(x2) * 2
        val yStart = (scaledHeight - y1.coerceAtLeast(y2)) * 2
        GL11.glScissor(xStart, yStart, width, height)
    }

    @JvmStatic
    fun pre3D() {
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        //GL11.glShadeModel(GL11.GL_SMOOTH)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        GL11.glDisable(GL11.GL_DEPTH_TEST)
        GL11.glDisable(GL11.GL_LIGHTING)
        GL11.glDepthMask(false)
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST)
    }

    @JvmStatic
    fun post3D() {
        GL11.glDepthMask(true)
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glDisable(GL11.GL_LINE_SMOOTH)
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glDisable(GL11.GL_BLEND)
        //GL11.glColor4f(1f, 1f, 1f, 1f)
    }

    @JvmStatic
    fun pre2D() {
        GL11.glEnable(GL11.GL_BLEND)
        //GL11.glEnable(GL11.GL_ALPHA);
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glEnable(GL11.GL_LINE_SMOOTH)
    }

    @JvmStatic
    fun post2D() {
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glDisable(GL11.GL_BLEND)
        //GL11.glDisable(GL11.GL_ALPHA)
        GL11.glDisable(GL11.GL_LINE_SMOOTH)
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F)
    }

    @JvmStatic
    fun drawRoundedRect(x1: Float, y1: Float, x2: Float, y2: Float, radius: Float, color: Int) {
        quickGLColor(color)

        val xStart = x1 + radius
        val yStart = y1 + radius
        val xEnd = x2 - radius
        val yEnd = y2 - radius

        //GL11.glEnable(GL11.GL_POLYGON_SMOOTH)
        //GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_NICEST)
        //GL11.glShadeModel(GL11.GL_SMOOTH)
        GL11.glBegin(GL11.GL_POLYGON)
        val degree = Math.PI / 180
        for (i in 0..90) GL11.glVertex2d(xEnd + sin((i * degree)) * radius, yEnd + cos((i * degree)) * radius)
        //for (i in IntRange(floor(yEnd).toInt(), ceil(yStart).toInt())) GL11.glVertex2d(x2.toDouble(), i.toDouble())
        for (i in 90..180) GL11.glVertex2d(xEnd + sin((i * degree)) * radius, yStart + cos((i * degree)) * radius)
        //for (i in IntRange())
        for (i in 180..270) GL11.glVertex2d(xStart + sin((i * degree)) * radius, yStart + cos((i * degree)) * radius)
        for (i in 270..360) GL11.glVertex2d(xStart + sin((i * degree)) * radius, yEnd + cos((i * degree)) * radius)
        //for (i in IntRange(ceil(xStart).toInt(), floor(xEnd).toInt())) GL11.glVertex2d(i.toDouble(), y2.toDouble())
        GL11.glEnd()
        //GL11.glDisable(GL11.GL_POLYGON_SMOOTH)
        //GL11.glShadeModel(GL11.GL_FLAT)
    }

    @JvmStatic
    fun drawBorder(x1: Float, y1: Float, x2: Float, y2: Float, width: Float, color: Int) {
        quickGLColor(color)
        GL11.glLineWidth(width)

        GL11.glBegin(GL11.GL_LINE_LOOP)
        GL11.glVertex2f(x1, y1)
        GL11.glVertex2f(x2, y1)
        GL11.glVertex2f(x2, y2)
        GL11.glVertex2f(x1, y2)
        GL11.glEnd()
    }

    @JvmStatic
    fun drawRect(x1: Float, y1: Float, x2: Float, y2: Float, color: Int) {
        quickGLColor(color)

        GL11.glBegin(GL11.GL_QUADS)
        GL11.glVertex2f(x2, y1)
        GL11.glVertex2f(x1, y1)
        GL11.glVertex2f(x1, y2)
        GL11.glVertex2f(x2, y2)

        GL11.glEnd()
    }

    @JvmStatic
    fun drawCircle(x: Float, y: Float, radius: Float, color: Int) {
        quickGLColor(color)
        GL11.glEnable(GL11.GL_POLYGON_SMOOTH)
        GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_NICEST)
        GL11.glBegin(GL11.GL_POLYGON)
        for (i in 0..360)
            GL11.glVertex2f(
                (x + sin(Math.toRadians(i.toDouble())) * radius).toFloat(),
                (y + cos(Math.toRadians(i.toDouble())) * radius).toFloat()
            )
        GL11.glEnd()
        GL11.glDisable(GL11.GL_POLYGON_SMOOTH)
    }

    @JvmStatic
    fun drawLine(x1: Float, y1: Float, x2: Float, y2: Float, width: Float, color: Int) {
        quickGLColor(color)
        GL11.glLineWidth(width)

        GL11.glBegin(GL11.GL_LINES)
        GL11.glVertex2f(x1, y1)
        GL11.glVertex2f(x2, y2)
        GL11.glEnd()
    }

    @JvmStatic
    fun quickGLColor(hex: Int) {
        val alpha = (hex shr 24 and 0xFF) / 255F
        val red = (hex shr 16 and 0xFF) / 255F
        val green = (hex shr 8 and 0xFF) / 255F
        val blue = (hex and 0xFF) / 255F
        GL11.glColor4f(red, green, blue, alpha)
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