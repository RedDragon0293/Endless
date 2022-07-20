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
    /**
     * 若方法返回后参数in中还存在未读数据 (in.readableBytes() > 0), 则剩余的未读数据会在下次接收到新数据包时继续处理 (新的数据会拼接在旧数据后)
     * @param ctx           the {@link ChannelHandlerContext} which this {@link ByteToMessageDecoder} belongs to
     * @param in            the {@link ByteBuf} from which to read data
     * @param out           the {@link List} to which decoded messages should be added
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, @NotNull ByteBuf in, List<Object> out) throws Exception {
        //在成功读取数据包并传入下一个Handler之前都会调用resetReaderIndex()
        //保证了只有在那之后才会重置readerIndex
        in.markReaderIndex();
        byte[] abyte = new byte[3];

        /*
        int由4个字节组成
        此循环的目的是为了找到int开始的字节位,从而读取数据包大小
         */
        for (int i = 0; i < abyte.length; ++i) {
            //若当前接收到的数据包总量不够,则等待更多数据包传入
            if (!in.isReadable()) {
                in.resetReaderIndex();
                return;
            }

            abyte[i] = in.readByte();

            /*
            大于0意味着此byte的最高位(对应Byte数据类型的符号位)为0
            若最高位为0则意味着此byte是int的最高字节位
            参考PacketBuffer#readVarIntFromBuffer()
             */
            if (abyte[i] >= 0) {
                PacketBuffer packetbuffer = new PacketBuffer(Unpooled.wrappedBuffer(abyte));

                try {
                    //数据包大小
                    int size = packetbuffer.readVarIntFromBuffer();

                    //若当前接收到的数据包总量大于目标大小则读取传递至下一个Handler
                    if (in.readableBytes() >= size) {
                        out.add(in.readBytes(size));
                        return;
                    }

                    //否则等待更多的数据包传入
                    //此语句的用意为在能够读取数据包之前每次循环都计算一次数据包目标大小
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
