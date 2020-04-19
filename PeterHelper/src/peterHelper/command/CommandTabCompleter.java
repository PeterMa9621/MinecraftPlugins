package peterHelper.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import peterHelper.PeterHelper;

import java.util.ArrayList;
import java.util.List;

public class CommandTabCompleter implements TabCompleter {
    PeterHelper plugin;
    public CommandTabCompleter(PeterHelper plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String alias, String[] args) {
        if(command.getName().equalsIgnoreCase("ph")) {
            switch (args.length) {
                case 1:
                    List<String> list = new ArrayList<String>() {{
                        add("item");
                        add("give");
                        add("giveitem");
                        add("reload");
                    }};
                    return getContainedCommands(list, args[0]);
                case 3:
                    if(args[0].equalsIgnoreCase("giveitem")) {
                        return getContainedCommands(plugin.configManager.itemIds, args[2]);
                    }
            }
        }
        return null;
    }

    private List<String> getContainedCommands(List<String> commands, String arg) {
        return  new ArrayList<String>() {{
            for(String command:commands) {
                if(command.contains(arg)) {
                    add(command);
                }
            }
        }};
    }
}
