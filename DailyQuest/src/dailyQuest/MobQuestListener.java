package dailyQuest;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import net.citizensnpcs.api.CitizensAPI;

public class MobQuestListener implements Listener
{
	private DailyQuest plugin;

	public MobQuestListener(DailyQuest plugin)
	{
		this.plugin=plugin;
	}
	
	@EventHandler
	public void onPlayerJoin(EntityDeathEvent event)
    {
		if(!event.getEntityType().equals(EntityType.PLAYER))
		{
			if(event.getEntity().getKiller() instanceof Player)
			{
				Player p = event.getEntity().getKiller();
				if(plugin.mobQuest.containsKey(p.getName()))
				{
					if(event.getEntityType().getTypeId()==plugin.mobQuest.get(p.getName()).getMobID() &&
							plugin.mobQuest.get(p.getName()).isFinished()==false)
					{
						MobQuest mobQuest = plugin.mobQuest.get(p.getName());
						int newCurrentAmount = mobQuest.getCurrentAmount();

						if(plugin.mobQuest.get(p.getName()).getMobName()!=null)
						{
							//p.sendMessage(plugin.mobQuest.get(p.getName()).getMobName());
							if(plugin.mobQuest.get(p.getName()).getMobName().equalsIgnoreCase(event.getEntity().getCustomName()))
								newCurrentAmount += 1;
							p.sendMessage("§6[日常任务] §e你已击杀§5"+newCurrentAmount+"§e只怪物，剩余§5"+(plugin.mobQuest.get(p.getName()).getAmountRequested()-newCurrentAmount)+"§e只。");
						}
						else
						{
							newCurrentAmount += 1;
							p.sendMessage("§6[日常任务] §e你已击杀§5"+newCurrentAmount+"§e只怪物，剩余§5"+(plugin.mobQuest.get(p.getName()).getAmountRequested()-newCurrentAmount)+"§e只。");
						}
						mobQuest.setCurrentAmount(newCurrentAmount);
						if(newCurrentAmount==plugin.mobQuest.get(p.getName()).getAmountRequested())
						{
							int npcID = plugin.quests.get(plugin.playerData.get(p.getName()).getWhatTheQuestIs()).getNPCId();
							String npcName = CitizensAPI.getNPCRegistry().getById(npcID).getName();
							p.sendMessage("§6[日常任务] §a你已完成任务，请前往"+npcName+"§a处，交付任务！");
						}
						
					}
				}
			}
			
		}
		
    }
}
