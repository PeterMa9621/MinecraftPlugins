package me.peterma.worldBossBungeecord.command;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class WorldBossCommand extends Command {
    public WorldBossCommand() {
        super("wbb", "worldboss.admin");
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if(strings.length == 0) {
            commandSender.sendMessage(new ComponentBuilder("Test text!").color(ChatColor.GREEN).create());
            return;
        }

        if(strings[0].equalsIgnoreCase("tp")) {
            if(strings.length == 2) {
                if(commandSender instanceof ProxiedPlayer) {
                    ProxiedPlayer proxiedPlayer = (ProxiedPlayer) commandSender;
                    proxiedPlayer.sendMessage(new ComponentBuilder("Hello World!").color(ChatColor.GREEN).create());
                }
            }
        }
    }
}
