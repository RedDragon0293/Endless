package cn.asone.endless.event

interface Listenable {
    val handledEvents: ArrayList<EventHook>
    fun isHandleEvents(): Boolean

    fun onUpdate()
    fun onPreMotion(event: PreMotionEvent)
    fun onPostMotion(event: PostMotionEvent)
    fun onRender2D(event: Render2DEvent)
    fun onRender3D(event: Render3DEvent)
    fun onKey(event: KeyEvent)
    fun onReceivePacket(event: ReceivePacketEvent)
    fun onSendPacket(event: SendPacketEvent)
}