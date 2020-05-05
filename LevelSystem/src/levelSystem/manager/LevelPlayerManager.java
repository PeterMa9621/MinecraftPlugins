package levelSystem.manager;

import levelSystem.LevelSystem;
import levelSystem.model.LevelPlayer;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class LevelPlayerManager {
    public LevelSystem plugin;
    private HashMap<UUID, LevelPlayer> players;
    public LevelPlayerManager(LevelSystem plugin) {
        this.plugin = plugin;
        players = new HashMap<>();
    }

    public LevelPlayer getLevelPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        return this.players.get(uuid);
    }

    public void addLevelPlayer(LevelPlayer levelPlayer) {
        Player player = levelPlayer.getPlayer();
        UUID uuid = player.getUniqueId();
        this.players.put(uuid, levelPlayer);
    }

    public Collection<LevelPlayer> getPlayers() {
        return players.values();
    }

    public boolean containsLevelPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        return players.containsKey(uuid);
    }
}
