package me.peterma.worldBossBungeecord.util;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.peterma.worldBossBungeecord.WorldBossBungeecord;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

public class Util {
    public static String mainChannel = "peter:worldboss";
    public static String subChannel = "worldBossNotification";
    public static String bungeecordChannel = "BungeeCord";

    public static void sendPluginData(ProxiedPlayer player, String data) {
        Collection<ProxiedPlayer> networkPlayers = ProxyServer.getInstance().getPlayers();

        if(networkPlayers==null || networkPlayers.isEmpty()) return;

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF( subChannel ); // the channel could be whatever you want
        out.writeUTF( data ); // this data could be whatever you want

        // we send the data to the server
        // using ServerInfo the packet is being queued if there are no players in the server
        // using only the server to send data the packet will be lost if no players are in it
        player.getServer().getInfo().sendData( mainChannel, out.toByteArray() );
    }

    public static void sendToBukkit(Plugin plugin, String message, String destServerName) {
        ServerInfo all = plugin.getProxy().getServerInfo(destServerName);
        _sendToBukkit(mainChannel, subChannel, message, all);
    }

    private static void _sendToBukkit(String mainChannel, String subChannel, String message, ServerInfo server) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);
        try {
            out.writeUTF(subChannel);
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.sendData(mainChannel, stream.toByteArray());
    }
}
