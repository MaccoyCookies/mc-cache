package io.github.maccoycookies.mccache.core;

import io.github.maccoycookies.mccache.McCache;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
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
        Command commandExecutor = Commands.get(command);
        if (commandExecutor != null) {
            try {
                Reply<?> reply = commandExecutor.exec(cache, args);
                System.out.println("CMD[" + command + "] => " + reply.getType() + " => " + reply.getValue());
                replyContext(channelHandlerContext, reply);
            } catch (Exception exception) {
                Reply<?> reply = Reply.error("EXP exception with msg: '" + exception.getMessage() + "'");
                replyContext(channelHandlerContext, reply);
            }
        } else {
            Reply<?> reply = Reply.error("ERR unsupported command '" + command + "'");
            replyContext(channelHandlerContext, reply);
        }
    }

    private void replyContext(ChannelHandlerContext channelHandlerContext, Reply<?> reply) {
        switch (reply.getType()) {
            case INT:
                integer(channelHandlerContext, (Integer) reply.getValue());
                break;
            case ERROR:
                error(channelHandlerContext, (String) reply.getValue());
                break;
            case SIMPLE_STRING:
                simpleString(channelHandlerContext, (String) reply.getValue());
                break;
            case BULK_STRING:
                bulkString(channelHandlerContext, (String) reply.getValue());
                break;
            case ARRAY:
                array(channelHandlerContext, (String[]) reply.getValue());
                break;
            default:
                simpleString(channelHandlerContext, OK);
        }

    }

    private void error(ChannelHandlerContext channelHandlerContext, String content) {
        writeByteBuf(channelHandlerContext, errorEncode(content));
    }

    private String errorEncode(String content) {
        return "-" + content + CRLF;
    }

    private void integer(ChannelHandlerContext channelHandlerContext, Integer content) {
        writeByteBuf(channelHandlerContext, integerEncode(content));
    }

    private String integerEncode(Integer content) {
        return ":" + content + CRLF;
    }

    private void array(ChannelHandlerContext channelHandlerContext, String[] array) {
        writeByteBuf(channelHandlerContext, arrayEncode(array));
    }

    private String arrayEncode(Object[] array) {
        StringBuilder stringBuilder = new StringBuilder();
        if (array == null) {
            stringBuilder.append("*-1").append(CRLF);
        } else if (array.length == 0) {
            stringBuilder.append("*0").append(CRLF);
        } else {
            stringBuilder.append("*").append(array.length).append(CRLF);
            for (Object arr : array) {
                if (arr == null) {
                    stringBuilder.append("$-1" + CRLF);
                } else if (arr instanceof Integer integer) {
                    stringBuilder.append(integerEncode(integer));
                } else if (arr instanceof String string) {
                    stringBuilder.append(bulkStringEncode(string));
                } else if (arr instanceof Object[] objects) {
                    stringBuilder.append(arrayEncode(objects));
                } else {

                }
            }
        }
        return stringBuilder.toString();
    }

    private void bulkString(ChannelHandlerContext channelHandlerContext, String content) {
        String res = bulkStringEncode(content);
        writeByteBuf(channelHandlerContext, res);
    }

    private String bulkStringEncode(String content) {
        String res;
        if (content == null) {
            res = "$-1";
        } else if (content.isEmpty()) {
            res = "$0";
        } else {
            res = BULK_PREFIX + content.getBytes().length + CRLF + content;
        }
        return res + CRLF;
    }

    private void simpleString(ChannelHandlerContext channelHandlerContext, String content) {
        writeByteBuf(channelHandlerContext, simpleStringEncode(content));
    }

    private String simpleStringEncode(String content) {
        String res;
        if (content == null) {
            res = "$-1";
        } else if (content.isEmpty()) {
            res = "$0";
        } else {
            res = STRING_PREFIX + content;
        }
        return res + CRLF;
    }

    private void writeByteBuf(ChannelHandlerContext channelHandlerContext, String content) {
        System.out.println("wrap byte buffer and reply: " + content);
        ByteBuf buffer = Unpooled.buffer(128);
        buffer.writeBytes(content.getBytes());
        channelHandlerContext.writeAndFlush(buffer);
    }
}
