package cn.asone.endless.ui.gui.clickgui

import cn.asone.endless.features.module.AbstractModule
import cn.asone.endless.features.module.ModuleManager
import cn.asone.endless.features.special.FakeForge
import cn.asone.endless.ui.font.CFontRenderer
import cn.asone.endless.ui.font.Fonts
import cn.asone.endless.ui.gui.clickgui.elements.CategoryButton
import cn.asone.endless.ui.gui.clickgui.elements.moduleslist.AbstractButton
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

class ClickGUI : GuiScreen() {

    companion object {
        var guiWidth = 480
        var guiHeight = 250
        var windowXStart = 0
        var windowYStart = 0

        var categoryIndex = 1
            private set
        var keyBindModule: AbstractModule? = null
        var currentInfoButton: AbstractButton? = null

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
    private var diffY = 0F
    private val moduleButtons: ArrayList<AbstractButton> = ArrayList()
    private val endlessLogo = ResourceLocation("endless/endless_Logo.png")
    private val moduleInfoFont = CFontRenderer(Fonts.getAssetsFont("Roboto-Medium.ttf", 44), true, false)
    private val descriptionFont = CFontRenderer(Fonts.getAssetsFont("Roboto-Thin.ttf", 16), true, true)
    private lateinit var closeButton: GuiButton

    init {
        for (module in ModuleManager.modules)
            buttonsMap[module.category]?.add(ModuleButton(module))
        buttonsMap[categoryIndex - 1]?.let { moduleButtons.addAll(it) }
        buttonsMap[7]?.add(object : AbstractButton("FakeForge") {

            override var state: Boolean
                get() = FakeForge.enabled.get()
                set(value) {
                    FakeForge.enabled.set(value)
                }
            //override val infoValues: ArrayList<AbstractValueButton>
            //get() = FakeForge.values
        })
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
        moduleButtons.forEach { it.updateX(windowXStart + 68 + 4F) }
        var y = 0
        for (button in moduleButtons) {
            button.updateY(windowYStart + 8F + y)
            y += 33
        }
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        GL11.glPushMatrix()
        RenderUtils.pre2D()
        //Main background
        RenderUtils.drawRoundedRect(
            windowXStart.toFloat(),
            windowYStart.toFloat(),
            guiWidth.toFloat(),
            guiHeight.toFloat(),
            8F,
            Color(236, 236, 236).rgb
        )
        //Main background border
        RenderUtils.drawRoundedBorder(
            windowXStart.toFloat(),
            windowYStart.toFloat(),
            windowXStart.toFloat() + guiWidth,
            windowYStart.toFloat() + guiHeight,
            8F,
            14F,
            Color.white.rgb
        )
        if (currentInfoButton != null) {
            RenderUtils.drawRect(windowXStart + 231F, windowYStart.toFloat(), guiWidth - 2F - 231, 44F, Color.white.rgb)
        }
        //Close button
        GL11.glEnable(GL11.GL_POLYGON_SMOOTH)
        RenderUtils.drawCircle(windowXStart + guiWidth - 10F, windowYStart + 10F, 4F, Color.RED.rgb)
        GL11.glDisable(GL11.GL_POLYGON_SMOOTH)
        //Category background
        RenderUtils.drawRect(
            windowXStart + 2F,
            windowYStart.toFloat(),
            66F,
            guiHeight.toFloat(),
            Color.white.rgb
        )
        //Current chosen category background
        RenderUtils.drawRoundedRect(
            windowXStart + 2F,
            windowYStart + 4 + 64 + 2F + 22 * (categoryIndex - 1),
            62F,
            18F,
            4F,
            Color(0, 111, 255).rgb
        )
        //处理滚轮
        val wheel = Mouse.getDWheel() / 10F
        if (mouseX in (windowXStart + 68 + 4 - 2)..(windowXStart + 68 + 4 + 150 + 2)
            && mouseY in (windowYStart + 7)..(windowYStart + guiHeight - 7)
        ) {
            diffY += wheel
            if (diffY < moduleButtons.size * -33 + guiHeight - 8)
                diffY = moduleButtons.size * -33 - 8 + guiHeight.toFloat()
            if (diffY > 0)
                diffY = 0F
            moduleButtons.forEach { it.offset = diffY }
        }
        //渲染modules list
        GL11.glEnable(GL11.GL_SCISSOR_TEST)
        RenderUtils.doScissor(
            windowXStart + 70,
            windowYStart + 7,
            windowXStart + 72 + 150 + 2,
            windowYStart + guiHeight - 7
        )
        moduleButtons.forEach {
            if (it.visible)
                it.drawBox()
        }
        GL11.glDisable(GL11.GL_SCISSOR_TEST)
        //module list与module info的分割线
        RenderUtils.drawRect(
            windowXStart + 222 + 4F, windowYStart.toFloat(), 5F, guiHeight.toFloat(), Color.white.rgb
        )

        //Logo
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

        //Category文字
        for (guiButton in buttonList) {
            guiButton.drawButton(mc, mouseX, mouseY)
        }
        //modules list文字
        GL11.glEnable(GL11.GL_SCISSOR_TEST)
        RenderUtils.doScissor(
            windowXStart + 70,
            windowYStart + 7,
            windowXStart + 72 + 150 + 2,
            windowYStart + guiHeight - 7
        )
        moduleButtons.forEach {
            if (it.visible)
                it.drawText()
        }
        GL11.glDisable(GL11.GL_SCISSOR_TEST)
        GL11.glPopMatrix()


        GL11.glPushMatrix()
        //Module info
        //x: windowXStart + 231 ~ windowXStart + guiWidth - 7
        if (currentInfoButton != null) {
            moduleInfoFont.drawCenteredString(
                currentInfoButton!!.name,
                windowXStart.toFloat() + (231 - 7 + guiWidth) / 2,
                windowYStart + 8F,
                Color.black.rgb
            )
            //Description
            if (currentInfoButton is ModuleButton)
                descriptionFont.drawString(
                    (currentInfoButton as ModuleButton).module.description,
                    windowXStart + 231 + 4F,
                    windowYStart + 36F,
                    Color.black.rgb
                )
            if (currentInfoButton!!.infoValues.isNotEmpty()) {

            }
        } else {
            Fonts.font20.drawCenteredString(
                "Left click a module to display more info.",
                windowXStart.toFloat() + (231 - 7 + guiWidth) / 2,
                windowYStart.toFloat() + guiHeight / 2,
                Color.black.rgb
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
    }

    override fun actionPerformed(button: GuiButton) {
        when (button.id) {
            0 -> mc.displayGuiScreen(null)
            else -> {
                diffY = 0F
                if (categoryIndex != button.id) {
                    categoryIndex = button.id
                    moduleButtons.clear()
                    buttonsMap[categoryIndex - 1]?.let { moduleButtons.addAll(it) }
                    moduleButtons.forEach { it.updateX(windowXStart + 68 + 4F) }
                    var y = 0
                    for (i in moduleButtons) {
                        i.updateY(windowYStart + 8F + y)
                        y += 33
                    }

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

        for (button in moduleButtons) {
            if (button.isHovering(mouseX, mouseY)) {
                button.mouseClicked(mouseX, mouseY, mouseButton)
                break
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton)
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
        } else
            super.keyTyped(typedChar, keyCode)
    }

    override fun doesGuiPauseGame() = false
}