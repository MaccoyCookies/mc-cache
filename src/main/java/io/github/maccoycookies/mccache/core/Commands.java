package io.github.maccoycookies.mccache.core;

import io.github.maccoycookies.mccache.command.CommandCommand;
import io.github.maccoycookies.mccache.command.GetCommand;
import io.github.maccoycookies.mccache.command.InfoCommand;
import io.github.maccoycookies.mccache.command.PingCommand;
import io.github.maccoycookies.mccache.command.SetCommand;

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
