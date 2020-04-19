package dailyQuest.manager;

import dailyQuest.DailyQuest;
import dailyQuest.model.QuestPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class QuestPlayerManager {
    private HashMap<UUID, QuestPlayer> questPlayers = new HashMap<>();
    private DailyQuest plugin;

    public QuestPlayerManager(DailyQuest plugin) {
        this.plugin = plugin;
    }

    public void addPlayer(QuestPlayer questPlayer) {
        Player player = questPlayer.getPlayer();
        questPlayers.put(player.getUniqueId(), questPlayer);
    }

    public HashMap<UUID, QuestPlayer> getQuestPlayers() {
        return questPlayers;
    }

    public boolean containPlayer(Player player) {
        return questPlayers.containsKey(player.getUniqueId());
    }

    public QuestPlayer getQuestPlayer(Player player) {
        return questPlayers.get(player.getUniqueId());
    }
}
