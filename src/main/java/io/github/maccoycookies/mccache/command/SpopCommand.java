package io.github.maccoycookies.mccache.command;

import io.github.maccoycookies.mccache.McCache;
import io.github.maccoycookies.mccache.core.Command;
import io.github.maccoycookies.mccache.core.Reply;

public class SpopCommand implements Command {

    public String name() {
        return "SPOP";
    }

    @Override
    public Reply<?> exec(McCache cache, String[] args) {
        String key = getKey(args);
        int count = 1;
        if (args.length > 6) {
            String val = getVal(args);
            count = Integer.parseInt(val);
            return Reply.array(cache.spop(key, count));
        }
        String[] res = cache.spop(key, count);
        return Reply.bulkString(res == null ? null : res[0]);
    }

}
