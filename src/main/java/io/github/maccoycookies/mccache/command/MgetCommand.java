package io.github.maccoycookies.mccache.command;

import io.github.maccoycookies.mccache.McCache;
import io.github.maccoycookies.mccache.core.Command;
import io.github.maccoycookies.mccache.core.Reply;

/**
 * @author Maccoy
 * @date 2024/6/30 23:00
 * Description
 */
public class MgetCommand implements Command {

    @Override
    public String name() {
        return "MGET";
    }

    @Override
    public Reply<?> exec(McCache cache, String[] args) {
        return Reply.array(cache.mget(getParams(args)));
    }
}
