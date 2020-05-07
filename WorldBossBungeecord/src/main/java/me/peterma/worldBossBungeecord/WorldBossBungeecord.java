package me.peterma.worldBossBungeecord;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.peterma.worldBossBungeecord.command.WorldBossCommand;
import me.peterma.worldBossBungeecord.listener.ChannelListener;
import me.peterma.worldBossBungeecord.util.Util;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.Collection;

public class WorldBossBungeecord extends Plugin{
    @Override
    public void onEnable() {
        // You should not put an enable message in your plugin.
        // BungeeCord already does so

        getProxy().getPluginManager().registerCommand(this, new WorldBossCommand(this));
        getProxy().getPluginManager().registerListener(this, new ChannelListener(this));
        getProxy().registerChannel(Util.mainChannel);
        getLogger().info("World Boss has enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("World Boss has disabled!");
    }
}
