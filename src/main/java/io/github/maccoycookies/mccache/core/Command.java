package io.github.maccoycookies.mccache.core;

import io.github.maccoycookies.mccache.McCache;

/**
 * @author Maccoy
 * @date 2024/6/30 22:53
 * Description command interface
 */
public interface Command {

    String CRLF = "\r\n";

    String OK = "OK";

    String name();
    Reply<?> exec(McCache cache, String[] args);

    // add default args operator
    default String getKey(String[] args) {
        return args[4];
    }

    default String getVal(String[] args) {
        return args[6];
    }

    default String[] getParams(String[] args) {
        String[] arr = new String[(args.length - 3) / 2];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = args[4 + i * 2];
        }
        return arr;
    }

    default String[] getParamsNoKey(String[] args) {
        String[] arr = new String[(args.length - 5) / 2];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = args[6 + i * 2];
        }
        return arr;
    }

    default String[] getKeys(String[] args) {
        int len = (args.length - 3) / 4;
        String[] keys = new String[len];
        for (int i = 0; i < len; i++) {
            keys[i] = args[4 + i * 4];
        }
        return keys;
    }

    default String[] getVals(String[] args) {
        int len = (args.length - 3) / 4;
        String[] vals = new String[len];
        for (int i = 0; i < len; i++) {
            vals[i] = args[6 + i * 4];
        }
        return vals;
    }

    default String[] getHkeys(String[] args) {
        int len = (args.length - 5) / 4;
        String[] vals = new String[len];
        for (int i = 0; i < len; i++) {
            vals[i] = args[6 + i * 4];
        }
        return vals;
    }

    default String[] getHvals(String[] args) {
        int len = (args.length - 5) / 4;
        String[] vals = new String[len];
        for (int i = 0; i < len; i++) {
            vals[i] = args[8 + i * 4];
        }
        return vals;
    }
}
