package me.peterma.worldBossBungeecord.command;

import me.peterma.worldBossBungeecord.WorldBossBungeecord;
import me.peterma.worldBossBungeecord.util.Util;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class WorldBossCommand extends Command {
    private WorldBossBungeecord plugin;
    public WorldBossCommand(WorldBossBungeecord plugin) {
        super("wbb", "worldboss.admin");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if(strings.length == 0) {
            commandSender.sendMessage(new ComponentBuilder("Test text!").color(ChatColor.GREEN).create());
            return;
        }

        if(strings[0].equalsIgnoreCase("tp")) {
            if(strings.length == 2) {
                String playerName = strings[1];
                ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(playerName);
                if(proxiedPlayer!=null) {
                    Util.sendPluginData(proxiedPlayer, "Test data");
                    this.plugin.getLogger().info("Send plugin message");
                }
                commandSender.sendMessage(new ComponentBuilder("Hello World!").color(ChatColor.GREEN).create());
            }
        }
    }
}
