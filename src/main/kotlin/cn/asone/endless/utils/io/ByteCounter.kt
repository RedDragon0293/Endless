package cn.asone.endless.utils.io

import cn.asone.endless.Endless
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageCodec
import io.netty.handler.codec.MessageToMessageDecoder
import io.netty.handler.codec.MessageToMessageEncoder

class ByteCounter : MessageToMessageCodec<ByteBuf, ByteBuf>() {
    /**
     * @see MessageToMessageEncoder.encode
     */
    override fun encode(ctx: ChannelHandlerContext, msg: ByteBuf, out: MutableList<Any>) {
        Endless.outboundBytes += msg.readableBytes()
        out.add(Unpooled.copiedBuffer(msg))
        msg.readerIndex(msg.writerIndex())
    }

    /**
     * @see MessageToMessageDecoder.decode
     */
    override fun decode(ctx: ChannelHandlerContext, msg: ByteBuf, out: MutableList<Any>) {
        Endless.inboundBytes += msg.readableBytes()
        out.add(Unpooled.copiedBuffer(msg))
        msg.readerIndex(msg.writerIndex())
    }

}