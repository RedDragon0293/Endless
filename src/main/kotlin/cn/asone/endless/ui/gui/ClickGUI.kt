package cn.asone.endless.ui.gui

import cn.asone.endless.utils.RenderUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import org.lwjgl.opengl.GL11
import java.awt.Color

class ClickGUI : GuiScreen() {
    var guiWidth = 600
    var guiHeight = 300
    var windowXStart = 0
    var windowYStart = 0
    lateinit var closeButton: GuiButton

    override fun initGui() {
        windowXStart = (width / 2) - (guiWidth / 2)
        windowYStart = (height / 2) - (guiHeight / 2)
        closeButton = object : GuiButton(
            0, windowXStart + guiWidth - 10 - 4,
            windowYStart + 10 - 4, ""
        ) {
            init {
                this.width = 8
                this.height = 8
            }

            override fun drawButton(mc: Minecraft, mouseX: Int, mouseY: Int) {
                if (this.visible) {
                    RenderUtils.pre2D()
                    RenderUtils.drawCircle(this.xPosition + 4F, this.yPosition + 4F, 4F, Color.RED.rgb)
                    RenderUtils.post2D()
                }
            }
        }
        buttonList.add(closeButton)
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        GL11.glPushMatrix()
        RenderUtils.pre2D()
        //Main background
        RenderUtils.drawRoundedRect(
            windowXStart.toFloat(),
            windowYStart.toFloat(),
            windowXStart.toFloat() + guiWidth,
            windowYStart.toFloat() + guiHeight,
            8F,
            Color(245, 245, 245).rgb
        )

        RenderUtils.post2D()
        super.drawScreen(mouseX, mouseY, partialTicks)
        GL11.glPopMatrix()
    }

    override fun actionPerformed(button: GuiButton) {
        when (button.id) {
            0 -> mc.displayGuiScreen(null)
        }
    }

    override fun doesGuiPauseGame() = false
}

class CategoryButton(id: Int, x: Int, y: Int, text: String) : GuiButton(id, x, y, text) {
    override fun drawButton(mc: Minecraft?, mouseX: Int, mouseY: Int) {

    }
}