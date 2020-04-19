package dailyQuest.listener;

import dailyQuest.DailyQuest;
import dailyQuest.manager.QuestManager;
import dailyQuest.model.MobQuest;
import dailyQuest.model.QuestPlayer;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class MobQuestListener implements Listener
{
	private DailyQuest plugin;

	public MobQuestListener(DailyQuest plugin)
	{
		this.plugin=plugin;
	}
	
	@EventHandler
	public void onMobDeath(EntityDeathEvent event)
    {
		if(!event.getEntityType().equals(EntityType.PLAYER))
		{
			if(event.getEntity().getKiller() != null)
			{
				Player p = event.getEntity().getKiller();
				if(QuestManager.mobQuests.containsKey(p.getUniqueId()))
				{
					MobQuest mobQuest = QuestManager.mobQuests.get(p.getUniqueId());
					if(event.getEntityType().equals(EntityType.valueOf(mobQuest.getMobID())) &&
							!mobQuest.isFinished())
					{
						int currentAmount = mobQuest.getCurrentAmount();

						if(mobQuest.getMobName()!=null) {
							if(mobQuest.getMobName().equalsIgnoreCase(event.getEntity().getCustomName()))
								currentAmount += 1;
						}
						else {
							currentAmount += 1;
						}

						mobQuest.setCurrentAmount(currentAmount);
						if(currentAmount==mobQuest.getAmountRequested()) {
							QuestPlayer questPlayer = plugin.questPlayerManager.getQuestPlayer(p);
							int npcID = QuestManager.quests.get(questPlayer.getWhatTheQuestIs()).getNPCId();
							String npcName = CitizensAPI.getNPCRegistry().getById(npcID).getName();
							p.sendMessage("§6[日常任务] §a你已完成任务，请前往"+npcName+"§a处，交付任务！");
						} else {
							p.sendMessage("§6[日常任务] §e你已击杀§5"+currentAmount+"§e只怪物，剩余§5"+(mobQuest.getAmountRequested()-currentAmount)+"§e只。");
						}
					}
				}
			}
		}
    }
}
