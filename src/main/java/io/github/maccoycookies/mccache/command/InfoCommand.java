package io.github.maccoycookies.mccache.command;

import io.github.maccoycookies.mccache.McCache;
import io.github.maccoycookies.mccache.core.Command;
import io.github.maccoycookies.mccache.core.Reply;

/**
 * @author Maccoy
 * @date 2024/6/30 23:00
 * Description
 */
public class InfoCommand implements Command {

    private static final String INFO = "McCache server[v1.0.1], created by maccoy." + CRLF
            + "Mock Redis Server at 20240630 in Shenzhen." + CRLF;

    @Override
    public String name() {
        return "INFO";
    }

    @Override
    public Reply<?> exec(McCache cache, String[] args) {
        return Reply.bulkString(INFO);
    }
}
