package net.minecraft.util;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.minecraft.network.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class MessageSerializer extends MessageToByteEncoder<Packet<? extends INetHandler>> {
    private static final Logger logger = LogManager.getLogger();
    private static final Marker RECEIVED_PACKET_MARKER = MarkerManager.getMarker("PACKET_SENT", NetworkManager.logMarkerPackets);
    private final EnumPacketDirection direction;

    public MessageSerializer(EnumPacketDirection direction) {
        this.direction = direction;
    }

    @Override
    protected void encode(@NotNull ChannelHandlerContext ctx, Packet msg, ByteBuf out) throws Exception {
        Integer packetId = ctx.channel().attr(NetworkManager.attrKeyConnectionState).get().getPacketId(this.direction, msg);

        if (logger.isDebugEnabled()) {
            logger.debug(RECEIVED_PACKET_MARKER, "OUT: [{}:{}] {}", new Object[]{ctx.channel().attr(NetworkManager.attrKeyConnectionState).get(), packetId, msg.getClass().getName()});
        }

        if (packetId == null) {
            throw new IOException("Can't serialize unregistered packet");
        } else {
            PacketBuffer packetbuffer = new PacketBuffer(out);
            packetbuffer.writeVarIntToBuffer(packetId);

            try {
                msg.writePacketData(packetbuffer);
            } catch (Throwable throwable) {
                logger.error(throwable);
            }
        }
    }
}
