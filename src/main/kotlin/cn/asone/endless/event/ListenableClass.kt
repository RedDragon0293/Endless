package cn.asone.endless.event

import net.minecraft.client.Minecraft

abstract class ListenableClass : Listenable {
    protected val mc: Minecraft = Minecraft.getMinecraft()

    override fun onUpdate() {}
    override fun onPreMotion(event: PreMotionEvent) {}
    override fun onPostMotion(event: PostMotionEvent) {}
    override fun onRender2D(event: Render2DEvent) {}
    override fun onRender3D(event: Render3DEvent) {}
    override fun onKey(event: KeyEvent) {}
    override fun onReceivePacket(event: ReceivePacketEvent) {}
    override fun onSendPacket(event: SendPacketEvent) {}
}