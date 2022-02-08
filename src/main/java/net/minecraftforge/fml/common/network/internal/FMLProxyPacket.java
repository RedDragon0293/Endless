package net.minecraftforge.fml.common.network.internal;

import com.google.common.collect.ConcurrentHashMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S3FPacketCustomPayload;

import java.io.IOException;
import java.util.List;

public class FMLProxyPacket implements Packet<INetHandler> {
    final String channel;
    private final PacketBuffer payload;
    private INetHandler netHandler;
    private static Multiset<String> badPackets = ConcurrentHashMultiset.create();
    //private static int packetCountWarning = Integers.parseInt(System.getProperty("fml.badPacketCounter", "100"), 100);
    private static int packetCountWarning = Integer.parseInt(System.getProperty("fml.badPacketCounter", "100"));

    public FMLProxyPacket(S3FPacketCustomPayload original) {
        this(original.getBufferData(), original.getChannelName());
    }

    public FMLProxyPacket(C17PacketCustomPayload original) {
        this(original.getBufferData(), original.getChannelName());
    }

    public FMLProxyPacket(PacketBuffer payload, String channel) {
        this.channel = channel;
        this.payload = payload;
    }

    @Override
    public void readPacketData(PacketBuffer packetbuffer) throws IOException {
        // NOOP - we are not built this way
    }

    @Override
    public void writePacketData(PacketBuffer packetbuffer) throws IOException {
        // NOOP - we are not built this way
    }

    @Override
    public void processPacket(INetHandler handler) {

    }

    public String channel() {
        return channel;
    }

    public ByteBuf payload() {
        return payload;
    }

    public INetHandler handler() {
        return netHandler;
    }

    public Packet<INetHandlerPlayServer> toC17Packet() {
        return new C17PacketCustomPayload(channel, payload);
    }

    static final int PART_SIZE = 0x1000000 - 0x50; // Make it a constant so that it gets inlined below.
    public static final int MAX_LENGTH = PART_SIZE * 255;

    public List<Packet<INetHandlerPlayClient>> toS3FPackets() throws IOException {
        List<Packet<INetHandlerPlayClient>> ret = Lists.newArrayList();
        byte[] data = payload.array();

        if (data.length < PART_SIZE) {
            ret.add(new S3FPacketCustomPayload(channel, new PacketBuffer(payload.duplicate())));
        } else {
            int parts = (int) Math.ceil(data.length / (double) (PART_SIZE - 1)); //We add a byte header so -1
            if (parts > 255) {
                throw new IllegalArgumentException("Payload may not be larger than " + MAX_LENGTH + " bytes");
            }
            PacketBuffer preamble = new PacketBuffer(Unpooled.buffer());
            preamble.writeString(channel);
            preamble.writeByte(parts);
            preamble.writeInt(data.length);
            ret.add(new S3FPacketCustomPayload("FML|MP", preamble));

            int offset = 0;
            for (int x = 0; x < parts; x++) {
                int length = Math.min(PART_SIZE, data.length - offset + 1);
                byte[] tmp = new byte[length];
                tmp[0] = (byte) (x & 0xFF);
                System.arraycopy(data, offset, tmp, 1, tmp.length - 1);
                offset += tmp.length - 1;
                ret.add(new S3FPacketCustomPayload("FML|MP", new PacketBuffer(Unpooled.wrappedBuffer(tmp))));
            }
        }
        return ret;
    }

    public NetworkManager getOrigin() {
        return null;
    }

    public FMLProxyPacket copy() {
        FMLProxyPacket pkt = new FMLProxyPacket(new PacketBuffer(payload.duplicate()), channel);
        pkt.netHandler = netHandler;
        return pkt;
    }
}