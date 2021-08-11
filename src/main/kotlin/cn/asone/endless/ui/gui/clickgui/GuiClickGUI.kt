package cn.asone.endless.ui.gui.clickgui

import cn.asone.endless.features.module.AbstractModule
import cn.asone.endless.features.module.ModuleManager
import cn.asone.endless.features.special.FakeForge
import cn.asone.endless.ui.font.Fonts
import cn.asone.endless.ui.gui.clickgui.elements.CategoryButton
import cn.asone.endless.ui.gui.clickgui.elements.moduleinfo.AbstractValueButton
import cn.asone.endless.ui.gui.clickgui.elements.moduleinfo.ListButton
import cn.asone.endless.ui.gui.clickgui.elements.moduleslist.AbstractButton
import cn.asone.endless.ui.gui.clickgui.elements.moduleslist.DisabledButton
import cn.asone.endless.ui.gui.clickgui.elements.moduleslist.ModuleButton
import cn.asone.endless.utils.RenderUtils
import cn.asone.endless.utils.playSound
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.resources.I18n
import net.minecraft.util.ResourceLocation
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.GL11
import java.awt.Color

class GuiClickGUI : GuiScreen() {

    companion object {
        var guiWidth = 480
        var guiHeight = 250
        var windowXStart = 0
        var windowYStart = 0

        var categoryIndex = 1
            private set
        var keyBindModule: AbstractModule? = null
        var currentInfoButton: AbstractButton? = null
        var listButton: ListButton? = null

        var backgroundColor: Int = Color(236, 236, 236).rgb
        var elementColor: Int = Color.white.rgb
        var textColor: Int = Color.black.rgb

        private lateinit var backButton: GuiButton
        fun settingKeyBind() {
            backButton.visible = true
        }
    }

    private val buttonsMap: Map<Int, ArrayList<AbstractButton>> = mapOf(
        Pair(0, ArrayList()), //Combat
        Pair(1, ArrayList()), //Player
        Pair(2, ArrayList()), //Movement
        Pair(3, ArrayList()), //Render
        Pair(4, ArrayList()), //World
        Pair(5, ArrayList()), //Misc
        Pair(6, ArrayList()), //Targets
        Pair(7, ArrayList())  //Global
    )
    private var listDiffY = 0F
    private var infoDiffY = 0F
    private var valueDiffY = 0F
    private val moduleButtons: ArrayList<AbstractButton> = ArrayList()
    private val endlessLogo = ResourceLocation("endless/endless_Logo.png")
    private lateinit var closeButton: GuiButton

    init {
        for (module in ModuleManager.modules)
            buttonsMap[module.category]?.add(ModuleButton(module))
        buttonsMap[7]?.add(object : DisabledButton("FakeForge") {
            init {
                infoButtons.add(AbstractValueButton.valueToButton(FakeForge.enabled, false))
            }
        })
        buttonsMap[7]?.add(object : DisabledButton("Font") {
            init {
                infoButtons.add(AbstractValueButton.valueToButton(Fonts.forceCustomFont, false))
                infoButtons.add(AbstractValueButton.valueToButton(Fonts.cacheFont, false))
            }
        })
    }

    override fun initGui() {
        closeButton = object : GuiButton(
            0, windowXStart + guiWidth - 10 - 4,
            windowYStart + 10 - 4, 8, 8, ""
        ) {
            override fun drawButton(mc: Minecraft, mouseX: Int, mouseY: Int) {}
        }
        buttonList.add(closeButton)
        backButton = GuiButton(-1, width / 2 - 100, height - 40, I18n.format("gui.back"))
        val categoryString = arrayOf("Combat", "Player", "Movement", "Render", "World", "Misc", "Targets", "Global")
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
        moduleButtons.clear()
        buttonsMap[categoryIndex - 1]?.let { moduleButtons.addAll(it) }
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        GL11.glPushMatrix()
        RenderUtils.pre2D()
        /**
         * Main background
         */
        RenderUtils.drawRoundedRect(
            windowXStart.toFloat(),
            windowYStart.toFloat(),
            guiWidth.toFloat(),
            guiHeight.toFloat(),
            8F,
            backgroundColor
        )
        /**
         * Main background border
         */
        RenderUtils.drawRoundedBorder(
            windowXStart.toFloat(),
            windowYStart.toFloat(),
            windowXStart.toFloat() + guiWidth,
            windowYStart.toFloat() + guiHeight,
            8F,
            14F,
            elementColor
        )
        /**
         * Info button name background
         */
        if (currentInfoButton != null) {
            RenderUtils.drawRect(windowXStart + 231F, windowYStart.toFloat(), guiWidth - 2F - 231, 44F, elementColor)
        }
        /**
         * Close button
         */
        GL11.glEnable(GL11.GL_POLYGON_SMOOTH)
        RenderUtils.drawCircle(windowXStart + guiWidth - 10F, windowYStart + 10F, 4F, Color.RED.rgb)
        GL11.glDisable(GL11.GL_POLYGON_SMOOTH)
        /**
         * Category background
         */
        RenderUtils.drawRect(
            windowXStart + 2F,
            windowYStart.toFloat(),
            66F,
            guiHeight.toFloat(),
            elementColor
        )
        /**
         * Current chosen category background
         */
        RenderUtils.drawRoundedRect(
            windowXStart + 2F,
            windowYStart + 4 + 64 + 2F + 22 * (categoryIndex - 1),
            62F,
            18F,
            4F,
            Color(0, 111, 255).rgb
        )
        /**
         * 计算滚轮
         */
        val wheel = Mouse.getDWheel() / 10F
        if (mouseX in (windowXStart + 68 + 4 - 2)..(windowXStart + 68 + 4 + 150 + 2)
            && mouseY in (windowYStart + 7)..(windowYStart + guiHeight - 7)
            && keyBindModule == null && listButton != null
        ) {
            /**
             * modules list滚轮
             */
            listDiffY += wheel
            if (listDiffY < moduleButtons.size * -33 + guiHeight - 8)
                listDiffY = moduleButtons.size * -33 - 8 + guiHeight.toFloat()
            if (listDiffY > 0)
                listDiffY = 0F
            moduleButtons.forEach { it.offset = listDiffY }
        }
        /**
         * 渲染modules list
         */
        GL11.glEnable(GL11.GL_SCISSOR_TEST)
        RenderUtils.doScissor(
            windowXStart + 70,
            windowYStart + 7,
            windowXStart + 72 + 150 + 2,
            windowYStart + guiHeight - 7
        )
        moduleButtons.forEach {
            if (it.visible)
                it.drawBox(mouseX, mouseY)
        }
        GL11.glDisable(GL11.GL_SCISSOR_TEST)
        /**
         * module list与module info的分割线
         */
        RenderUtils.drawRect(
            windowXStart + 222 + 4F, windowYStart.toFloat(), 5F, guiHeight.toFloat(), elementColor
        )

        /**
         * Logo
         */
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

        /**
         * Category文字
         */
        for (guiButton in buttonList) {
            guiButton.drawButton(mc, mouseX, mouseY)
        }
        /**
         * modules list文字
         */
        GL11.glEnable(GL11.GL_SCISSOR_TEST)
        RenderUtils.doScissor(
            windowXStart + 70,
            windowYStart + 7,
            windowXStart + 72 + 150 + 2,
            windowYStart + guiHeight - 7
        )
        moduleButtons.forEach {
            if (it.visible)
                it.drawText(mouseX, mouseY)
        }
        GL11.glDisable(GL11.GL_SCISSOR_TEST)
        GL11.glPopMatrix()

        /*========================================================================*/

        GL11.glPushMatrix()
        /**
         * Module values
         */
        //x: windowXStart + 231 ~ windowXStart + guiWidth - 7
        if (currentInfoButton != null) {
            if (currentInfoButton!!.infoButtons.isNotEmpty()) {
                /**
                 * Values list滚轮
                 */
                if (mouseX in (windowXStart + 231 + 2)..(windowXStart + guiWidth - 9)
                    && mouseY in (windowYStart + 44 + 2)..(windowYStart + guiHeight - 9)
                    && keyBindModule == null && listButton == null
                ) {
                    infoDiffY += wheel
                    var height = 0F
                    currentInfoButton!!.infoButtons.forEach {
                        height += it.boundingBoxHeight + 6
                    }
                    if (infoDiffY < -height + guiHeight - 8 - 44)
                        infoDiffY = -height + guiHeight - 8F - 44
                    if (infoDiffY > 0)
                        infoDiffY = 0F
                    currentInfoButton!!.infoButtons.forEach { it.updateOffset(infoDiffY) }
                }
                /**
                 * Update values list y position
                 */
                var y = 0F
                currentInfoButton!!.infoButtons.forEach {
                    it.updateY(windowYStart + 44 + 6F + y)
                    y += it.boundingBoxHeight + 6
                }
                /**
                 * Values list box
                 */
                RenderUtils.pre2D()
                GL11.glEnable(GL11.GL_SCISSOR_TEST)
                RenderUtils.doScissor(
                    windowXStart + 231 + 2,
                    windowYStart + 44 + 6,
                    windowXStart + guiWidth - 7 - 2,
                    windowYStart + guiHeight - 6
                )
                currentInfoButton!!.infoButtons.forEach {
                    if (it.visible) {
                        it.drawBox(mouseX, mouseY)
                    }
                }
                GL11.glDisable(GL11.GL_SCISSOR_TEST)
                RenderUtils.post2D()
            }
            /**
             * Name
             */
            Fonts.medium44.drawCenteredString(
                currentInfoButton!!.name,
                windowXStart.toFloat() + (231 - 7 + guiWidth) / 2,
                windowYStart + 8F,
                textColor
            )
            /**
             * Description
             */
            if (currentInfoButton is ModuleButton)
                Fonts.condensedLight16.drawString(
                    (currentInfoButton as ModuleButton).module.description,
                    windowXStart + 231 + 4F,
                    windowYStart + 36F,
                    textColor
                )
            /**
             * Values list text
             */
            if (currentInfoButton!!.infoButtons.isNotEmpty()) {
                GL11.glEnable(GL11.GL_SCISSOR_TEST)
                RenderUtils.doScissor(
                    windowXStart + 231 + 2,
                    windowYStart + 44 + 6,
                    windowXStart + guiWidth - 7 - 2,
                    windowYStart + guiHeight - 6
                )
                currentInfoButton!!.infoButtons.forEach {
                    if (it.visible) {
                        it.drawText(mouseX, mouseY)
                    }
                }
                GL11.glDisable(GL11.GL_SCISSOR_TEST)
            } else
                Fonts.regular20.drawCenteredString(
                    "Nothing to show...",
                    windowXStart.toFloat() + (231 - 7 + guiWidth) / 2,
                    windowYStart.toFloat() + guiHeight / 2,
                    textColor
                )
        } else {
            Fonts.regular20.drawCenteredString(
                "Left click a module to display more info.",
                windowXStart.toFloat() + (231 - 7 + guiWidth) / 2,
                windowYStart.toFloat() + guiHeight / 2,
                textColor
            )
        }
        GL11.glPopMatrix()
        if (keyBindModule != null) {
            drawDefaultBackground()
            backButton.drawButton(mc, mouseX, mouseY)
            mc.fontRendererObj.drawCenteredString(
                "按下一个按键以绑定快捷键至功能 §a${keyBindModule!!.name}§r...",
                width / 2F,
                height / 2F,
                Color.white.rgb,
                false
            )
            mc.fontRendererObj.drawCenteredString(
                "按下ESC或点击下方按钮以返回, 按下Delete键 (不是退格键! Not Backspace!) 以清除快捷键.",
                width / 2F,
                height / 2 + 10F,
                Color.white.rgb,
                false
            )
        }
        if (listButton != null) {
            drawDefaultBackground()
            GL11.glPushMatrix()
            RenderUtils.pre2D()
            /**
             * Title background
             */
            RenderUtils.drawRect(
                windowXStart + guiWidth / 2F - 100,
                windowYStart + guiHeight / 2F - 110,
                200F,
                30F,
                elementColor
            )
            /**
             * List background
             */
            RenderUtils.drawRect(
                windowXStart + guiWidth / 2F - 100,
                windowYStart + guiHeight / 2F - 80,
                200F,
                210F,
                backgroundColor
            )
            if (mouseX in (windowXStart + guiWidth / 2 - 98)..(windowXStart + guiWidth / 2 + 98)
                && mouseY in (windowYStart + guiHeight / 2 - 78)..(windowYStart + guiHeight / 2 + 128)
            ) {
                /**
                 * ListValue 滚轮
                 */
                valueDiffY += wheel
                if (valueDiffY < listButton!!.value.values.size * -(Fonts.light30.height + 4) - 8 + 210)
                    valueDiffY = listButton!!.value.values.size * -(Fonts.light30.height + 4) + 210 - 8F
                if (valueDiffY > 0)
                    valueDiffY = 0F
                /**
                 * calculate the index of the selection hovering currently
                 */
                var i = 0
                while (!(mouseY >= windowYStart + guiHeight / 2 - 80 + 2F + valueDiffY + i * (Fonts.light30.height + 4)
                            && mouseY <= windowYStart + guiHeight / 2 - 80 + 2F + valueDiffY + (i + 1) * (Fonts.light30.height + 4))
                ) {
                    i++
                    if (i >= 100)
                        break
                }
                if (i >= 0 && i <= listButton!!.value.values.lastIndex) {
                    GL11.glEnable(GL11.GL_SCISSOR_TEST)
                    /**
                     * 向内 -2
                     */
                    RenderUtils.doScissor(
                        windowXStart + guiWidth / 2 - 98,
                        windowYStart + guiHeight / 2 - 78,
                        windowXStart + guiWidth / 2 + 98,
                        windowYStart + guiHeight / 2 + 128
                    )
                    /**
                     * Blue border
                     */
                    RenderUtils.drawBorder(
                        windowXStart + guiWidth / 2 - Fonts.light30.getStringWidth(listButton!!.value.values[i]) / 2 - 2F,
                        windowYStart + guiHeight / 2 - 80 + 2F + valueDiffY + i * (Fonts.light30.height + 4),
                        windowXStart + guiWidth / 2 + Fonts.light30.getStringWidth(listButton!!.value.values[i]) / 2 + 2F,
                        windowYStart + guiHeight / 2 - 80 + 2F + valueDiffY + (i + 1) * (Fonts.light30.height + 4) - 2F,
                        1F,
                        Color(0, 111, 255).rgb
                    )
                    GL11.glDisable(GL11.GL_SCISSOR_TEST)
                }

            }
            RenderUtils.post2D()
            Fonts.regular38.drawCenteredString(
                listButton!!.value.name,
                windowXStart + guiWidth / 2F,
                windowYStart + guiHeight / 2F - 110 + 6F,
                textColor
            )
            GL11.glEnable(GL11.GL_SCISSOR_TEST)
            /**
             * 向内 -2
             */
            RenderUtils.doScissor(
                windowXStart + guiWidth / 2 - 98,
                windowYStart + guiHeight / 2 - 78,
                windowXStart + guiWidth / 2 + 98,
                windowYStart + guiHeight / 2 + 128
            )
            var var0 = windowYStart + guiHeight / 2 - 80 + 4F + valueDiffY
            listButton!!.value.values.forEach {
                Fonts.light30.drawCenteredString(it, windowXStart + guiWidth / 2F, var0, textColor)
                var0 += Fonts.light30.height + 4
            }
            GL11.glDisable(GL11.GL_SCISSOR_TEST)
            GL11.glPopMatrix()
        }
    }

    override fun actionPerformed(button: GuiButton) {
        when (button.id) {
            0 -> mc.displayGuiScreen(null)
            else -> {
                listDiffY = 0F
                moduleButtons.forEach { it.offset = 0F }
                if (categoryIndex != button.id) {
                    categoryIndex = button.id
                    moduleButtons.clear()
                    buttonsMap[categoryIndex - 1]?.let { moduleButtons.addAll(it) }
                }
            }
        }
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        if (keyBindModule != null) {
            if (backButton.mousePressed(mc, mouseX, mouseY)) {
                backButton.playPressSound(mc.soundHandler)
                keyBindModule = null
            }
            return
        }
        if (listButton != null) {
            if (mouseX in (windowXStart + guiWidth / 2 - 100)..(windowXStart + guiWidth / 2 + 100)
                && mouseY in (windowYStart + guiHeight / 2 - 80)..(windowYStart + guiHeight / 2 + 130)
            ) {
                var i = 0
                while (!(mouseY >= windowYStart + guiHeight / 2 - 80 + 2F + valueDiffY + i * (Fonts.light30.height + 4)
                            && mouseY <= windowYStart + guiHeight / 2 - 80 + 2F + valueDiffY + (i + 1) * (Fonts.light30.height + 4))
                ) {
                    i++
                    if (i >= 100)
                        break
                }
                if (i >= 0 && i <= listButton!!.value.values.lastIndex) {
                    listButton!!.value.set(listButton!!.value.values[i])
                    listButton!!.updateX(windowXStart + 231 + 6F)
                    mc.soundHandler.playSound("gui.button.press", 1F)
                    listButton = null
                }
            } else
                listButton = null
            return
        }

        if (mouseX in (windowXStart + 70)..(windowXStart + 72 + 150 + 2)
            && mouseY in (windowYStart + 7)..(windowYStart + guiHeight - 7)
        ) {
            for (button in moduleButtons) {
                if (button.isHovering(mouseX, mouseY)) {
                    button.mouseClicked(mouseX, mouseY, mouseButton)
                    break
                }
            }
        }
        if (currentInfoButton != null && currentInfoButton!!.infoButtons.isNotEmpty() &&
            mouseX in (windowXStart + 231 + 2)..(windowXStart + guiWidth - 7 - 2) &&
            mouseY in (windowYStart + 44 + 6)..(windowYStart + guiHeight - 6)
        ) {
            for (button in currentInfoButton!!.infoButtons) {
                if (button.isHovering(mouseX, mouseY)) {
                    button.mouseClicked(mouseX, mouseY, mouseButton)
                    break
                }
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton)
    }

    override fun mouseDragged(mouseX: Int, mouseY: Int, mouseButton: Int, duration: Long) {
        if (keyBindModule != null)
            return
        if (listButton != null) {
            return
        }
        if (currentInfoButton != null && currentInfoButton!!.infoButtons.isNotEmpty() && mouseButton == 0
            && mouseX in (windowXStart + 231 + 2)..(windowXStart + guiWidth - 7 - 2)
            && mouseY in (windowYStart + 44 + 6)..(windowYStart + guiHeight - 6)
        ) {
            for (button in currentInfoButton!!.infoButtons) {
                if (button.isHovering(mouseX, mouseY)) {
                    button.mouseDragged(mouseX, mouseY, mouseButton, duration)
                }
            }
        }
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        if (keyBindModule != null) {
            backButton.visible = false
            if (keyCode == Keyboard.KEY_ESCAPE) {
                keyBindModule = null
                return
            }
            if (keyCode == Keyboard.KEY_DELETE) {
                keyBindModule!!.keyBind = Keyboard.KEY_NONE
                keyBindModule = null
                mc.soundHandler.playSound("random.anvil_use", 1F)
                return
            }
            keyBindModule!!.keyBind = keyCode
            keyBindModule = null
            mc.soundHandler.playSound("random.anvil_use", 1F)
        } else if (listButton != null) {
            if (keyCode == Keyboard.KEY_ESCAPE) {
                listButton = null
            }
            return
        } else
            super.keyTyped(typedChar, keyCode)
    }

    override fun setWorldAndResolution(mc: Minecraft, width: Int, height: Int) {
        windowXStart = (width / 2) - (guiWidth / 2)
        windowYStart = (height / 2) - (guiHeight / 2)
        buttonsMap.values.forEach {
            var y = 0
            it.forEach { button ->
                button.updateX(windowXStart + 68 + 4F)
                button.updateY(windowYStart + 8F + y)
                y += 33
            }
        }
        if (currentInfoButton != null && currentInfoButton!!.infoButtons.isNotEmpty()) {
            currentInfoButton!!.infoButtons.forEach {
                it.updateX(windowXStart + 231 + 6F)
            }
        }
        super.setWorldAndResolution(mc, width, height)
    }

    override fun doesGuiPauseGame() = false
}