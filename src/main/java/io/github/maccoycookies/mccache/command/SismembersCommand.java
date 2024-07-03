package io.github.maccoycookies.mccache.command;

import io.github.maccoycookies.mccache.McCache;
import io.github.maccoycookies.mccache.core.Command;
import io.github.maccoycookies.mccache.core.Reply;

public class SismembersCommand implements Command {

    public String name() {
        return "SISMEMBER";
    }

    @Override
    public Reply<?> exec(McCache cache, String[] args) {
        String key = getKey(args);
        String val = getVal(args);
        return Reply.integer(cache.sismembers(key, val));
    }

}
