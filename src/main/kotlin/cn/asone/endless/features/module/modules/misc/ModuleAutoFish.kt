package cn.asone.endless.features.module.modules.misc

import cn.asone.endless.event.EventHook
import cn.asone.endless.event.ReceivePacketEvent
import cn.asone.endless.event.Render2DEvent
import cn.asone.endless.event.UpdateEvent
import cn.asone.endless.features.module.AbstractModule
import cn.asone.endless.features.module.ModuleCategory
import cn.asone.endless.option.AbstractOption
import cn.asone.endless.option.BoolOption
import cn.asone.endless.option.FloatOption
import cn.asone.endless.option.ListOption
import cn.asone.endless.utils.ClientUtils
import cn.asone.endless.utils.misc.MSTimer
import cn.asone.endless.utils.player.RotationUtils
import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.item.ItemFishingRod
import net.minecraft.network.play.server.S45PacketTitle
import java.awt.Color

object ModuleAutoFish : AbstractModule(
    "AutoFish",
    "Auto Fish",
    ModuleCategory.MISC
) {
    private val fishTimer = MSTimer()
    private val orbTimer = MSTimer()
    private var states = 0
    private var standingYPos = 0.0
    private var orb = false
    private var lastX = 0.0
    private var lastY = 0.0
    private var lastZ = 0.0
    private var motionX = 0.0
    private var motionY = 0.0
    private var motionZ = 0.0

    /**
     * 宝球对应的盔甲架
     */
    private var orbEntity: EntityArmorStand? = null

    /**
     * 鱼钩对应的盔甲架
     */
    private var hookArmorStand: EntityArmorStand? = null
    private var yaw = 0F
    private val mode: ListOption = ListOption("Mode", arrayOf("Hypixel"), "Hypixel")
    private val staticYaw: BoolOption = BoolOption("StaticYaw", true)
    private val pitch: FloatOption = FloatOption("RotationPitch", 55F, -90F..90F)

    override val handledEvents: ArrayList<EventHook> = arrayListOf(
        EventHook(UpdateEvent::class.java),
        EventHook(Render2DEvent::class.java),
        EventHook(ReceivePacketEvent::class.java)
    )

    override val options: ArrayList<AbstractOption<*>> = arrayListOf(mode, pitch)

    override fun onUpdate() {
        //钓鱼得有钓鱼竿
        if (mc.thePlayer.heldItem == null || mc.thePlayer.heldItem.item !is ItemFishingRod) {
            states = 0
            orb = false
            return
        }
        val hookEntity = mc.thePlayer.fishEntity
        //纠错机制 避免错误的状态
        if (states != 0 && states != 4 && hookEntity == null && !orb) {
            fishTimer.reset()
            orbTimer.reset()
            orb = false
            states = 0
            mc.thePlayer.rotationPitch = pitch.get()
            mc.thePlayer.rotationYaw = yaw
        }
        //Deactivate orb!!
        if (orb) {
            //寻找宝球对应的盔甲架
            val list = mc.theWorld.loadedEntityList
            var min = Float.MAX_VALUE
            for (i in list) {
                if (i !is EntityArmorStand || i.getCurrentArmor(3) == null) {
                    continue
                }
                val dis = i.getDistanceToEntity(mc.thePlayer)
                if (dis < min) {
                    min = dis
                    orbEntity = i
                }
            }
            if (orbEntity != null) {
                //瞄准
                val rotation = RotationUtils.toRotation(orbEntity!!.getPositionEyes(1F), true)
                mc.thePlayer.rotationYaw = rotation[0]
                mc.thePlayer.rotationPitch = rotation[1]
            }
            if (orbTimer.hasTimePassed(800)) {
                //攻击
                useFishingRod()
                orbTimer.reset()
            }
            return
        }
        //正常钓鱼流程
        if (fishTimer.hasTimePassed(400)) {
            when (states) {
                0 -> { //idle
                    if (hookEntity == null) {
                        useFishingRod()
                        fishTimer.reset()
                    }
                    if (hookEntity != null) {
                        states = 1
                        mc.thePlayer.rotationPitch = pitch.get()
                        mc.thePlayer.rotationYaw = yaw
                    }
                }

                1 -> { //drowning
                    if (hookEntity.motionY > 0) {
                        states = 2
                        val list = mc.theWorld.loadedEntityList
                        var min = Float.MAX_VALUE
                        for (i in list) {
                            if (i !is EntityArmorStand) {
                                continue
                            }
                            val dis = i.getDistanceToEntity(hookEntity)
                            if (dis < min) {
                                min = dis
                                hookArmorStand = i
                            }
                        }
                    }
                }

                2 -> { //floating
                    if (motionY >= 0) {
                        states = 3
                        standingYPos = hookArmorStand!!.posY
                    }
                }

                3 -> { //waiting
                    if (standingYPos - hookArmorStand!!.posY < 0) {
                        standingYPos = hookArmorStand!!.posY
                    }
                    if (standingYPos - hookArmorStand!!.posY >= 0.13 || (motionX != 0.0 && motionZ != 0.0 && motionY < 0.0)) {
                        states = 4
                        useFishingRod()
                    }
                }

                4 -> {
                    if (!orb) {
                        fishTimer.reset()
                        states = 0
                        mc.thePlayer.rotationPitch = pitch.get()
                        mc.thePlayer.rotationYaw = yaw
                    }
                }
            }
        }
        if (hookArmorStand != null) {
            motionX = hookArmorStand!!.posX - lastX
            motionY = hookArmorStand!!.posY - lastY
            motionZ = hookArmorStand!!.posZ - lastZ

            lastX = hookArmorStand!!.posX
            lastY = hookArmorStand!!.posY
            lastZ = hookArmorStand!!.posZ
        }
    }

    override fun onRender2D(event: Render2DEvent) {
        val text = when (states) {
            0 -> "idle"
            1 -> "drowning"
            2 -> "floating"
            3 -> "waiting"
            4 -> "success"
            else -> throw IllegalStateException()
        }
        mc.fontRendererObj.drawString(text, 200, 200, Color.RED.rgb)
        if (mc.thePlayer.heldItem != null && mc.thePlayer.heldItem.item is ItemFishingRod) {
            if (hookArmorStand != null) {
                mc.fontRendererObj.drawString(motionX.toString(), 200, 210, Color.BLUE.rgb)
                mc.fontRendererObj.drawString(motionY.toString(), 200, 220, Color.BLUE.rgb)
                mc.fontRendererObj.drawString(motionZ.toString(), 200, 230, Color.BLUE.rgb)
                mc.fontRendererObj.drawString(
                    (standingYPos - hookArmorStand!!.posY).toString(),
                    200,
                    240,
                    Color.GREEN.rgb
                )
            }
        }
    }

    private fun useFishingRod() {
        if (mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.heldItem)) {
            mc.entityRenderer.itemRenderer.resetEquippedProgress2()
        }
    }

    override fun onEnable() {
        fishTimer.reset()
        orbTimer.reset()
        orb = false
        states = 0
        if (staticYaw.get()) {
            yaw = mc.thePlayer.rotationYaw
        }
    }

    override fun onReceivePacket(event: ReceivePacketEvent) {
        val packet = event.packet
        if (packet is S45PacketTitle && packet.type == S45PacketTitle.Type.TITLE) {
            if (!orb && packet.message.unformattedText.contains("警告")) {
                orb = true
                ClientUtils.chatInfo("!!!Orb!!!")
            }
            if (orb && packet.message.unformattedText.contains("成功")) {
                orb = false
                orbTimer.reset()
                ClientUtils.chatSuccess("Orb deactivated!")
                useFishingRod()
            }
        }
    }
}