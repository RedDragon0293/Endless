package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.jetbrains.annotations.NotNull;

import java.util.zip.Deflater;

public class NettyCompressionEncoder extends MessageToByteEncoder<ByteBuf> {
    private final byte[] buffer = new byte[8192];
    private final Deflater deflater;
    private int threshold;

    public NettyCompressionEncoder(int threshold) {
        this.threshold = threshold;
        this.deflater = new Deflater();
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, @NotNull ByteBuf msg, ByteBuf out) throws Exception {
        int i = msg.readableBytes();
        PacketBuffer packetbuffer = new PacketBuffer(out);

        if (i < this.threshold) {
            packetbuffer.writeVarIntToBuffer(0);
            packetbuffer.writeBytes(msg);
        } else {
            byte[] abyte = new byte[i];
            msg.readBytes(abyte);
            /*
            压缩后第一个Int存放解压后数据大小
             */
            packetbuffer.writeVarIntToBuffer(abyte.length);
            this.deflater.setInput(abyte, 0, i);
            this.deflater.finish();

            while (!this.deflater.finished()) {
                int j = this.deflater.deflate(this.buffer);
                packetbuffer.writeBytes(this.buffer, 0, j);
            }

            this.deflater.reset();
        }
    }

    public void setCompressionTreshold(int treshold) {
        this.threshold = treshold;
    }
}
