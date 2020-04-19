package dailyQuest.api;

import dailyQuest.DailyQuest;
import dailyQuest.model.QuestPlayer;
import org.bukkit.entity.Player;

public class DailyQuestAPI 
{
	private DailyQuest plugin;
	public DailyQuestAPI(DailyQuest plugin)
	{
		this.plugin=plugin;
	}
	
	public int getHowManyQuestLeft(Player p)
	{
		QuestPlayer questPlayer = plugin.questPlayerManager.getQuestPlayer(p);
		int questLimit = questPlayer.getDailyLimit();
		int finishedQuest = questPlayer.getTotalQuest();
		return (questLimit-finishedQuest);
	}

}
