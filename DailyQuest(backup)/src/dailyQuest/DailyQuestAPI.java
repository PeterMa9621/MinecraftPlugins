package dailyQuest;

import org.bukkit.entity.Player;

public class DailyQuestAPI 
{
	private DailyQuest plugin;
	public DailyQuestAPI(DailyQuest plugin)
	{
		this.plugin=plugin;
	}
	
	public int getHowMnayQuestLeft(Player p)
	{
		int questLimit = plugin.defaultQuantity;
		for(String permission:plugin.group.keySet())
		{
			if(p.hasPermission("dailyQuest.limit."+permission))
			{
				questLimit = plugin.group.get(permission);
			}
		}
		int finishedQuest = plugin.playerData.get(p.getName()).getTotalQuest();
		return (questLimit-finishedQuest);
	}

}
