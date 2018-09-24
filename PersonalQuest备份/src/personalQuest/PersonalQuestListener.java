package personalQuest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

public class PersonalQuestListener implements Listener
{
	private PersonalQuest plugin;
	private PersonalQuestInitGui gui;
	HashMap<String, ArrayList<String>> quest = new HashMap<String, ArrayList<String>>();
	HashMap<String, Boolean> vip = new HashMap<String, Boolean>();
	HashMap<String, String> type = new HashMap<String, String>();

	public PersonalQuestListener(PersonalQuest plugin, PersonalQuestInitGui gui)
	{
		this.plugin=plugin;
		this.gui=gui;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
    {
		//loadQuestsConfig(event.getPlayer());
		return;
    }
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event)
    {
		//saveQuestsConfig(event.getPlayer());
		return;
    }
	
	/*
	public void loadQuestsConfig(Player p)
	{
		File file=new File(plugin.getDataFolder(),"/Data/quests.yml");
		FileConfiguration config;
		if (!file.exists())
		{
			config = plugin.load(file);
			
			try 
			{
				config.save(file);
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			loadQuestsConfig(p);
			return;
		}
		config = plugin.load(file);
		if(!config.contains(p.getName()))
			return;
		ArrayList<ArrayList<String>> totalQuest = new ArrayList<ArrayList<String>>();
		for(int i=1; config.contains(p.getName()+i); i++)
		{
			plugin.quests.get(p.getName()).clear();
			ArrayList<String> everyQuest = new ArrayList<String>();
			everyQuest.add(config.getString(p.getName()+"."+i+".QuestType"));
			everyQuest.add(config.getString(p.getName()+"."+i+".QuestName"));
			everyQuest.add(config.getString(p.getName()+"."+i+".QuestContent"));
			totalQuest = plugin.quests.get(p.getName());
			totalQuest.add(everyQuest);
			plugin.quests.put(p.getName(), totalQuest);
		}
		
	}
	*/
	/*
	public void saveQuestsConfig(Player p)
	{
		File file=new File(plugin.getDataFolder(),"/Data/quests.yml");
		FileConfiguration config;
		config = plugin.load(file);
		if(!plugin.quests.containsKey(p.getName()))
			return;
		int number = 1;
		for(ArrayList<String> everyQuest:plugin.quests.get(p.getName()))
		{
			config.set(p.getName()+"."+number+".QuestType", everyQuest.get(0));
			config.set(p.getName()+"."+number+".QuestName", everyQuest.get(1));
			config.set(p.getName()+"."+number+".QuestContent", everyQuest.get(2));
			number += 1;
		}
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	*/
	@EventHandler
	public void onPlayerClickGUI(InventoryClickEvent event)
    {
		if(event.getInventory().getTitle().equalsIgnoreCase(plugin.message.get("MainGui.Name")))
		{
			event.setCancelled(true);
			Player p = (Player)event.getWhoClicked();
			if(event.getRawSlot()==2)
			{
				p.openInventory(gui.initQuestGUI(p).get(0));
				return;
			}
		}
		
		if(event.getInventory().getTitle().equalsIgnoreCase(plugin.message.get("QuestGui.Name")))
		{
			Player p = (Player)event.getWhoClicked();
			event.setCancelled(true);
			int page = Integer.valueOf(String.valueOf(event.getInventory().getItem(47).getItemMeta().getDisplayName().charAt(3)));
			if(event.getRawSlot()==49)
			{
				p.openInventory(gui.initAdd(p));
				return;
			}
			
			if(event.getRawSlot()==45)
			{
				ArrayList<Inventory> inventory = gui.initQuestGUI(p);
				if(page==1)
					return;
				else
					p.openInventory(inventory.get(page-2));
				return;
			}
			
			if(event.getRawSlot()==53)
			{
				ArrayList<Inventory> inventory = gui.initQuestGUI(p);
				if(page==inventory.size())
					return;
				else
					p.openInventory(inventory.get(page));
				return;
			}
		}
		
		if(event.getInventory().getTitle().equalsIgnoreCase(plugin.message.get("AddGui.Name")))
		{
			event.setCancelled(true);
			Player p = (Player)event.getWhoClicked();
			if(event.getRawSlot()==2)
			{
				if(!plugin.itemQuests.containsKey(p.getName()) && 
						!plugin.vipItemQuests.containsKey(p.getName()))
				{
					p.sendMessage("§a[发布物品需求任务]§e 请输入任务名称(最大8个字,输入exit放弃发布任务):");
					p.closeInventory();
					//plugin.quests.get(p.getName()).add(e)
					plugin.playerPostQuest1.add(p.getName());
					vip.put(p.getName(), false);
					type.put(p.getName(), "item");
				}
				else
				{
					p.closeInventory();
					p.sendMessage("§a[发布物品需求任务]§c 你已经发布了一个物品需求任务了,请等待任务完成再发布下一个任务.");
				}
				
				return;
			}
			if(event.getRawSlot()==6)
			{
				if(!plugin.headQuests.containsKey(p.getName()) &&
						!plugin.vipHeadQuests.containsKey(p.getName()))
				{
					p.sendMessage("§a[发布头颅悬赏任务]§e 请输入任务名称(最大8个字,输入exit放弃发布任务):");
					p.closeInventory();
					//plugin.quests.get(p.getName()).add(e)
					plugin.playerPostQuest1.add(p.getName());
					vip.put(p.getName(), false);
					type.put(p.getName(), "head");
				}
				else
				{
					p.closeInventory();
					p.sendMessage("§a[发布头颅悬赏任务]§c 你已经发布了一个头颅悬赏任务了,请等待任务完成再发布下一个任务.");
				}
				return;
			}
			if(event.getRawSlot()==11)
			{
				if(!plugin.vipItemQuests.containsKey(p.getName()) &&
						!plugin.itemQuests.containsKey(p.getName()))
				{
					int size = plugin.vipItemQuests.size() + plugin.vipHeadQuests.size();
					
					if(size==8)
					{
						p.sendMessage("§a[发布物品需求任务]§c VIP任务已满");
						p.closeInventory();
						return;
					}
					
					p.sendMessage("§a[发布物品需求任务]§e 请输入任务名称(最大8个字,输入exit放弃发布任务):");
					p.closeInventory();
					plugin.playerPostQuest1.add(p.getName());
					vip.put(p.getName(), true);
					type.put(p.getName(), "VIPitem");
				}
				else
				{
					p.closeInventory();
					p.sendMessage("§a[发布物品需求任务]§c 你已经发布了一个物品需求任务了,请等待任务完成再发布下一个任务.");
				}
				return;
			}
			if(event.getRawSlot()==15)
			{
				if(!plugin.headQuests.containsKey(p.getName()) &&
						!plugin.vipHeadQuests.containsKey(p.getName()))
				{
					int size = plugin.vipItemQuests.size() + plugin.vipHeadQuests.size();
					
					if(size==8)
					{
						p.sendMessage("§a[发布头颅悬赏任务]§c VIP任务已满");
						p.closeInventory();
						return;
					}
					
					p.sendMessage("§a[发布头颅悬赏任务]§e 请输入任务名称(最大8个字,输入exit放弃发布任务):");
					p.closeInventory();
					plugin.playerPostQuest1.add(p.getName());
					vip.put(p.getName(), true);
					type.put(p.getName(), "VIPhead");
				}
				else
				{
					p.closeInventory();
					p.sendMessage("§a[发布头颅悬赏任务]§c 你已经发布了一个头颅悬赏任务了,请等待任务完成再发布下一个任务.");
				}
				return;
			}
		}
		
		return;
    }
	
	@EventHandler
	private void onPlayerChat(AsyncPlayerChatEvent event)
	{
		if(plugin.playerPostQuest2.contains(event.getPlayer().getName()))
		{
			
			event.setCancelled(true);
			Player p = event.getPlayer();
			String typeItem = null;
			if(type.get(p.getName()).equalsIgnoreCase("item") 
					|| type.get(p.getName()).equalsIgnoreCase("VIPitem"))
				typeItem=plugin.message.get("QuestGui.ItemQuestTag");
			else if(type.get(p.getName()).equalsIgnoreCase("head") 
					|| type.get(p.getName()).equalsIgnoreCase("VIPhead"))
				typeItem=plugin.message.get("QuestGui.HeadQuestTag");
				
			if(event.getMessage().toLowerCase().equalsIgnoreCase("exit"))
			{
				plugin.playerPostQuest2.remove(p.getName());
				p.sendMessage("§a[发布"+typeItem+"任务]§3 已放弃发布任务");
				return;
			}
			
			if(event.getMessage().length()>30)
			{
				p.sendMessage("§a[发布"+typeItem+"任务]§e 你输入的任务描述大于字数限制,请重新输入(最大30个字,输入exit放弃发布任务):");
				return;
			}
			
			if(event.getMessage().length()==0)
			{
				p.sendMessage("§a[发布"+typeItem+"任务]§e 任务描述不能为空,请重新输入(最大30个字,输入exit放弃发布任务):");
				return;
			}
			
			//--------------------------------------------------------------------
			
			quest.get(p.getName()).add(event.getMessage());

			
			plugin.playerPostQuest2.remove(event.getPlayer().getName());
			HashMap<String, ArrayList<String>> questInfo = new HashMap<String, ArrayList<String>>();
			questInfo.put("quest", quest.get(p.getName()));
			if(vip.get(p.getName())==true)
			{
				if(typeItem.equalsIgnoreCase(plugin.message.get("QuestGui.ItemQuestTag")))
				{
					plugin.vipItemQuests.put(p.getName(), questInfo);
				}
				else if(typeItem.equalsIgnoreCase(plugin.message.get("QuestGui.HeadQuestTag")))
				{
					plugin.vipHeadQuests.put(p.getName(), questInfo);
				}
			}
			else
			{
				if(typeItem.equalsIgnoreCase(plugin.message.get("QuestGui.ItemQuestTag")))
				{
					plugin.itemQuests.put(p.getName(), questInfo);
				}
				else if(typeItem.equalsIgnoreCase(plugin.message.get("QuestGui.HeadQuestTag")))
				{
					plugin.headQuests.put(p.getName(), questInfo);
				}
			}
			p.sendMessage("§a[发布"+typeItem+"任务]§c 发布任务成功");

		}
		else if(plugin.playerPostQuest1.contains(event.getPlayer().getName()))
		{
			event.setCancelled(true);
			Player p = event.getPlayer();
			String typeItem = null;
			if(type.get(p.getName()).equalsIgnoreCase("item") || type.get(p.getName()).equalsIgnoreCase("VIPitem"))
				typeItem=plugin.message.get("QuestGui.ItemQuestTag");
			else if(type.get(p.getName()).equalsIgnoreCase("head") || type.get(p.getName()).equalsIgnoreCase("VIPhead"))
				typeItem=plugin.message.get("QuestGui.HeadQuestTag");
			
			if(event.getMessage().toLowerCase().equalsIgnoreCase("exit"))
			{
				plugin.playerPostQuest1.remove(p.getName());
				p.sendMessage("§a[发布"+typeItem+"任务]§3 已放弃发布任务");
				return;
			}
			
			if(event.getMessage().length()>8)
			{
				p.sendMessage("§a[发布"+typeItem+"任务]§e 你输入的任务名称大于字数限制,请重新输入(最大8个字,输入exit放弃发布任务):");
				return;
			}
			
			if(event.getMessage().length()==0)
			{
				p.sendMessage("§a[发布"+typeItem+"任务]§e 任务名称不能为空,请重新输入(最大8个字,输入exit放弃发布任务):");
				return;
			}
			
			//--------------------------------------------------------------------
			ArrayList<String> list = new ArrayList<String>();
			quest.put(p.getName(), list);
			quest.get(p.getName()).add(type.get(p.getName()));
			quest.get(p.getName()).add(event.getMessage());

			plugin.playerPostQuest1.remove(event.getPlayer().getName());
			plugin.playerPostQuest2.add(event.getPlayer().getName());
			p.sendMessage("§a[发布"+typeItem+"任务]§3 任务名称已收录");
			p.sendMessage("§a[发布"+typeItem+"任务]§e 请输入任务描述(最大30个字,输入exit放弃发布任务):");
		}
		
		//===========================================================================
		//===========================================================================

		
	}

}
