package net.minecraft.network;

import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.src.Config;
import net.minecraft.util.IThreadListener;

public class PacketThreadUtil {
    public static int lastDimensionId = Integer.MIN_VALUE;

    public static <T extends INetHandler> void checkThreadAndEnqueue(final Packet<T> packet, final T netHandler, IThreadListener threadListener) throws ThreadQuickExitException {
        if (!threadListener.isCallingFromMinecraftThread()) {
            threadListener.addScheduledTask(() -> {
                PacketThreadUtil.clientPreProcessPacket(packet);
                packet.processPacket(netHandler);
            });
            throw ThreadQuickExitException.INSTANCE;
        } else {
            clientPreProcessPacket(packet);
        }
    }

    protected static <T extends INetHandler> void clientPreProcessPacket(Packet<T> p_clientPreProcessPacket_0_) {
        if (p_clientPreProcessPacket_0_ instanceof S08PacketPlayerPosLook) {
            Config.getRenderGlobal().onPlayerPositionSet();
        }

        if (p_clientPreProcessPacket_0_ instanceof S07PacketRespawn) {
            S07PacketRespawn s07packetrespawn = (S07PacketRespawn) p_clientPreProcessPacket_0_;
            lastDimensionId = s07packetrespawn.getDimensionID();
        } else if (p_clientPreProcessPacket_0_ instanceof S01PacketJoinGame) {
            S01PacketJoinGame s01packetjoingame = (S01PacketJoinGame) p_clientPreProcessPacket_0_;
            lastDimensionId = s01packetjoingame.getDimension();
        } else {
            lastDimensionId = Integer.MIN_VALUE;
        }
    }
}
