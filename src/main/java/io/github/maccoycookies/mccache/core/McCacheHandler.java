package io.github.maccoycookies.mccache.core;

import io.github.maccoycookies.mccache.McCache;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class McCacheHandler extends SimpleChannelInboundHandler<String> {

    private static final String CRLF = "\r\n";
    private static final String STRING_PREFIX = "+";
    private static final String BULK_PREFIX = "$";
    private static final String OK = "OK";
    private static final String INFO = "McCache server[v1.0.0], created by maccoy." + CRLF
            + "Mock Redis Server at 20240626 in Shenzhen." + CRLF;
    private static final McCache cache = new McCache();

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
        } else if ("SET".equals(command)) {
            cache.set(args[4], args[6]);
            simpleString(channelHandlerContext, OK);
        } else if ("GET".equals(command)) {
            bulkString(channelHandlerContext, cache.get(args[4]));
        } else if ("STRLEN".equals(command)) {
            String value = cache.get(args[4]);
            intString(channelHandlerContext, value == null ? 0 : value.length());
        } else if ("DEL".equals(command)) {
            String[] arr = new String[(args.length - 3) / 2];
            for (int i = 0; i < arr.length; i++) {
                arr[i] = args[4 + i * 2];
            }
            intString(channelHandlerContext, cache.del(arr));
        } else if ("EXISTS".equals(command)) {
            String[] arr = new String[(args.length - 3) / 2];
            for (int i = 0; i < arr.length; i++) {
                arr[i] = args[4 + i * 2];
            }
            intString(channelHandlerContext, cache.exists(arr));
        } else {
            simpleString(channelHandlerContext, OK);
        }
    }

    private void intString(ChannelHandlerContext channelHandlerContext, Integer content) {
        writeByteBuf(channelHandlerContext, ":" + content + CRLF);
    }

    private void bulkString(ChannelHandlerContext channelHandlerContext, String content) {
        String res;
        if (content == null) {
            res = "$-1";
        } else if (content.isEmpty()) {
            res = "$0";
        } else {
            res = BULK_PREFIX + content.getBytes().length + CRLF + content;
        }
        writeByteBuf(channelHandlerContext, res + CRLF);
    }

    private void simpleString(ChannelHandlerContext channelHandlerContext, String content) {
        String res;
        if (content == null) {
            res = "$-1";
        } else if (content.isEmpty()) {
            res = "$0";
        } else {
            res = STRING_PREFIX + content;
        }
        writeByteBuf(channelHandlerContext, res + CRLF);
    }

    private void writeByteBuf(ChannelHandlerContext channelHandlerContext, String content) {
        System.out.println("wrap byte buffer and reply: " + content);
        ByteBuf buffer = Unpooled.buffer(128);
        buffer.writeBytes(content.getBytes());
        channelHandlerContext.writeAndFlush(buffer);
    }
}
