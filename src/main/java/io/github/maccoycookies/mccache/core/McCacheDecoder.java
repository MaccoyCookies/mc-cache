package io.github.maccoycookies.mccache.core;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class McCacheDecoder extends ByteToMessageDecoder {

    AtomicLong counter = new AtomicLong();

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {

        System.out.println("McCacheDecoder decodeCount: " + counter.incrementAndGet());
        if (in.readableBytes() <= 0) {
            return;
        }
        int count = in.readableBytes();
        int index = in.readerIndex();
        System.out.println("count: " + count + ", index: " + index);
        byte[] bytes = new byte[count];
        in.readBytes(bytes);
        String res = new String(bytes);
        System.out.println("res: " + res);

        out.add(res);
    }
}
