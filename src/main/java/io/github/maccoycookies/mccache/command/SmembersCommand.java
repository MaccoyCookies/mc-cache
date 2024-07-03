package io.github.maccoycookies.mccache.command;

import io.github.maccoycookies.mccache.McCache;
import io.github.maccoycookies.mccache.core.Command;
import io.github.maccoycookies.mccache.core.Reply;

public class SmembersCommand implements Command {

    public String name() {
        return "SMEMBERS";
    }

    @Override
    public Reply<?> exec(McCache cache, String[] args) {
        String key = getKey(args);
        return Reply.array(cache.smembers(key));
    }

}
