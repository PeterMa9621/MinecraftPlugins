package me.peterma.worldBossBungeecord.listener;

import me.peterma.worldBossBungeecord.WorldBossBungeecord;
import me.peterma.worldBossBungeecord.util.Util;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ChannelListener implements Listener {
    private WorldBossBungeecord plugin;
    public ChannelListener(WorldBossBungeecord plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        if (event.getTag().equalsIgnoreCase(Util.bungeecordChannel)) {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
            try {
                String channel = in.readUTF();
                if (channel.equals(Util.mainChannel)) {
                    String message = in.readUTF();
                    plugin.getLogger().info(message);

                    JSONParser parser = new JSONParser();
                    try {
                        JSONObject jsonObject = (JSONObject) parser.parse(message);
                        String server = (String) jsonObject.get("server");
                        JSONArray uuids = (JSONArray) jsonObject.get("players");
                        ServerInfo target = ProxyServer.getInstance().getServerInfo(server);
                        for (Object o : uuids) {
                            String uuid = (String) o;
                            ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(UUID.fromString(uuid));
                            proxiedPlayer.connect(target);
                        }

                        ProxyServer.getInstance().getScheduler().schedule(plugin, ()->{
                            Util.sendToBukkit(plugin, message, server);
                        }, 10, TimeUnit.SECONDS);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException ignored) { }
        }
    }
}
