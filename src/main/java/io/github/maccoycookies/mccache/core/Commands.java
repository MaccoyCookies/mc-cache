package io.github.maccoycookies.mccache.core;

import io.github.maccoycookies.mccache.command.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Maccoy
 * @date 2024/6/30 23:01
 * Description register commands here
 */
public class Commands {

    private final static Map<String, Command> COMMAND_MAP = new LinkedHashMap<>();

    static {
        initCommands();
    }

    private static void initCommands() {
        register(new CommandCommand());
        register(new PingCommand());
        register(new InfoCommand());

        // String
        register(new SetCommand());
        register(new GetCommand());
        register(new StrlenCommand());
        register(new DelCommand());
        register(new ExistsCommand());
        register(new IncrCommand());
        register(new DecrCommand());
        register(new MgetCommand());
        register(new MsetCommand());

        // List
        // Lpush Rpush Lpop Rpop Lindex Lrange
        register(new LpushCommand());
        register(new LpopCommand());
        register(new RpushCommand());
        register(new RpopCommand());
        register(new LlenCommand());
        register(new LindexCommand());
        register(new LrangeCommand());

        // set
        register(new SaddCommand());
        register(new SmembersCommand());
        register(new SismembersCommand());
        register(new SremCommand());
        register(new ScardCommand());
        register(new SpopCommand());


        // hash hset hget hlen hgetall hdel hexists hmget
        register(new HsetCommand());
        register(new HgetCommand());
        register(new HgetallCommand());
        register(new HlenCommand());
        register(new HdelCommand());
        register(new HexistsCommand());
        register(new HmgetCommand());


        // zset

    }

    public static void register(Command command) {
        COMMAND_MAP.put(command.name(), command);
    }

    public static Command get(String name) {
        return COMMAND_MAP.get(name);
    }

    public static String[] getCommandNames() {
        return COMMAND_MAP.keySet().toArray(new String[0]);
    }

}
