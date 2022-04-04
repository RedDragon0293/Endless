package net.minecraft.util;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.minecraft.network.PacketBuffer;
import org.jetbrains.annotations.NotNull;

/**
 * 出站数据包第二层序列器
 */
public class MessageSerializer2 extends MessageToByteEncoder<ByteBuf> {
    /**
     * 序列化后的数据格式：
     * 数据包大小 + 数据包内容
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, @NotNull ByteBuf msg, ByteBuf out) throws Exception {
        int i = msg.readableBytes();
        int j = PacketBuffer.getVarIntSize(i);

        if (j > 3) {
            throw new IllegalArgumentException("unable to fit " + i + " into " + 3);
        } else {
            PacketBuffer packetbuffer = new PacketBuffer(out);
            packetbuffer.ensureWritable(j + i);
            packetbuffer.writeVarIntToBuffer(i);
            packetbuffer.writeBytes(msg, msg.readerIndex(), i);
        }
    }
}
