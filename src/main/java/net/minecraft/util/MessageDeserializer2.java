package net.minecraft.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import net.minecraft.network.PacketBuffer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MessageDeserializer2 extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, @NotNull ByteBuf in, List<Object> out) throws Exception {
        in.markReaderIndex();
        byte[] abyte = new byte[3];

        /*
        int由4字节组成
        此循环的目的是为了找到int开始的字节位
         */
        for (int i = 0; i < abyte.length; ++i) {
            if (!in.isReadable()) {
                in.resetReaderIndex();
                return;
            }

            abyte[i] = in.readByte();

            /*
            大于0意味着此byte的最高位(对应Byte数据类型的符号位)为0
            若最高位为0则意味着此byte并不是int的最高字节位
            参考PacketBuffer#readVarIntFromBuffer()
             */
            if (abyte[i] >= 0) {
                PacketBuffer packetbuffer = new PacketBuffer(Unpooled.wrappedBuffer(abyte));

                try {
                    int j = packetbuffer.readVarIntFromBuffer();

                    if (in.readableBytes() >= j) {
                        out.add(in.readBytes(j));
                        return;
                    }

                    in.resetReaderIndex();
                } finally {
                    packetbuffer.release();
                }

                return;
            }
        }

        throw new CorruptedFrameException("length wider than 21-bit");
    }
}
