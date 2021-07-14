package cn.asone.endless.utils

import net.minecraft.client.audio.PositionedSoundRecord
import net.minecraft.client.audio.SoundHandler
import net.minecraft.util.ResourceLocation

fun SoundHandler.playSound(name: String, pitch: Float) {
    mc.soundHandler.playSound(PositionedSoundRecord.create(ResourceLocation(name), pitch))
}