package net.minecraft.util;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

public class MessageDeserializer extends ByteToMessageDecoder {
    private static final Logger logger = LogManager.getLogger();
    private static final Marker RECEIVED_PACKET_MARKER = MarkerManager.getMarker("PACKET_RECEIVED", NetworkManager.logMarkerPackets);
    private final EnumPacketDirection direction;

    public MessageDeserializer(EnumPacketDirection direction) {
        this.direction = direction;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, @NotNull ByteBuf in, List<Object> out) throws IOException, InstantiationException, IllegalAccessException {
        if (in.readableBytes() != 0) {
            PacketBuffer packetbuffer = new PacketBuffer(in);
            int packetId = packetbuffer.readVarIntFromBuffer();
            Packet<?> packet = ctx.channel().attr(NetworkManager.attrKeyConnectionState).get().getPacket(this.direction, packetId);

            if (packet == null) {
                throw new IOException("Bad packet id " + packetId);
            } else {
                packet.readPacketData(packetbuffer);

                if (packetbuffer.readableBytes() > 0) {
                    throw new IOException("Packet " + ctx.channel().attr(NetworkManager.attrKeyConnectionState).get().getId() + "/" + packetId + " (" + packet.getClass().getSimpleName() + ") was larger than I expected, found " + packetbuffer.readableBytes() + " bytes extra whilst reading packet " + packetId);
                } else {
                    out.add(packet);

                    if (logger.isDebugEnabled()) {
                        logger.debug(RECEIVED_PACKET_MARKER, " IN: [{}:{}] {}", new Object[]{ctx.channel().attr(NetworkManager.attrKeyConnectionState).get(), packetId, packet.getClass().getName()});
                    }
                }
            }
        }
    }
}
