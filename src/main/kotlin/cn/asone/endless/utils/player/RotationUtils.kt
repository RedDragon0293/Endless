package cn.asone.endless.utils.player

import cn.asone.endless.utils.extensions.mc
import net.minecraft.entity.Entity
import net.minecraft.util.MathHelper
import net.minecraft.util.Vec3
import kotlin.math.atan2
import kotlin.math.sqrt

object RotationUtils {
    /**
     * Translate vec to rotation
     *
     * @param vec     target vec
     * @param predict predict new location of your body
     * @return rotation
     */
    fun toRotation(vec: Vec3, predict: Boolean, fromEntity: Entity = mc.thePlayer): FloatArray {
        val eyesPos = fromEntity.getPositionEyes(1F)
        if (predict) eyesPos.addVector(fromEntity.motionX, fromEntity.motionY, fromEntity.motionZ)

        val diffVec = vec.subtract(eyesPos)
        return floatArrayOf(
            MathHelper.wrapAngleTo180_float(
                (Math.toDegrees(atan2(diffVec.zCoord, diffVec.xCoord)).toFloat()) - 90f
            ), MathHelper.wrapAngleTo180_float(
                Math.toDegrees(
                    -atan2(
                        diffVec.yCoord,
                        sqrt(diffVec.xCoord * diffVec.xCoord + diffVec.zCoord * diffVec.zCoord)
                    )
                ).toFloat()
            )
        )
    }
}