package cn.asone.endless.features.module.modules.misc

import cn.asone.endless.event.EventHook
import cn.asone.endless.event.ReceivePacketEvent
import cn.asone.endless.event.Render2DEvent
import cn.asone.endless.event.UpdateEvent
import cn.asone.endless.features.module.AbstractModule
import cn.asone.endless.features.module.ModuleCategory
import cn.asone.endless.option.AbstractOption
import cn.asone.endless.option.FloatOption
import cn.asone.endless.utils.ClientUtils.chatInfo
import cn.asone.endless.utils.ClientUtils.chatSuccess
import cn.asone.endless.utils.misc.MSTimer
import cn.asone.endless.utils.player.RotationUtils.toRotation
import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.item.ItemFishingRod
import net.minecraft.network.play.server.S45PacketTitle
import java.awt.Color
import kotlin.math.abs


object ModuleAutoFish : AbstractModule(
    "AutoFish",
    "Auto Fish",
    ModuleCategory.MISC
) {
    private val fishTimer = MSTimer()
    private val orbTimer = MSTimer()
    private var hookArmorStand: EntityArmorStand? = null
    private var orbEntity: EntityArmorStand? = null
    private var states = 0
    private var orb = false
    private var lifting = false
    private var standingYPos = -1.0
    private var yaw = 0f
    private var lastX = 0.0
    private var lastY = 0.0
    private var lastZ = 0.0
    private var motionX = 0.0
    private var motionY = 0.0
    private var motionZ = 0.0
    private val pitch = FloatOption("Pitch", 55f, -90f..90f)


    override val handledEvents: ArrayList<EventHook> = arrayListOf(
        EventHook(Render2DEvent::class.java),
        EventHook(UpdateEvent::class.java),
        EventHook(ReceivePacketEvent::class.java)
    )

    override val options: ArrayList<AbstractOption<*>> = arrayListOf(pitch)

    override fun onUpdate() {
        if (mc.thePlayer.heldItem == null || mc.thePlayer.heldItem.item !is ItemFishingRod) {
            states = 0
            orb = false
            return
        }
        if (hookArmorStand != null) {
            motionX = hookArmorStand!!.posX - lastX
            motionY = hookArmorStand!!.posY - lastY
            motionZ = hookArmorStand!!.posZ - lastZ
            lastX = hookArmorStand!!.posX
            lastY = hookArmorStand!!.posY
            lastZ = hookArmorStand!!.posZ
        }
        val hookEntity = mc.thePlayer.fishEntity
        if (orb) {
            //寻找宝球对应的盔甲架
            val list: List<Entity> = mc.theWorld.loadedEntityList
            var min = Float.MAX_VALUE
            for (i in list) {
                if (i !is EntityArmorStand || i.getCurrentArmor(3) == null) {
                    continue
                }
                val dis: Float = i.getDistanceToEntity(mc.thePlayer)
                if (dis < min) {
                    min = dis
                    orbEntity = i
                }
            }
            if (orbEntity != null) {
                //瞄准
                val rotation = toRotation(orbEntity!!.getPositionEyes(1f), true, mc.thePlayer)
                mc.thePlayer.rotationYaw = rotation[0]
                mc.thePlayer.rotationPitch = rotation[1]
            }
            if (orbTimer.hasTimePassed(800) && fishTimer.hasTimePassed(1700)) {
                //攻击
                useFishingRod()
                if (mc.thePlayer.fishEntity != null) {
                    useFishingRod()
                }
                orbTimer.reset()
            }
            return
        }
        if (states != 0 && states != 5 && hookEntity == null || fishTimer.hasTimePassed(60000)) {
            fishTimer.reset()
            orbTimer.reset()
            orb = false
            states = 0
            mc.thePlayer.rotationPitch = pitch.get()
            mc.thePlayer.rotationYaw = yaw
        }
        if (fishTimer.hasTimePassed(400)) {
            when (states) {
                0 -> {
                    //idle
                    if (hookEntity == null) {
                        mc.thePlayer.rotationPitch = pitch.get()
                        mc.thePlayer.rotationYaw = yaw
                        useFishingRod()
                    }
                    if (hookEntity != null) {
                        states = 1
                    }
                    fishTimer.reset()
                }

                1 -> {
                    //get armor stand
                    if (fishTimer.hasTimePassed(300)) {
                        val list: List<Entity> = mc.theWorld.loadedEntityList
                        var min = Float.MAX_VALUE
                        for (i in list) {
                            if (i !is EntityArmorStand) {
                                continue
                            }
                            assert(hookEntity != null)
                            val dis: Float = i.getDistanceToEntity(hookEntity)
                            if (dis < min) {
                                min = dis
                                hookArmorStand = i
                            }
                        }
                        states = 2
                        fishTimer.reset()
                    }
                }

                2 -> {
                    // measure
                    if (standingYPos != -1.0) {
                        states = 3
                        return
                    }
                    if (hookArmorStand != null) {
                        if (motionX == 0.0 && motionY == 0.0 && motionZ == 0.0 && fishTimer.hasTimePassed(3000)) {
                            standingYPos = hookArmorStand!!.posY
                        }
                    }
                }

                3 -> {
                    //hook moving
                    if (motionY > 0 || hookArmorStand!!.posY > standingYPos + 0.1) {
                        lifting = true
                    }
                    if ( /*motionX == 0 && motionY == 0 && motionZ == 0 && */abs(standingYPos - hookArmorStand!!.posY) <= 0.07) {
                        states = 4
                        return
                    }
                    if (hookArmorStand!!.posY < standingYPos && (motionY < 0 || (motionY == 0.0 && hookEntity!!.motionY < 0)) && lifting) {
                        lifting = false
                        states = 5
                        chatSuccess("State 3 success")
                        useFishingRod()
                        fishTimer.reset()
                    }
                }

                4 -> {
                    //waiting
                    if (standingYPos - hookArmorStand!!.posY >= 0.08) {
                        states = 5
                        chatSuccess("State 4 success, diff=" + (standingYPos - hookArmorStand!!.posY))
                        useFishingRod()
                        fishTimer.reset()
                    }
                }

                5 -> {
                    //success
                    hookArmorStand = null
                    if (!orb) {
                        states = 0
                        mc.thePlayer.rotationPitch = pitch.get()
                        mc.thePlayer.rotationYaw = yaw
                    }
                }
            }
        }
    }

    override fun onRender2D(event: Render2DEvent) {
        var text = ""
        when (states) {
            0 -> text = "idle"
            1 -> text = "get armor stand"
            2 -> text = "measure"
            3 -> text = "hook moving"
            4 -> text = "waiting"
            5 -> text = "success"
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
                mc.fontRendererObj.drawString(standingYPos.toString(), 200, 250, Color.RED.rgb)
            }
        }
    }

    override fun onReceivePacket(event: ReceivePacketEvent) {
        val packet = event.packet
        if (packet is S45PacketTitle && packet.type == S45PacketTitle.Type.TITLE) {
            if (!orb && packet.message.getUnformattedText().contains("警告")) {
                orb = true
                chatInfo("!!!Orb!!!")
                orbTimer.reset()
                fishTimer.reset()
            }
            if (orb && packet.message.getUnformattedText().contains("成功")) {
                orb = false
                orbTimer.reset()
                chatSuccess("Orb deactivated!")
                useFishingRod()
                fishTimer.reset()
            }
        }
    }

    override fun onEnable() {
        states = 0
        fishTimer.reset()
        orbTimer.reset()
        standingYPos = -1.0
        yaw = mc.thePlayer.rotationYaw
    }

    private fun useFishingRod() {
        if (mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.heldItem)) {
            mc.entityRenderer.itemRenderer.resetEquippedProgress2()
        }
    }
}