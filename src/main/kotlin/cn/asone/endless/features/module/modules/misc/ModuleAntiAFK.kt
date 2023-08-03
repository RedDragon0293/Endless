package cn.asone.endless.features.module.modules.misc

import cn.asone.endless.event.EventHook
import cn.asone.endless.event.UpdateEvent
import cn.asone.endless.features.module.AbstractModule
import cn.asone.endless.features.module.ModuleCategory
import cn.asone.endless.option.AbstractOption
import cn.asone.endless.option.FloatOption
import cn.asone.endless.option.IntOption
import cn.asone.endless.option.ListOption
import cn.asone.endless.utils.misc.MSTimer

object ModuleAntiAFK : AbstractModule(
    "AntiAFK",
    "Anti AFK",
    ModuleCategory.MISC
) {
    private val timer = MSTimer()
    private var stage = false
    private var posY = -1.0
    private val mode = ListOption("Mode", arrayOf("Jump", "FlyingJitter", "FlyingStable"), "Jump")
    private val delay = IntOption("Delay", 60000, 0..600000)
    private val range = FloatOption("Range", 2F, 0.1F..100F)
    override val handledEvents: ArrayList<EventHook> = arrayListOf(EventHook(UpdateEvent::class.java))
    override val options: ArrayList<AbstractOption<*>> = arrayListOf(mode, delay)

    init {
        mode.subOptions["FlyingJitter"]!!.add(range)
        mode.subOptions["FlyingStable"]!!.add(range)
    }
    override fun onUpdate() {
        if (timer.hasTimePassed(delay.get().toLong())) {
            when (mode.get()) {
                "Jump" -> {
                    mc.thePlayer.jump()
                }

                "FlyingJitter" -> {
                    if (stage) {
                        mc.gameSettings.keyBindSneak.pressed = false
                        mc.gameSettings.keyBindJump.pressed = true
                    } else {
                        mc.gameSettings.keyBindJump.pressed = false
                        mc.gameSettings.keyBindSneak.pressed = true
                    }
                    if (mc.thePlayer.posY < posY - (range.get() / 2F)) {
                        //mc.thePlayer.motionY += mc.thePlayer.capabilities.flySpeed * 3.0
                        stage = true
                    } else if (mc.thePlayer.posY > posY + (range.get() / 2F)) {
                        //mc.thePlayer.motionY -= mc.thePlayer.capabilities.flySpeed * 3.0
                        stage = false
                    }
                }

                "FlyingStable" -> {
                    if (stage) {
                        mc.thePlayer.motionY += mc.thePlayer.capabilities.flySpeed * 3.0
                    } else {
                        mc.thePlayer.motionY -= mc.thePlayer.capabilities.flySpeed * 3.0
                    }
                    if (mc.thePlayer.posY < posY - (range.get() / 2F)) {
                        stage = true
                    } else if (mc.thePlayer.posY > posY + (range.get() / 2F)) {
                        stage = false
                    }
                }
            }
            timer.reset()
        }
    }

    override fun onEnable() {
        timer.reset()
        posY = mc.thePlayer.posY
    }

    override fun onDisable() {
        mc.gameSettings.keyBindJump.pressed = false
        mc.gameSettings.keyBindSneak.pressed = false
    }
}