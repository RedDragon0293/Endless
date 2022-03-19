package cn.asone.endless.event

import cn.asone.endless.features.module.AbstractModule
import cn.asone.endless.utils.ClientUtils
import java.util.*

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
    private val registry = mutableMapOf<Class<out Event>, ArrayList<EventTarget>>()

    init {
        for (event in availableEvents) {
            registry[event] = ArrayList()
        }
    }

    fun registerListener(listener: Listenable) {
        for (eventHook in listener.handledEvents) {
            registry[eventHook.event]!!.add(EventTarget(listener, eventHook.priority))
        }
    }

    fun removeListener(listener: Listenable) {
        for (eventHook in listener.handledEvents) {
            for (it in registry[eventHook.event]!!.indices) {
                if (Objects.equals(registry[eventHook.event]!![it].targetClass, listener)) {
                    registry[eventHook.event]!!.removeAt(it)
                }
            }
        }
    }

    fun sort() = registry.forEach { (_, u) ->
        u.sortBy { -it.priority }
    }

    fun <T : Event> callEvent(event: T) {
        /**
         * 当前event对应的Listenable List
         */
        val eventTarget = registry[event.javaClass] ?: return

        when (event) {
            is UpdateEvent -> {
                for (target in eventTarget) {
                    if (!target.targetClass.isHandleEvents())
                        continue
                    runCatching {
                        target.targetClass.onUpdate()
                    }.onFailure {
                        it.printStackTrace()
                    }
                }
            }

            is PreMotionEvent -> {
                for (target in eventTarget) {
                    if (!target.targetClass.isHandleEvents())
                        continue
                    runCatching {
                        target.targetClass.onPreMotion(event)
                    }.onFailure {
                        it.printStackTrace()
                    }
                }
            }

            is PostMotionEvent -> {
                for (target in eventTarget) {
                    if (!target.targetClass.isHandleEvents())
                        continue
                    runCatching {
                        target.targetClass.onPostMotion(event)
                    }.onFailure {
                        it.printStackTrace()
                    }
                }
            }

            is Render2DEvent -> {
                for (target in eventTarget) {
                    if (!target.targetClass.isHandleEvents())
                        continue
                    runCatching {
                        target.targetClass.onRender2D(event)
                    }.onFailure {
                        it.printStackTrace()
                        if (target.targetClass is AbstractModule)
                            target.targetClass.state = false
                    }

                }
            }

            is Render3DEvent -> {
                for (target in eventTarget) {
                    if (!target.targetClass.isHandleEvents())
                        continue
                    runCatching {
                        target.targetClass.onRender3D(event)
                    }.onFailure {
                        it.printStackTrace()
                        if (target.targetClass is AbstractModule)
                            target.targetClass.state = false
                    }
                }
            }

            is KeyEvent -> {
                for (target in eventTarget) {
                    if (!target.targetClass.isHandleEvents())
                        continue
                    runCatching {
                        target.targetClass.onKey(event)
                    }.onFailure {
                        it.printStackTrace()
                    }
                }
            }

            is ReceivePacketEvent -> {
                for (target in eventTarget) {
                    if (!target.targetClass.isHandleEvents())
                        continue
                    runCatching {
                        target.targetClass.onReceivePacket(event)
                    }.onFailure {
                        it.printStackTrace()
                    }

                }
            }

            is SendPacketEvent -> {
                for (target in eventTarget) {
                    if (!target.targetClass.isHandleEvents())
                        continue
                    runCatching {
                        target.targetClass.onSendPacket(event)
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