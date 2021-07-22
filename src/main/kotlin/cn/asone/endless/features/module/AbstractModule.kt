package cn.asone.endless.features.module

import cn.asone.endless.event.*
import cn.asone.endless.utils.playSound
import cn.asone.endless.value.Value
import net.minecraft.client.Minecraft
import org.lwjgl.input.Keyboard

abstract class AbstractModule(
    var name: String,
    var description: String,
    var category: Int,
    var keyBind: Int = Keyboard.CHAR_NONE
) : Listenable {

    protected val mc = Minecraft.getMinecraft()!!
    var state = false
        set(value) {
            if (field == value)
                return
            if (mc.thePlayer != null && mc.theWorld != null) {
                mc.soundHandler.playSound("random.click", 1F)
                if (value)
                    onEnable()
                else
                    onDisable()
            }
            field = value
        }

    open val values: ArrayList<Value<*>> = arrayListOf()

    open fun getAllValue(): ArrayList<Value<*>> = arrayListOf()

    fun getValue(value: String) = getAllValue().find { it.name.equals(value, true) }

    fun toggle() {
        state = !state
    }

    open fun onEnable() {}
    open fun onDisable() {}

    override fun onUpdate() {}
    override fun onPreMotion(event: PreMotionEvent) {}
    override fun onPostMotion(event: PostMotionEvent) {}
    override fun onRender2D(event: Render2DEvent) {}
    override fun onRender3D(event: Render3DEvent) {}
    override fun onKey(event: KeyEvent) {}
    override fun onReceivePacket(event: ReceivePacketEvent) {}
    override fun onSendPacket(event: SendPacketEvent) {}

    override fun isHandleEvents() = state
}