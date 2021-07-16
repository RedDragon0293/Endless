package cn.asone.endless.features.module.modules.misc

import cn.asone.endless.event.*
import cn.asone.endless.features.module.AbstractModule
import cn.asone.endless.features.module.ModuleCategory
import cn.asone.endless.utils.ClientUtils
import cn.asone.endless.utils.RenderUtils
import cn.asone.endless.value.*
import net.minecraft.network.play.client.C01PacketChatMessage
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
    }

    override val handledEvents: List<Class<out Event>> = arrayListOf(
            Render2DEvent::class.java,
            Render3DEvent::class.java
    )

    override fun onSendPacket(event: SendPacketEvent) {
        val packet = event.packet
        if (packet is C01PacketChatMessage) {
            ClientUtils.displayChatMessage("WDNMD C01!")
            packet.message = "WDNMD"
        }
    }

    override fun onKey(event: KeyEvent) {
        ClientUtils.displayChatMessage("${event.key} ${event.state}")
    }

    override fun onRender2D(event: Render2DEvent) {
        GL11.glPushMatrix()
        RenderUtils.pre2D()
        RenderUtils.drawRect(10F, 10F, 100F, 100F, Color.red.rgb)
        RenderUtils.post2D()
        GL11.glPopMatrix()
        mc.fontRendererObj.drawStringWithShadow(rotationMode.get(), 200F, 10F, Color.green.rgb)
        mc.fontRendererObj.drawStringWithShadow(rotationSpeed.get().toString(), 200F, 20F, Color.green.rgb)
        mc.fontRendererObj.drawStringWithShadow(staticYaw.get().toString(), 200F, 30F, Color.green.rgb)
        mc.fontRendererObj.drawStringWithShadow(staticPitch.get().toString(), 200F, 40F, Color.green.rgb)
    }

    override fun onRender3D(event: Render3DEvent) {
        GL11.glPushMatrix()
        RenderUtils.pre3D()
        RenderUtils.drawPath(Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ))
        RenderUtils.post3D()
        GL11.glPopMatrix()
    }
}