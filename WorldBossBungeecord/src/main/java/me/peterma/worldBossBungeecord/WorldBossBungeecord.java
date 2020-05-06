package me.peterma.worldBossBungeecord;

import me.peterma.worldBossBungeecord.command.WorldBossCommand;
import net.md_5.bungee.api.plugin.Plugin;

public class WorldBossBungeecord extends Plugin{
    @Override
    public void onEnable() {
        // You should not put an enable message in your plugin.
        // BungeeCord already does so
        getLogger().info("Yay! It loads!");
        getProxy().getPluginManager().registerCommand(this, new WorldBossCommand());
    }
}
