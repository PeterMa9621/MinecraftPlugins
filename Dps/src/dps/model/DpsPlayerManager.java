package dps.model;

import dps.Dps;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class DpsPlayerManager {
    public static HashMap<UUID, HashMap<UUID, DpsPlayer>> dpsData = new HashMap<>();
    public static HashMap<UUID, DpsPlayer> dpsPlayers = new HashMap<>();

    public static void removeDpsPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        DpsPlayer dpsPlayer = dpsPlayers.get(uuid);
        UUID worldId = dpsPlayer.getDungeonWorldId();
        if(dpsData.containsKey(worldId)){
            HashMap<UUID, DpsPlayer> currentDungeonPlayers =  dpsData.get(worldId);
            currentDungeonPlayers.remove(uuid);
        }
    }
}
