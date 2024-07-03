package io.github.maccoycookies.mccache.command;

import io.github.maccoycookies.mccache.McCache;
import io.github.maccoycookies.mccache.core.Command;
import io.github.maccoycookies.mccache.core.Reply;

public class ZcountCommand implements Command {

    public String name() {
        return "ZCOUNT";
    }

    @Override
    public Reply<?> exec(McCache cache, String[] args) {
        String key = getKey(args);
        String min = getVal(args);
        String max = args[8];
        return Reply.integer(cache.zcount(key, Double.parseDouble(min), Double.parseDouble(max)));
    }
}
