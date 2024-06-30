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

}
