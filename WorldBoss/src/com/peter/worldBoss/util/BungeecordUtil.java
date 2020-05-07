package com.peter.worldBoss.util;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.peter.worldBoss.WorldBoss;
import org.bukkit.entity.Player;

public class BungeecordUtil {
    public static String mainChannel = "peter:worldboss";
    public static String subChannel = "worldBossNotification";
    public static String bungeecordChannel = "BungeeCord";

    public static void sendMessageToBungeecord(Player player, String message, WorldBoss plugin){
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF( mainChannel ); // Write the channel
        out.writeUTF( message ); // Write the message
        player.sendPluginMessage(plugin, bungeecordChannel, out.toByteArray() ); // Send to Bungee
    }
}
