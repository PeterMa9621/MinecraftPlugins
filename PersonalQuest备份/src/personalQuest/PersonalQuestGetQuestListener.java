package personalQuest;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class PersonalQuestGetQuestListener implements Listener
{
	private PersonalQuest plugin;
	public PersonalQuestGetQuestListener(PersonalQuest plugin)
	{
		this.plugin=plugin;
	}
	
	@EventHandler
	public void onPlayerClickGuiToGetQuest(InventoryClickEvent event)
    {
		if(event.getInventory().getTitle().equalsIgnoreCase(plugin.message.get("QuestGui.Name")))
		{
			Player p = (Player)event.getWhoClicked();
			event.setCancelled(true);
			// The inventory's size is 54, quests show between 0 and 45.
			if(event.getRawSlot()>=0 && event.getRawSlot()<=44)
			{
				if(event.getInventory().getItem(event.getRawSlot())==null)
				{
					return;
				}
				// Get the type of the quest
				String type = event.getInventory().getItem(event.getRawSlot()).getItemMeta().getDisplayName();
				// Get the poster's name
				String posterName = null;
				for(String lore:event.getInventory().getItem(event.getRawSlot()).getItemMeta().getLore())
				{
					if(lore.contains("§d发布人:"))
					{
						posterName = lore.split(":")[1];
						break;
					}
				}
				//=================================================================
				
				if(posterName==null)
				{
					p.closeInventory();
					p.sendMessage("§a[任务系统] §4出现未知错误，不能成功领取任务");
					return;
				}
				
				//=================================================================
				
				if(posterName.equalsIgnoreCase(p.getName()))
				{
					p.closeInventory();
					p.sendTitle("§a[任务系统]", "§c你不能领取你自己发布的任务");
					return;
				}
				
				//=================================================================
				
				ArrayList<ArrayList<String>> totalQuest = new ArrayList<ArrayList<String>>();
				
				// The maximum quantity of quests for one player is 2.
				if(!plugin.playerQuest.isEmpty() && plugin.playerQuest.get(p.getName()).size()<2)
				{
					totalQuest = plugin.playerQuest.get(p.getName());
				}
				// return once player has two ore more quests
				else if(!plugin.playerQuest.isEmpty() && plugin.playerQuest.get(p.getName()).size()>=2)
				{
					p.sendMessage("§a[任务系统] §c你最多只能接2个任务，请先完成任务");
					p.closeInventory();
					return;
				}
				ArrayList<String> questInfo = new ArrayList<String>();
				// Get the other quests of this player
				if(!plugin.playerQuest.isEmpty())
				{
					totalQuest = plugin.playerQuest.get(p.getName());
				}
				// Judge if this quest is a VIP quest
				if(event.getRawSlot()>=0 && event.getRawSlot()<=8)
				{
					if(type.contains(plugin.message.get("QuestGui.ItemQuestTag")))
						questInfo = plugin.vipItemQuests.get(posterName).get("quest");
					else if(type.contains(plugin.message.get("QuestGui.HeadQuestTag")))
						questInfo = plugin.vipHeadQuests.get(posterName).get("quest");
				}
				else if(event.getRawSlot()>=9 && event.getRawSlot()<=45)
				{
					if(type.contains(plugin.message.get("QuestGui.ItemQuestTag")))
						questInfo = plugin.itemQuests.get(posterName).get("quest");
					else if(type.contains(plugin.message.get("QuestGui.HeadQuestTag")))
						questInfo = plugin.headQuests.get(posterName).get("quest");
				}
				// Add the poster's name at the end of the list
				questInfo.add(posterName);
				
				if(plugin.playerQuest.get(p.getName()).size()==1)
				{
					if(plugin.isSame(plugin.playerQuest.get(p.getName()).get(0), questInfo))
					{
						p.closeInventory();
						p.sendMessage("§a[任务系统] §c你已经领取过该任务了");
						return;
					}
				}

				totalQuest.add(questInfo);
				plugin.playerQuest.put(p.getName(), totalQuest);
				
				
				// Get players who are finishing this quest
				ArrayList<String> players = new ArrayList<String>();
				if(event.getRawSlot()>=0 && event.getRawSlot()<=8)
				{
					if(type.contains(plugin.message.get("QuestGui.ItemQuestTag")))
					{
						if(plugin.vipItemQuests.get(posterName).containsKey("player"))
							players = plugin.vipItemQuests.get(posterName).get("player");
						players.add(p.getName());
						plugin.vipItemQuests.get(posterName).put("player", players);
					}
						
					else if(type.contains(plugin.message.get("QuestGui.HeadQuestTag")))
					{
						if(plugin.vipHeadQuests.get(posterName).containsKey("player"))
							players = plugin.vipHeadQuests.get(posterName).get("player");
						players.add(p.getName());
						plugin.vipHeadQuests.get(posterName).put("player", players);
					}
					
				}
				else if(event.getRawSlot()>=9 && event.getRawSlot()<=45)
				{
					if(type.contains(plugin.message.get("QuestGui.ItemQuestTag")))
					{
						if(plugin.itemQuests.get(posterName).containsKey("player"))
							players = plugin.itemQuests.get(posterName).get("player");
						players.add(p.getName());
						plugin.itemQuests.get(posterName).put("player", players);
					}
						
					else if(type.contains(plugin.message.get("QuestGui.HeadQuestTag")))
					{
						if(plugin.headQuests.get(posterName).containsKey("player"))
							players = plugin.headQuests.get(posterName).get("player");
						players.add(p.getName());
						plugin.headQuests.get(posterName).put("player", players);
					}

				}
				p.closeInventory();
				if(Bukkit.getPlayer(posterName)!=null)
				{
					Bukkit.getPlayer(posterName).sendMessage("§a[任务系统] §e玩家§d"+p.getName()+"§e已领取你发布的任务");
				}
				p.sendTitle("§a[任务系统]", "§c领取任务成功");
				//p.sendMessage("§a[任务系统] §e领取任务成功");
			}
		}
		return;
    }
	
}
