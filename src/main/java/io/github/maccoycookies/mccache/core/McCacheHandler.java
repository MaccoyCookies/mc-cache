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
            Reply<?> reply = commandExecutor.exec(cache, args);
            System.out.println("CMD[" + command + "] => " + reply.getType() + " => " + reply.getValue());
            replyContext(channelHandlerContext, reply);
            return;
        }

        if ("COMMAND".equals(command)) {
            writeByteBuf(channelHandlerContext,
                    "*2" + CRLF +
                            "$7" + CRLF +
                            "COMMAND" + CRLF +
                            "$4" + CRLF +
                            "PING" + CRLF);
        // } else if ("PING".equals(command)) {
        //     String ret = "PONG";
        //     if (args.length >= 5) {
        //         ret = args[4];
        //     }
        //     simpleString(channelHandlerContext, ret);
        // } else if ("INFO".equals(command)) {
        //     bulkString(channelHandlerContext, INFO);
        } else if ("SET".equals(command)) {
            cache.set(args[4], args[6]);
            simpleString(channelHandlerContext, OK);
        } else if ("GET".equals(command)) {
            bulkString(channelHandlerContext, cache.get(args[4]));
        } else if ("STRLEN".equals(command)) {
            String value = cache.get(args[4]);
            integer(channelHandlerContext, value == null ? 0 : value.length());
        } else if ("DEL".equals(command)) {
            String[] arr = new String[(args.length - 3) / 2];
            for (int i = 0; i < arr.length; i++) {
                arr[i] = args[4 + i * 2];
            }
            integer(channelHandlerContext, cache.del(arr));
        } else if ("EXISTS".equals(command)) {
            String[] arr = new String[(args.length - 3) / 2];
            for (int i = 0; i < arr.length; i++) {
                arr[i] = args[4 + i * 2];
            }
            integer(channelHandlerContext, cache.exists(arr));
        } else if ("MGET".equals(command)) {
            String[] arr = new String[(args.length - 3) / 2];
            for (int i = 0; i < arr.length; i++) {
                arr[i] = args[4 + i * 2];
            }
            array(channelHandlerContext, cache.mget(arr));
        } else if ("MSET".equals(command)) {
            int len = (args.length - 3) / 4;
            String[] keys = new String[len];
            String[] vals = new String[len];
            for (int i = 0; i < len; i++) {
                keys[i] = args[4 + i * 4];
                vals[i] = args[6 + i * 4];
            }
            cache.mset(keys, vals);
            simpleString(channelHandlerContext, OK);
        } else if ("INCR".equals(command)) {
            String key = args[4];
            try {
                integer(channelHandlerContext, cache.incr(key));
            } catch (NumberFormatException exception) {
                error(channelHandlerContext, "NFE " + key + " value[" + cache.get(key) + "] is not an integer");
            }
        } else if ("DECR".equals(command)) {
            String key = args[4];
            try {
                integer(channelHandlerContext, cache.decr(key));
            } catch (NumberFormatException exception) {
                error(channelHandlerContext, "NFE " + key + " value[" + cache.get(key) + "] is not an integer");
            }
        } else {
            simpleString(channelHandlerContext, OK);
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
