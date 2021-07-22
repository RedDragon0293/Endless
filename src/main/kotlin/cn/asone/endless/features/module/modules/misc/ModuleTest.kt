package cn.asone.endless.features.module.modules.misc

import cn.asone.endless.event.Event
import cn.asone.endless.event.KeyEvent
import cn.asone.endless.event.Render2DEvent
import cn.asone.endless.event.Render3DEvent
import cn.asone.endless.features.module.AbstractModule
import cn.asone.endless.features.module.ModuleCategory
import cn.asone.endless.ui.font.Fonts
import cn.asone.endless.utils.ClientUtils
import cn.asone.endless.utils.RenderUtils
import cn.asone.endless.value.*
import net.minecraft.util.Vec3
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import java.awt.Color

object ModuleTest : AbstractModule(
        "TestModule",
        "Hello, world!",
        ModuleCategory.MISC,
        Keyboard.KEY_R
) {
    private val checkServerSide = BoolValue("CheckServerSide", true)
    private val checkServerSideOnlyGround = BoolValue("CheckServerSideOnlyGround", false)
    private val rotationMode = ListValue("RotationMode", arrayOf("Normal", "Static"), "Normal")
    private val rotationSpeed = IntValue("RotationSpeed", 180, 1, 180)
    private val staticYaw = FloatValue("StaticYaw", 45F, 0F, 180F)
    private val staticPitch = FloatValue("StaticPitch", 90F, -90F, 90F)

    override val values: ArrayList<Value<*>> = arrayListOf(
            checkServerSide,
            rotationMode
    )

    override fun getAllValue(): ArrayList<Value<*>> = arrayListOf(
            checkServerSide,
            checkServerSideOnlyGround,
            rotationMode,
            rotationSpeed,
            staticYaw,
            staticPitch
    )

    init {
        checkServerSide.subValue.add(checkServerSideOnlyGround)
        rotationMode.subValue["Normal"]!!.add(rotationSpeed)
        rotationMode.subValue["Static"]!!.add(staticYaw)
        rotationMode.subValue["Static"]!!.add(staticPitch)
        //font.addGlyphs(0x0000, 0xFFFF)
        //font.effects.add(ColorEffect(Color.red))
        //ClientUtils.logger.info("Generating fonts...")
        //font.loadGlyphs()
        //ClientUtils.logger.info("Done!")
    }

    override val handledEvents: ArrayList<Class<out Event>> = arrayListOf(
            Render2DEvent::class.java,
            Render3DEvent::class.java
    )

    override fun onKey(event: KeyEvent) {
        ClientUtils.displayChatMessage("${event.key} ${event.state}")
    }

    override fun onRender2D(event: Render2DEvent) {
        mc.fontRendererObj.drawStringWithShadow(rotationMode.get(), 200F, 10F, Color.green.rgb)
        mc.fontRendererObj.drawStringWithShadow(rotationSpeed.get().toString(), 200F, 20F, Color.green.rgb)
        mc.fontRendererObj.drawStringWithShadow(staticYaw.get().toString(), 200F, 30F, Color.green.rgb)
        mc.fontRendererObj.drawStringWithShadow(staticPitch.get().toString(), 200F, 40F, Color.green.rgb)
        Fonts.font20.drawString("ABCDEFGHIGKLMNOPQRSTUVWXYZ", 200F, 70F, Color.RED.rgb)
        mc.fontRendererObj.drawString("ABCDEFGHIGKLMNOPQRSTUVWXYZ", 200, 70, Color.BLUE.rgb)

        var y = 70 + Fonts.font20.height + 1.0
        Fonts.font20.drawString("abcdefghijklmnopqrstuvwxyz", 200.0, y, Color.RED.rgb)
        mc.fontRendererObj.drawString("abcdefghijklmnopqrstuvwxyz", 200, y.toInt(), Color.BLUE.rgb)

        y += Fonts.font20.height + 1.0
        Fonts.font20.drawString("1234567890,./?:[]", 200.0, y, Color.RED.rgb)
        mc.fontRendererObj.drawString("1234567890,./?:[]", 200, y.toInt(), Color.BLUE.rgb)
        //GL11.glPushMatrix()
        //GL11.glTranslatef(200F, 100F, 1F)
        //GL11.glScalef(0.25F, 0.25F, 0.25F)
        //font.drawString(0F, 0F, "我操你妈很来劲噢！")
        //GL11.glPopMatrix()
    }

    override fun onRender3D(event: Render3DEvent) {
        GL11.glPushMatrix()
        RenderUtils.pre3D()
        RenderUtils.drawPath(Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ))
        RenderUtils.post3D()
        GL11.glPopMatrix()
    }
}