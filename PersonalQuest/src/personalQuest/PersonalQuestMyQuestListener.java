package personalQuest;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

public class PersonalQuestMyQuestListener implements Listener
{
	private PersonalQuest plugin;
	private PersonalQuestInitGui gui;
	int[] windowSlot = {9,10,11,12,13,14,15,16,17,27,28,29,30,31,32,33,34,35};
	int[] getQuestSlot = {0,1,2,3,4,5,6,7,8};
	int[] vipQuestSlot = {18,19,20,21,22,23,24,25,26};
	int[] normalQuestSlot = {36,37,38,39,40,41,42,43,44};
	ArrayList<HashMap<String, HashMap<String, ArrayList<String>>>> totalQuestList = new ArrayList<HashMap<String, HashMap<String, ArrayList<String>>>>();
	public PersonalQuestMyQuestListener(PersonalQuest plugin, PersonalQuestInitGui gui)
	{
		this.plugin=plugin;
		this.gui=gui;
	}
	
	
	@EventHandler
	public void onPlayerClickMyQuest(InventoryClickEvent event)
    {
		if(event.getInventory().getTitle().equalsIgnoreCase(plugin.message.get("MyQuestGui.Name")))
		{

			if(event.getRawSlot()>=9 && event.getRawSlot()<=17 ||
					event.getRawSlot()>=27 && event.getRawSlot()<=35)
			{
				event.setCancelled(true);
				return;
			}
			if(event.getRawSlot()>=0 && event.getRawSlot()<=8)
			{
				if(event.getInventory().getItem(event.getRawSlot())==null)
					return;
				
				event.setCancelled(true);
				
				Player p1 = (Player)event.getWhoClicked();
				String type = plugin.playerQuest.get(p1.getName()).get(event.getRawSlot()).get(0);
				String posterName = null;
				for(String lore:event.getInventory().getItem(event.getRawSlot()).getItemMeta().getLore())
				{
					if(lore.contains("§d发布人:"))
					{
						posterName = lore.split(":")[1];
						break;
					}
				}
				
				if(event.getClick()==ClickType.RIGHT)
				{
					plugin.playerQuest.get(p1.getName()).remove(event.getRawSlot());

					if(type.contains("VIPitem"))
						plugin.vipItemQuests.get(posterName).get("player").remove(p1.getName());
					else if(type.contains("VIPhead"))
						plugin.vipHeadQuests.get(posterName).get("player").remove(p1.getName());
					else if(type.contains("item"))
						plugin.itemQuests.get(posterName).get("player").remove(p1.getName());
					else if(type.contains("head"))
						plugin.headQuests.get(posterName).get("player").remove(p1.getName());
					event.getInventory().setItem(event.getRawSlot(), null);
				}
				else if(event.getClick()==ClickType.LEFT)
				{
					if(Bukkit.getServer().getPlayer(posterName)==null)
					{
						p1.closeInventory();
						p1.sendMessage("§a[任务系统] §c该玩家不存在或不在线");
						return;
					}
					Player p2 = Bukkit.getServer().getPlayer(posterName);
					String questType = "";
					if(type.contains("VIPitem"))
						questType = "VIP"+plugin.message.get("QuestGui.ItemQuestTag");
					else if(type.contains("VIPhead"))
						questType = "VIP"+plugin.message.get("QuestGui.HeadQuestTag");
					else if(type.contains("item"))
						questType = plugin.message.get("QuestGui.ItemQuestTag");
					else if(type.contains("head"))
						questType = plugin.message.get("QuestGui.HeadQuestTag");
					p1.openInventory(gui.initConfirmForPlayer(p1, p2, questType, posterName));
					p2.openInventory(gui.initConfirmForPlayer(p2, p1, questType, posterName));
					plugin.confirmPlayers.put(p1.getName(), posterName);
					plugin.confirmPlayers.put(posterName, p1.getName());
				}
			
			}
			
			else if(plugin.isExist(event.getRawSlot(), vipQuestSlot) || plugin.isExist(event.getRawSlot(), normalQuestSlot))
			{
				if(event.getInventory().getItem(event.getRawSlot())==null)
					return;

				event.setCancelled(true);
				if(event.getClick()==ClickType.RIGHT)
				{
					Player p = (Player)event.getWhoClicked();
					String type = event.getInventory().getItem(event.getRawSlot()).getItemMeta().getDisplayName();
					String posterName = p.getName();
					
					// if the quest's tag changed, this must change
					String questName = type.split("§f")[1];
					String lore = "";
					for(String l:event.getInventory().getItem(event.getRawSlot()).getItemMeta().getLore())
					{
						lore+=l;
					}
					
					
					/*
					 *  to get the quest's content by separating with the following methods
					 *  this place must change if the quest's tag changed
					 *  get between "§d发布人" and the color sign "§e" which is in 
					 *  the front of first line of lore
					 */
					lore = lore.split("§d发布人")[0].replace("§e", "");
					HashMap<String, HashMap<String, ArrayList<String>>> replace = new HashMap<String, HashMap<String, ArrayList<String>>>();
					if(event.getRawSlot()>=18 && event.getRawSlot()<=26)
					{
						if(type.contains(plugin.message.get("QuestGui.ItemQuestTag")))
						{
							replace = plugin.vipItemQuests;
						}
						else if(type.contains(plugin.message.get("QuestGui.HeadQuestTag")))
						{
							replace = plugin.vipHeadQuests;
						}
					}
					else if(event.getRawSlot()>=36 && event.getRawSlot()<=54)
					{
						if(type.contains(plugin.message.get("QuestGui.ItemQuestTag")))
						{
							replace = plugin.itemQuests;
						}
						else if(type.contains(plugin.message.get("QuestGui.HeadQuestTag")))
						{
							replace = plugin.headQuests;
						}
					}
					
					if(replace.get(posterName).get("player")!=null)
					{
						for(String otherPlayer:replace.get(posterName).get("player"))
						{

							if(plugin.playerQuest.get(otherPlayer)!=null)
							{
								int index = 0;
								for(ArrayList<String> everyQuest:plugin.playerQuest.get(otherPlayer))
								{
									if(everyQuest.get(1).equalsIgnoreCase(replace.get(posterName).get("quest").get(1)))
										if(everyQuest.get(2).equalsIgnoreCase(replace.get(posterName).get("quest").get(2)))
											if(everyQuest.get(everyQuest.size()-1).equalsIgnoreCase(posterName))
											{
												plugin.playerQuest.get(otherPlayer).remove(index);
												break;
											}

									index+=1;
								}
							}
							
							if(Bukkit.getServer().getPlayer(otherPlayer)!=null)
							{
								Player otherp = Bukkit.getServer().getPlayer(otherPlayer);
								
								if(otherp.getOpenInventory().getTitle().equalsIgnoreCase(plugin.message.get("MyQuestGui.Name")))
								{
									otherp.openInventory(gui.initMyQuest(otherp));
								}
							}	
						}
					}
					
					replace.remove(posterName);

					event.getInventory().setItem(event.getRawSlot(), null);
				}
				else if(event.getClick()==ClickType.LEFT)
				{
					Player p = (Player)event.getWhoClicked();
					String type = event.getInventory().getItem(event.getRawSlot()).getItemMeta().getDisplayName();
					
					HashMap<String, HashMap<String, ArrayList<String>>> replace = new HashMap<String, HashMap<String, ArrayList<String>>>();
					
					if(event.getRawSlot()>=18 && event.getRawSlot()<=26)
					{
						if(type.contains(plugin.message.get("QuestGui.ItemQuestTag")))
						{
							replace = plugin.vipItemQuests;
						}
						else if(type.contains(plugin.message.get("QuestGui.HeadQuestTag")))
						{
							replace = plugin.vipHeadQuests;
						}
					}
					else if(event.getRawSlot()>=36 && event.getRawSlot()<=54)
					{
						if(type.contains(plugin.message.get("QuestGui.ItemQuestTag")))
						{
							replace = plugin.itemQuests;
						}
						else if(type.contains(plugin.message.get("QuestGui.HeadQuestTag")))
						{
							replace = plugin.headQuests;
						}
					}
					p.sendMessage("§e====================");
					if(!replace.get(p.getName()).containsKey("player") ||
							(!replace.get(p.getName()).get("player").isEmpty()))
					{
						for(String everyPlayerName:replace.get(p.getName()).get("player"))
						{
							p.sendMessage("§e玩家: §a"+everyPlayerName);
						}
					}
					else
					{
						p.sendMessage("§c目前没有人领取你的任务");
					}
					
					p.sendMessage("§e====================");
				}
			}
		}
	}
}
