package io.github.maccoycookies.mccache.command;

import io.github.maccoycookies.mccache.McCache;
import io.github.maccoycookies.mccache.core.Command;
import io.github.maccoycookies.mccache.core.Reply;

/**
 * @author Maccoy
 * @date 2024/6/30 23:00
 * Description
 */
public class SremCommand implements Command {

    @Override
    public String name() {
        return "SREM";
    }

    @Override
    public Reply<?> exec(McCache cache, String[] args) {
        String key = getKey(args);
        String[] val = getParamsNoKey(args);
        return Reply.integer(cache.srem(key, val));
    }
}
