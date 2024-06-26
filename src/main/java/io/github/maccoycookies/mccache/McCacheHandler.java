package io.github.maccoycookies.mccache;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class McCacheHandler extends SimpleChannelInboundHandler<String> {

    private static final String CRLF = "\r\n";
    private static final String STRING_PREFIX = "+";
    private static final String OK = "OK";
    private static final String INFO = "McCache server[v1.0.0], created by maccoy." + CRLF
                                     + "Mock Redis Server at 20240626 in Shenzhen." + CRLF;



    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String message) throws Exception {
        String[] args = message.split(CRLF);
        System.out.println("McCacheHandler => " + String.join(",", args));
        String command = args[2].toUpperCase();
        if ("COMMAND".equals(command)) {
            writeByteBuf(channelHandlerContext,
                    "*2" + CRLF +
                            "$7" + CRLF +
                            "COMMAND" + CRLF +
                            "$4" + CRLF +
                            "PING" + CRLF);
        } else if ("PING".equals(command)) {
            String ret = "PONG";
            if (args.length >= 5) {
                ret = args[4];
            }
            simpleString(channelHandlerContext, ret);
        } else if ("INFO".equals(command)) {
            // writeByteBuf(channelHandlerContext, "$" + INFO.getBytes().length + CRLF + INFO + CRLF);
            bulkString(channelHandlerContext, INFO);
        } else {
            simpleString(channelHandlerContext, OK);
        }
    }

    private void bulkString(ChannelHandlerContext channelHandlerContext, String content) {
        writeByteBuf(channelHandlerContext, "$" + content.getBytes().length + CRLF + content + CRLF);
    }

    private void simpleString(ChannelHandlerContext channelHandlerContext, String content) {
        writeByteBuf(channelHandlerContext, STRING_PREFIX + content + CRLF);
    }

    private void writeByteBuf(ChannelHandlerContext channelHandlerContext, String content) {
        System.out.println("wrap byte buffer and reply: " + content);
        ByteBuf buffer = Unpooled.buffer(128);
        buffer.writeBytes(content.getBytes());
        channelHandlerContext.writeAndFlush(buffer);
    }
}
