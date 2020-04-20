package dps.model;

import dps.Dps;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class DpsPlayerManager {
    public static HashMap<UUID, HashMap<UUID, DpsPlayer>> dpsData = new HashMap<>();
    public static HashMap<UUID, DpsPlayer> dpsPlayers = new HashMap<>();

    public static void markPlayerExitDungeon(Player player) {
        UUID uuid = player.getUniqueId();
        //Bukkit.getConsoleSender().sendMessage(uuid.toString());
        DpsPlayer dpsPlayer = dpsPlayers.get(uuid);
        if(dpsPlayer!=null)
            dpsPlayer.exitDungeon();
    }

    public static boolean isPlayerInDps(Player player) {
        DpsPlayer dpsPlayer = dpsPlayers.get(player.getUniqueId());
        if(dpsPlayer==null)
            return false;
        return dpsPlayer.isInDungeon();
    }

    public static DpsPlayer getDpsPlayer(Player player) {
        return dpsPlayers.get(player.getUniqueId());
    }
}
