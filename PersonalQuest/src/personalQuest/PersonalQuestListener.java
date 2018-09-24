package personalQuest;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
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
					if(!plugin.economy.has(p, Integer.valueOf(plugin.config.get("NormalItemQuest.Price"))))
					{
						p.closeInventory();
						p.sendMessage("��a[����ϵͳ] ��c���û���㹻�Ľ�Ǯ!");
						return;
					}
					plugin.economy.withdrawPlayer(p, Integer.valueOf(plugin.config.get("NormalItemQuest.Price")));
					p.sendMessage("��a[����"+plugin.message.get("QuestGui.ItemQuestTag")+"����]��e ��������������(���8����,����exit������������):");
					p.closeInventory();
					//plugin.quests.get(p.getName()).add(e)
					plugin.playerPostQuest1.add(p.getName());
					vip.put(p.getName(), false);
					type.put(p.getName(), "item");
				}
				else
				{
					p.closeInventory();
					p.sendMessage("��a[����"+plugin.message.get("QuestGui.ItemQuestTag")+"����]��c ���Ѿ�������һ����Ʒ����������,��ȴ���������ٷ�����һ������.");
				}
				
				return;
			}
			if(event.getRawSlot()==6)
			{
				if(!plugin.headQuests.containsKey(p.getName()) &&
						!plugin.vipHeadQuests.containsKey(p.getName()))
				{
					if(!plugin.economy.has(p, Integer.valueOf(plugin.config.get("NormalHeadQuest.Price"))))
					{
						p.closeInventory();
						p.sendMessage("��a[����ϵͳ] ��c���û���㹻�Ľ�Ǯ!");
						return;
					}
					plugin.economy.withdrawPlayer(p, Integer.valueOf(plugin.config.get("NormalHeadQuest.Price")));
					p.sendMessage("��a[����"+plugin.message.get("QuestGui.HeadQuestTag")+"����]��e ��������������(���8����,����exit������������):");
					p.closeInventory();
					//plugin.quests.get(p.getName()).add(e)
					plugin.playerPostQuest1.add(p.getName());
					vip.put(p.getName(), false);
					type.put(p.getName(), "head");
				}
				else
				{
					p.closeInventory();
					p.sendMessage("��a[����"+plugin.message.get("QuestGui.HeadQuestTag")+"����]��c ���Ѿ�������һ��ͷ­����������,��ȴ���������ٷ�����һ������.");
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
						p.sendMessage("��a[����"+plugin.message.get("QuestGui.ItemQuestTag")+"����]��c VIP��������");
						p.closeInventory();
						return;
					}
					
					if(!plugin.economy.has(p, Integer.valueOf(plugin.config.get("VIPItemQuest.Price"))))
					{
						p.closeInventory();
						p.sendMessage("��a[����ϵͳ] ��c���û���㹻�Ľ�Ǯ!");
						return;
					}
					plugin.economy.withdrawPlayer(p, Integer.valueOf(plugin.config.get("VIPItemQuest.Price")));
					
					p.sendMessage("��a[����"+plugin.message.get("QuestGui.ItemQuestTag")+"����]��e ��������������(���8����,����exit������������):");
					p.closeInventory();
					plugin.playerPostQuest1.add(p.getName());
					vip.put(p.getName(), true);
					type.put(p.getName(), "VIPitem");
				}
				else
				{
					p.closeInventory();
					p.sendMessage("��a[����"+plugin.message.get("QuestGui.ItemQuestTag")+"����]��c ���Ѿ�������һ����Ʒ����������,��ȴ���������ٷ�����һ������.");
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
						p.sendMessage("��a[����"+plugin.message.get("QuestGui.HeadQuestTag")+"����]��c VIP��������");
						p.closeInventory();
						return;
					}
					
					if(!plugin.economy.has(p, Integer.valueOf(plugin.config.get("VIPHeadQuest.Price"))))
					{
						p.closeInventory();
						p.sendMessage("��a[����ϵͳ] ��c���û���㹻�Ľ�Ǯ!");
						return;
					}
					plugin.economy.withdrawPlayer(p, Integer.valueOf(plugin.config.get("VIPHeadQuest.Price")));
					
					p.sendMessage("��a[����"+plugin.message.get("QuestGui.HeadQuestTag")+"����]��e ��������������(���8����,����exit������������):");
					p.closeInventory();
					plugin.playerPostQuest1.add(p.getName());
					vip.put(p.getName(), true);
					type.put(p.getName(), "VIPhead");
				}
				else
				{
					p.closeInventory();
					p.sendMessage("��a[����"+plugin.message.get("QuestGui.HeadQuestTag")+"����]��c ���Ѿ�������һ��ͷ­����������,��ȴ���������ٷ�����һ������.");
				}
				return;
			}
		}
		
		return;
    }
	
	@EventHandler
	private void onPlayerChat(AsyncPlayerChatEvent event)
	{
		if(plugin.playerPostQuest3.contains(event.getPlayer().getName()))
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
				p.sendMessage("��a[����"+typeItem+"����]��3 �ѷ�����������");
				return;
			}
			
			if(event.getMessage().length()>20)
			{
				p.sendMessage("��a[����"+typeItem+"����]��e �������������ִ�����������,����������(���20����,����exit������������):");
				return;
			}
			
			if(event.getMessage().length()==0)
			{
				p.sendMessage("��a[����"+typeItem+"����]��e ������ֲ���Ϊ��,����������(���20����,����exit������������):");
				return;
			}
			
			if(Bukkit.getServer().getPlayer(event.getMessage())==null)
			{
				p.sendMessage("��a[����"+typeItem+"����]��e ����Ҳ�����,����������(���20����,����exit������������):");
				return;
			}
			
			if(plugin.needToBeKilled!=null)
			{
				if(plugin.needToBeKilled.contains(event.getMessage()))
				{
					p.sendMessage("��a[����"+typeItem+"����]��e ������Ѿ�����,����������(���20����,����exit������������):");
					return;
				}
			}
			
			//---------------------------------------------------------------------------
			ArrayList<String> list = quest.get(p.getName());
			list.add(event.getMessage());
			list.add(p.getName());
			quest.put(p.getName(), list);
			
			HashMap<String, ArrayList<String>> questInfo = new HashMap<String, ArrayList<String>>();
			questInfo.put("quest", quest.get(p.getName()));
			
			plugin.playerPostQuest3.remove(event.getPlayer().getName());
			
			plugin.needToBeKilled.add(event.getMessage());
			if(vip.get(p.getName())==true)
			{
				plugin.vipHeadQuests.put(p.getName(), questInfo);
			}
			else
			{
				plugin.headQuests.put(p.getName(), questInfo);
			}
			p.sendMessage("��a[����"+typeItem+"����]��c ��������ɹ�");
		}
		//=====================================================================================
		//=====================================================================================
		else if(plugin.playerPostQuest2.contains(event.getPlayer().getName()))
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
				p.sendMessage("��a[����"+typeItem+"����]��3 �ѷ�����������");
				return;
			}
			
			if(event.getMessage().length()>30)
			{
				p.sendMessage("��a[����"+typeItem+"����]��c ���������������������������,����������(���30����,����exit������������):");
				return;
			}
			
			if(event.getMessage().length()==0)
			{
				p.sendMessage("��a[����"+typeItem+"����]��c ������������Ϊ��,����������(���30����,����exit������������):");
				return;
			}
			
			//--------------------------------------------------------------------
			
			ArrayList<String> list = quest.get(p.getName());
			list.add(event.getMessage());
			if(!typeItem.equalsIgnoreCase(plugin.message.get("QuestGui.HeadQuestTag")))
				list.add(p.getName());
			quest.put(p.getName(), list);
			
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
					plugin.playerPostQuest3.add(p.getName());
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
					plugin.playerPostQuest3.add(p.getName());
				}
			}
			
			
			
			if(!plugin.playerPostQuest3.contains(p.getName()))
			{
				p.sendMessage("��a[����"+typeItem+"����]��c ��������ɹ�");
			}
			else
			{
				p.sendMessage("��a[����"+typeItem+"����]��3 ������������¼");
				p.sendMessage("��a[����"+typeItem+"����]��e ��Ҫ����˭��ͷ­?����������˵�����(���20����,����exit������������):");
			}

		}
		//=====================================================================================
		//=====================================================================================
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
				p.sendMessage("��a[����"+typeItem+"����]��3 �ѷ�����������");
				return;
			}
			
			if(event.getMessage().length()>8)
			{
				p.sendMessage("��a[����"+typeItem+"����]��e ��������������ƴ�����������,����������(���8����,����exit������������):");
				return;
			}
			
			if(event.getMessage().length()==0)
			{
				p.sendMessage("��a[����"+typeItem+"����]��e �������Ʋ���Ϊ��,����������(���8����,����exit������������):");
				return;
			}
			
			//--------------------------------------------------------------------
			ArrayList<String> list = new ArrayList<String>();
			list.add(type.get(p.getName()));
			list.add(event.getMessage());

			quest.put(p.getName(), list);

			plugin.playerPostQuest1.remove(event.getPlayer().getName());
			plugin.playerPostQuest2.add(event.getPlayer().getName());
			p.sendMessage("��a[����"+typeItem+"����]��3 ������������¼");
			p.sendMessage("��a[����"+typeItem+"����]��e ��������������(���30����,����exit������������):");
		}
		
		//===========================================================================
		//===========================================================================

		
	}

}
