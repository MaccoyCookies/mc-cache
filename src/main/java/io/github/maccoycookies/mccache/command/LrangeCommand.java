package io.github.maccoycookies.mccache.command;

import io.github.maccoycookies.mccache.McCache;
import io.github.maccoycookies.mccache.core.Command;
import io.github.maccoycookies.mccache.core.Reply;

public class LrangeCommand implements Command {

    public String name() {
        return "LRANGE";
    }

    @Override
    public Reply<?> exec(McCache cache, String[] args) {
        String key = getKey(args);
        String[] paramsNoKey = getParamsNoKey(args);
        int start = Integer.parseInt(paramsNoKey[0]);
        int end = Integer.parseInt(paramsNoKey[1]);
        return Reply.array(cache.lrange(key, start, end));
    }

}
