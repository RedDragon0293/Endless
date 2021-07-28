package cn.asone.endless.ui.gui.clickgui

import cn.asone.endless.features.module.ModuleManager
import cn.asone.endless.ui.gui.clickgui.elements.CategoryButton
import cn.asone.endless.ui.gui.clickgui.elements.moduleslist.AbstractButton
import cn.asone.endless.ui.gui.clickgui.elements.moduleslist.ModuleButton
import cn.asone.endless.utils.RenderUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.GL11
import java.awt.Color

class ClickGUI : GuiScreen() {

    companion object {
        var guiWidth = 500
        var guiHeight = 300
        var windowXStart = 0
        var windowYStart = 0

        var categoryIndex = 1
            private set
    }

    private val buttonsMap: Map<Int, ArrayList<AbstractButton>> = mapOf()
    private var diffY = 0F
    private val moduleButtons: ArrayList<AbstractButton> = ArrayList()
    private val endlessLogo = ResourceLocation("endless/endless_Logo.png")
    private lateinit var closeButton: GuiButton

    init {
        for (module in ModuleManager.modules)
            buttonsMap[module.category]?.add(ModuleButton(module))
        buttonsMap[categoryIndex - 1]?.let { moduleButtons.addAll(it) }
    }

    override fun initGui() {
        windowXStart = (width / 2) - (guiWidth / 2)
        windowYStart = (height / 2) - (guiHeight / 2)
        closeButton = object : GuiButton(
            0, windowXStart + guiWidth - 10 - 4,
            windowYStart + 10 - 4, 8, 8, ""
        ) {
            override fun drawButton(mc: Minecraft, mouseX: Int, mouseY: Int) {}
        }
        buttonList.add(closeButton)
        val categoryString = arrayOf("Combat", "Player", "Movement", "Render", "World", "Misc", "Target", "Global")
        for (i in 0..categoryString.lastIndex) {
            buttonList.add(
                CategoryButton(
                    i + 1,
                    windowXStart + 2,
                    windowYStart + 4 + 64 + 2 + i * 22,
                    categoryString[i]
                )
            )
        }
        moduleButtons.forEach { it.updateX(windowXStart + 68 + 4F) }
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        GlStateManager.pushMatrix()
        RenderUtils.pre2D()
        //Main background
        RenderUtils.drawRoundedRect(
            windowXStart.toFloat(),
            windowYStart.toFloat(),
            windowXStart.toFloat() + guiWidth,
            windowYStart.toFloat() + guiHeight,
            8F,
            Color(236, 236, 236).rgb
        )
        RenderUtils.drawRoundedBorder(
            windowXStart.toFloat(),
            windowYStart.toFloat(),
            windowXStart.toFloat() + guiWidth,
            windowYStart.toFloat() + guiHeight,
            8F,
            14F,
            Color.white.rgb
        )
        //Close button
        RenderUtils.drawCircle(windowXStart + guiWidth - 10F, windowYStart + 10F, 4F, Color.RED.rgb)
        //Category background
        RenderUtils.drawRect(
            windowXStart + 2F,
            windowYStart.toFloat(),
            windowXStart + 68F,
            (windowYStart + guiHeight).toFloat(),
            Color.white.rgb
        )
        //Current chosen category background
        RenderUtils.drawAntiAliasingRoundedRect(
            windowXStart + 2F,
            windowYStart + 4 + 64 + 2F + 22 * (categoryIndex - 1),
            windowXStart + 64F,
            windowYStart + 4 + 64 + 2F + 22 * categoryIndex - 4,
            4F,
            Color(0, 111, 255).rgb
        )
        if (Mouse.hasWheel()) {
            diffY += Mouse.getDWheel() / 7F
            if (diffY > 0)
                diffY = 0F
        }

        //GL11.glEnable(GL11.GL_SCISSOR_TEST)
        //for ()
        //GL11.glDisable(GL11.GL_SCISSOR_TEST)

        GlStateManager.enableTexture2D()
        GlStateManager.disableDepth()
        GlStateManager.depthMask(false)
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO)
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F)
        GlStateManager.scale(1 / 16F, 1 / 16F, 1 / 16F)
        RenderUtils.drawImage(endlessLogo, (windowXStart + 2) * 16, (windowYStart + 4) * 16, 1024, 1024)
        GlStateManager.scale(16F, 16F, 16F)
        GlStateManager.depthMask(true)
        GlStateManager.enableDepth()
        GlStateManager.disableBlend()

        GlStateManager.pushMatrix()
        for (guiButton in buttonList) {
            guiButton.drawButton(mc, mouseX, mouseY)
        }
        for (guiLabel in labelList) {
            guiLabel.drawLabel(mc, mouseX, mouseY)
        }
        GlStateManager.popMatrix()

        GlStateManager.popMatrix()
    }

    override fun actionPerformed(button: GuiButton) {
        when (button.id) {
            0 -> mc.displayGuiScreen(null)
            else -> {
                diffY = 0F
                if (categoryIndex != button.id) {
                    categoryIndex = button.id
                    if (categoryIndex in 1..6) {
                        moduleButtons.clear()
                        buttonsMap[categoryIndex - 1]?.let { moduleButtons.addAll(it) }
                        moduleButtons.forEach { it.updateX(windowXStart + 68 + 4F) }
                    }
                }
            }
        }
    }

    override fun doesGuiPauseGame() = false
}