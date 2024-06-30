package io.github.maccoycookies.mccache.command;

import io.github.maccoycookies.mccache.McCache;
import io.github.maccoycookies.mccache.core.Command;
import io.github.maccoycookies.mccache.core.Reply;

/**
 * @author Maccoy
 * @date 2024/6/30 23:00
 * Description
 */
public class PingCommand implements Command {

    @Override
    public String name() {
        return "PING";
    }

    @Override
    public Reply<?> exec(McCache cache, String[] args) {
        String ret = "PONG";
        if (args.length >= 5) {
            ret = getKey(args);
        }
        return Reply.simpleString(ret);
    }
}
