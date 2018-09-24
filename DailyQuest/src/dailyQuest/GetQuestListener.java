package dailyQuest;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class GetQuestListener implements Listener
{
	private DailyQuest plugin;

	public GetQuestListener(DailyQuest plugin)
	{
		this.plugin=plugin;
	}

	@EventHandler
	private void onPlayerClickQuestItemGUI(InventoryClickEvent event)
	{
		if(event.getInventory().getTitle().contains("§8日常任务"))
		{
			Player p = (Player)event.getWhoClicked();
			event.setCancelled(true);
			if(event.getRawSlot()==2)
			{
				int index = plugin.random(plugin.quests.size());

				if(plugin.playerData.get(p.getName()).getCurrentNumber()!=0)
				{
					p.sendMessage("§6[日常任务] §a你目前正在做任务，请先取消任务再重新领取任务!");
					return;
				}
				int questLimit = plugin.defaultQuantity;
				for(String permission:plugin.group.keySet())
				{
					if(p.hasPermission("dailyQuest.limit."+permission))
					{
						questLimit = plugin.group.get(permission);
					}
				}
				if(plugin.playerData.get(p.getName()).getTotalQuest()>=questLimit)
				{
					p.sendMessage("§6[日常任务] §a你今天的任务已达上限，请明天再来!");
					return;
				}
				
				// Get the state of this player
				// the first index means the current index of the quest
				// the second index means what the quest is
				// the last index means how many quests totally this player has already finished
				PlayerData player = plugin.playerData.get(p.getName());
				player.setCurrentNumber(1);
				player.setWhatTheQuestIs(index);
				if(plugin.quests.get(index).getQuest().getType().equalsIgnoreCase("mob"))
				{
					int amount = plugin.quests.get(index).getQuest().getMobAmount();
					int mobID = plugin.quests.get(index).getQuest().getMobId();
					String mobName = plugin.quests.get(index).getQuest().getMobName();
					MobQuest mobQuest = null;

					if(mobName==null)
						mobQuest = new MobQuest(amount, 0, mobID);
					else
						mobQuest = new MobQuest(amount, 0, mobID, mobName);
					plugin.mobQuest.put(p.getName(), mobQuest);
				}
				plugin.playerData.put(p.getName(), player);
				p.sendMessage("§6[第 1 环] §a"+plugin.quests.get(index).getQuestDescribe());
				p.closeInventory();
				return;
			}
			
			if(event.getRawSlot()==3)
			{
				p.performCommand("rw quit");
				p.closeInventory();
				return;
			}
			
			if(event.getRawSlot()==4)
			{
				p.performCommand("ob open 4");
				p.closeInventory();
				return;
			}
			
			if(event.getRawSlot()==5)
			{
				p.closeInventory();
				return;
			}
		}

	}
	
	@EventHandler
	private void onPlayerCloseInventory(InventoryCloseEvent event)
	{
		if(event.getInventory().getTitle().equalsIgnoreCase("§8日常任务"))
		{
			Player p = (Player)event.getPlayer();
			p.playSound(p.getLocation(), Sound.BLOCK_SNOW_BREAK, 1F, 0.0F);
		}
	}
}
