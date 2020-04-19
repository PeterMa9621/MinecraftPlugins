package dailyQuest.manager;

import dailyQuest.model.MobQuest;
import dailyQuest.model.Quest;
import dailyQuest.model.QuestPlayer;
import dailyQuest.util.QuestFinishType;
import dailyQuest.util.Util;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class QuestManager {
    public static HashMap<UUID, MobQuest> mobQuests = new HashMap<UUID, MobQuest>();
    public static ArrayList<Quest> quests = new ArrayList<Quest>();

    public static int getRandomQuest() {
        return Util.random(quests.size());
    }

    public static Quest getQuest(int index) {
        return quests.get(index);
    }

    public static QuestFinishType isQuestFinished(QuestPlayer questPlayer) {
        Player player = questPlayer.getPlayer();
        Quest quest = questPlayer.getCurrentQuest();
        String type = quest.getQuestInfo().getType();
        ItemStack questItem = quest.getQuestInfo().getQuestItem();

        ItemStack item = player.getInventory().getItemInMainHand();
        if(type.equalsIgnoreCase("item")) {
            if(!item.equals(questItem)) {
                return QuestFinishType.ItemQuestNotFinished;
            } else {
                return QuestFinishType.ItemQuestFinished;
            }
        } else {
            if(!mobQuests.get(player.getUniqueId()).isFinished()) {
                return QuestFinishType.MobQuestNotFinished;
            } else {
                return QuestFinishType.MobQuestFinished;
            }
        }
    }
}
