package cn.asone.endless.event

import cn.asone.endless.features.module.AbstractModule
import cn.asone.endless.utils.ClientUtils

object EventManager {
    private val availableEvents = arrayListOf(
            KeyEvent::class.java,
            PreMotionEvent::class.java,
            PostMotionEvent::class.java,
            ReceivePacketEvent::class.java,
            SendPacketEvent::class.java,
            Render2DEvent::class.java,
            Render3DEvent::class.java,
            UpdateEvent::class.java
    )
    private val registry = mutableMapOf<Class<out Event>, ArrayList<Listenable>>()

    init {
        for (event in availableEvents) {
            registry[event] = ArrayList()
        }
    }

    fun registerListener(listener: Listenable) {
        for (event in listener.handledEvents) {
            registry[event]!!.add(listener)
        }
    }

    fun <T : Event> callEvent(event: T) {
        /**
         * 当前event对应的Listenable List
         */
        val targetClass = registry[event.javaClass] ?: return

        when (event) {
            is UpdateEvent -> {
                for (target in targetClass) {
                    if (!target.isHandleEvents())
                        continue
                    runCatching {
                        target.onUpdate()
                    }.onFailure {
                        it.printStackTrace()
                    }
                }
            }

            is PreMotionEvent -> {
                for (target in targetClass) {
                    if (!target.isHandleEvents())
                        continue
                    runCatching {
                        target.onPreMotion(event)
                    }.onFailure {
                        it.printStackTrace()
                    }
                }
            }

            is PostMotionEvent -> {
                for (target in targetClass) {
                    if (!target.isHandleEvents())
                        continue
                    runCatching {
                        target.onPostMotion(event)
                    }.onFailure {
                        it.printStackTrace()
                    }
                }
            }

            is Render2DEvent -> {
                for (target in targetClass) {
                    if (!target.isHandleEvents())
                        continue
                    runCatching {
                        target.onRender2D(event)
                    }.onFailure {
                        it.printStackTrace()
                        if (target is AbstractModule)
                            target.state = false
                    }

                }
            }

            is Render3DEvent -> {
                for (target in targetClass) {
                    if (!target.isHandleEvents())
                        continue
                    runCatching {
                        target.onRender3D(event)
                    }.onFailure {
                        it.printStackTrace()
                        if (target is AbstractModule)
                            target.state = false
                    }
                }
            }

            is KeyEvent -> {
                for (target in targetClass) {
                    if (!target.isHandleEvents())
                        continue
                    runCatching {
                        target.onKey(event)
                    }.onFailure {
                        it.printStackTrace()
                    }
                }
            }

            is ReceivePacketEvent -> {
                for (target in targetClass) {
                    if (!target.isHandleEvents())
                        continue
                    runCatching {
                        target.onReceivePacket(event)
                    }.onFailure {
                        it.printStackTrace()
                    }

                }
            }

            is SendPacketEvent -> {
                for (target in targetClass) {
                    if (!target.isHandleEvents())
                        continue
                    runCatching {
                        target.onSendPacket(event)
                    }.onFailure {
                        it.printStackTrace()
                    }
                }
            }

            else -> {
                ClientUtils.displayChatMessage("[EventManager] Unsupported event detected!")
            }
        }
    }
}