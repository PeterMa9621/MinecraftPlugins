package dailyQuest;

import dailyQuest.config.ConfigManager;
import dailyQuest.manager.QuestPlayerManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.citizensnpcs.api.CitizensAPI;
import net.milkbowl.vault.economy.Economy;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import peterUtil.database.Database;
import peterUtil.database.DatabaseType;
import peterUtil.database.StorageInterface;

public class DailyQuest extends JavaPlugin
{
	Random rand = new Random();
	
	//HashMap<Integer, String> npc = new HashMap<Integer, String>();


	/**
	 *  In the hash map, keys are players' names. The value means the status of players
	 *  The first index means the current index of the quest		
	 *  The second index means what the quest is
	 *	The last index means how many quests totally this player has already finished
	 */

	ArrayList<String> cancelQuestPlayer = new ArrayList<String>();
	HashMap<String, Integer> cancelTask = new HashMap<String, Integer>();

	DailyQuestAPI api = new DailyQuestAPI(this);

	private ConfigManager configManager;
	public QuestPlayerManager questPlayerManager;
	
	public Economy economy;
	
	private boolean setupEconomy()
	{
		RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServicesManager().getRegistration(Economy.class);
		if (economyProvider != null)
		{
			economy = economyProvider.getProvider();
		}
		return (economy!=null);
	}
	
	public DailyQuestAPI getAPI()
	{
		return api;
	}
	
	public void onEnable() 
	{
		if(!new File(getDataFolder(),"Data").exists()) 
		{
			new File(getDataFolder(),"Data").mkdirs();
		}
		if(setupEconomy()==false)
			Bukkit.getConsoleSender().sendMessage("��a[DailyQuest] ��cVault���δ����!");

		configManager = new ConfigManager(this);
		questPlayerManager = new QuestPlayerManager(this);

		configManager.loadItemConfig();
		configManager.loadQuestConfig();
		configManager.initDatabase();

		task();
		getServer().getPluginManager().registerEvents(new MobQuestListener(this), this);
		getServer().getPluginManager().registerEvents(new FinishQuestListener(this), this);
		getServer().getPluginManager().registerEvents(new GetQuestListener(this), this);
		CitizensAPI.registerEvents(new DailyQuestListener(this));
		Bukkit.getConsoleSender().sendMessage("��a[DailyQuest] ��e�ճ�����ϵͳ�������");
	}



	public void onDisable() 
	{
		for(QuestPlayer questPlayer:questPlayerManager.getQuestPlayers().values()) {
			configManager.savePlayerConfig(questPlayer);
		}

		saveConfig();
		Bukkit.getConsoleSender().sendMessage("��a[DailyQuest] ��e�ճ�����ϵͳж�����");
	}
	
	public void task()
	{
		new BukkitRunnable()
		{
    		String previousDate = null;
			public void run()
			{
				if(previousDate!=null && (!previousDate.equalsIgnoreCase(date.format(new Date()))))
				{
					for(Player p:Bukkit.getOnlinePlayers())
					{
						QuestPlayer pd = questPlayers.get(p.getName());
						if(pd.getCurrentNumber()==0)
						{
							pd.setTotalQuest(0);
							pd.setWhatTheQuestIs(0);
							pd.setCurrentNumber(0);
						}
						else
						{
							pd.setTotalQuest(0);
							pd.setCurrentNumber(1);
						}
					}
				}
				previousDate = date.format(new Date());
			}
		}.runTaskTimer(this, 0L, 20L);
	}
	

	
	public Inventory getQuestGUI(Player p, String name, int NPCID)
	{
		Inventory inv = Bukkit.createInventory(p, 9, "��8�ճ�����");

		ItemStack button1 = createItem(368, 1, 0, "��6��������ȡ�����");
		ItemStack button4 = createItem(368, 1, 0, "��c���������������");
		ItemStack button2 = createItem(331, 1, 0, "��6�ҵ����");
		ItemStack button3 = createItem(368, 1, 0, "��a����֪��ʲô��ÿ�������");
		ItemStack describe = createItem(397, 1, 3, "��3"+name+"��a����˵:", "��5��������ʲô����?");
		inv.setItem(0, describe);

		inv.setItem(2, button1);
		inv.setItem(3, button4);
		inv.setItem(4, button3);
		inv.setItem(5, button2);

		return inv;
	}
	
	public Inventory createGUI(Player p, String name, int NPCID)
	{
		Inventory inv = Bukkit.createInventory(p, 9, "��8NPC");
		ItemStack describe = null;
		ItemStack button1 = createItem(368, 1, 0, "��6�������������");
		ItemStack button2 = createItem(331, 1, 0, "��6�ҵ����");
		describe = createItem(397, 1, 3, "��3"+name+"��a����˵:", "��5��������ʲô����?");
		inv.setItem(0, describe);
		if(questPlayers.get(p.getName()).getCurrentNumber()!=0 &&
				quests.get(questPlayers.get(p.getName()).getWhatTheQuestIs()).getNPCId()==NPCID)
		{
			inv.setItem(0, describe);
			inv.setItem(3, button1);
			inv.setItem(5, button2);
		}
		else
		{
			inv.setItem(4, button2);
		}
		return inv;
	}
	
	public ArrayList<Inventory> questItemGUI(Player p)
	{
		Inventory inv = Bukkit.createInventory(p, 54, "����������Ʒ-ҳ��:1");
		ItemStack next = createItem(351, 1, 13, "��3���������һҳ");
		ItemStack previous = createItem(351, 1, 8, "��3���������һҳ");
		inv.setItem(47, previous);
		inv.setItem(51, next);
		ArrayList<Inventory> list = new ArrayList<Inventory>();
		for(int i=0; i<quests.size(); i++)
		{
			
			if(quests.get(i).getQuest().getType().equalsIgnoreCase("item"))
			{
				inv.setItem(i%44, quests.get(i).getQuest().getQuestItem());
			}
			if(i>43 && i/44==0)
			{
				list.add(inv);
				inv = Bukkit.createInventory(p, 54, "����������Ʒ-ҳ��:"+(((i+1)/44)+1));
				inv.setItem(47, previous);
				inv.setItem(51, next);
			}
		}
		
		list.add(inv);
		
		return list;
	}
	
	public ArrayList<Inventory> rewardItemGUI(Player p)
	{
		Inventory inv = Bukkit.createInventory(p, 54, "���н�����Ʒ-ҳ��:1");
		ItemStack next = createItem(351, 1, 13, "��3���������һҳ");
		ItemStack previous = createItem(351, 1, 8, "��3���������һҳ");
		ItemStack interval = createItem(160, 1, 0, " ");
		inv.setItem(47, previous);
		inv.setItem(51, next);
		ArrayList<Inventory> list = new ArrayList<Inventory>();
		int index = 0;
		for(int i=0; i<quests.size(); i++)
		{
			if(quests.get(i).getQuest().getType().equalsIgnoreCase("item"))
			{
				for(ItemStack item: rewards)
				{
					inv.setItem(index%44, item);
					index++;
					if(index>43 && index/44==0)
					{
						list.add(inv);
						inv = Bukkit.createInventory(p, 54, "���н�����Ʒ-ҳ��:"+(((index+1)/44)+1));
						inv.setItem(47, previous);
						inv.setItem(51, next);
					}
				}
				inv.setItem(index%44, interval);
				index ++;
				
			}
			if(index>43 && index/44==0)
			{
				list.add(inv);
				inv = Bukkit.createInventory(p, 54, "���н�����Ʒ-ҳ��:"+(((index+1)/44)+1));
				inv.setItem(47, previous);
				inv.setItem(51, next);
			}
		}
		
		list.add(inv);
		
		return list;
	}
	
	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args)  
	{
		if (cmd.getName().equalsIgnoreCase("rw"))
		{
			if (args.length==0)
			{
				if(sender instanceof Player)
				{
					Player p = (Player)sender;
					if(questPlayers.get(p.getName()).getCurrentNumber()==0)
					{
						p.sendMessage("��6[�ճ�����] ��a��Ŀǰû�н�����������/rw get��ȡ����!");
						return true;
					}
					p.sendMessage("��6[��"+ questPlayers.get(p.getName()).getCurrentNumber()+"��] ��a"+quests.get(questPlayers.get(p.getName()).getWhatTheQuestIs()).getQuestDescribe());
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("help"))
			{
				sender.sendMessage("��6=========[�ճ�����]=========");
				sender.sendMessage("��6/rw ��8- ��e�鿴��ǰ����");
				if(enableCommandGetQuest==true)
					sender.sendMessage("��6/rw get ��8- ��e��ȡ������");
				sender.sendMessage("��6/rw quit ��8- ��e������ǰ����");
				sender.sendMessage("��6/rw info ��8- ��e�鿴���������״̬");
				sender.sendMessage("��6/rw help ��8- ��e�鿴����");
				if(sender.isOp())
				{
					sender.sendMessage("��6/rw set [NPCID] ��8- ��e������ȡ�����NPCID");
					sender.sendMessage("��6/rw clear [�����] ��8- ��e���ø���ҵ���������");
					sender.sendMessage("��6/rw quest ��8- ��e��ʾ����������Ʒ");
					sender.sendMessage("��6/rw reward ��8- ��e��ʾ���н�����Ʒ");
					sender.sendMessage("��6/rw reload ��8- ��e��������");
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("set"))
			{
				if(sender.isOp())
				{
					if(args.length==2)
					{
						if(args[1].matches("[0-9]*"))
						{
							if(CitizensAPI.getNPCRegistry().getById(Integer.valueOf(args[1]))!=null)
							{
								getQuestNPCId = Integer.valueOf(args[1]);
								sender.sendMessage("��6[�ճ�����] ��a�������");
							}
						}
					}
				}
				return true;
			}
			if(args[0].equalsIgnoreCase("clear"))
			{
				if(sender.isOp())
				{
					if(args.length==2)
					{
						if(!this.questPlayers.containsKey(args[1]))
						{
							sender.sendMessage("��6[�ճ�����] ��cû�и���ҵ��������ݣ��޷�����");
							return true;
						}
						if(Bukkit.getServer().getPlayer(args[1])!=null)
						{
							QuestPlayer player = questPlayers.get(args[1]);
							player.setCurrentNumber(0);
							player.setWhatTheQuestIs(0);
							player.setTotalQuest(0);
							questPlayers.put(args[1], player);
							Bukkit.getServer().getPlayer(args[1]).sendMessage("��6[�ճ�����] ��3������������ѱ�����");
							sender.sendMessage("��6[�ճ�����] ��e��������ҡ�d"+args[1]+"��e����������");
						}
						else
						{
							sender.sendMessage("��6[�ճ�����] ��c����Ҳ����ڻ�����");
						}
					}
					else
					{
						sender.sendMessage("��6/rw clear [�����] ��8- ��e���ø���ҵ���������");
					}
				}
				else
				{
					sender.sendMessage("��6[�ճ�����] ��c��û��Ȩ��!");
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("reward"))
			{
				if(sender.isOp())
				{
					Player p = (Player)sender;
					p.openInventory(rewardItemGUI(p).get(0));
				}
				else
				{
					sender.sendMessage("��6[�ճ�����] ��c��û��Ȩ��!");
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("quest"))
			{
				if(sender.isOp())
				{
					Player p = (Player)sender;
					p.openInventory(questItemGUI(p).get(0));
				}
				else
				{
					sender.sendMessage("��6[�ճ�����] ��c��û��Ȩ��!");
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("reload"))
			{
				if(sender.isOp())
				{
					loadConfig();
					loadItemConfig();
					loadQuestConfig();
					sender.sendMessage("��6[�ճ�����] ��3�������سɹ�!");
				}
				else
				{
					sender.sendMessage("��6[�ճ�����] ��c��û��Ȩ��!");
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("info"))
			{
				if(sender instanceof Player)
				{
					Player p = (Player)sender;
					if(questPlayers.containsKey(p.getName()))
					{
						int questLimit = defaultQuantity;
						for(String permission:group.keySet())
						{
							if(p.hasPermission("dailyQuest.limit."+permission))
							{
								questLimit = group.get(permission);
							}
						}
						String msg = "��e�����������:��d"+questLimit+"��e,�������:��d"+ questPlayers.get(p.getName()).getTotalQuest()+"��e,";
						if(questPlayers.get(p.getName()).getCurrentNumber()==0)
							msg += "��Ŀǰû�н�������";
						else
							msg += "��ǰΪ�ڡ�d"+ questPlayers.get(p.getName()).getCurrentNumber()+"��e������";
						p.sendMessage(msg);
					}
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("get"))
			{
				if(sender instanceof Player)
				{
					Player p = (Player)sender;
					int index = random(quests.size());
					if(enableCommandGetQuest==false)
					{
						p.sendMessage("��6[�ճ�����] ��c�޷�ʹ��ָ���ȡ��������"+CitizensAPI.getNPCRegistry().getById(getQuestNPCId).getFullName()+"����ȡ");
						return true;
					}
					if(questPlayers.get(p.getName()).getCurrentNumber()!=0)
					{
						p.sendMessage("��6[�ճ�����] ��a��Ŀǰ��������������ȡ��������������ȡ����!");
						return true;
					}
					int questLimit = defaultQuantity;
					for(String permission:this.group.keySet())
					{
						if(p.hasPermission("dailyQuest.limit."+permission))
						{
							questLimit = this.group.get(permission);
						}
					}
					if(questPlayers.get(p.getName()).getTotalQuest()>=questLimit)
					{
						p.sendMessage("��6[�ճ�����] ��a�����������Ѵ����ޣ�����������!");
						return true;
					}
					
					// Get the state of this player
					// the first index means the current index of the quest
					// the second index means what the quest is
					// the last index means how many quests totally this player has already finished
					QuestPlayer player = questPlayers.get(p.getName());
					player.setCurrentNumber(1);
					player.setWhatTheQuestIs(index);
					questPlayers.put(p.getName(), player);
					p.sendMessage("��6[�� 1 ��] ��a"+quests.get(index).getQuestDescribe());
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("quit"))
			{
				if(sender instanceof Player)
				{
					Player p = (Player)sender;
					if(questPlayers.get(p.getName()).getCurrentNumber()!=0)
					{
						if(cancelQuestPlayer.contains(p.getName()))
						{
							QuestPlayer player = questPlayers.get(p.getName());
							player.setCurrentNumber(0);
							player.setWhatTheQuestIs(0);

							questPlayers.put(p.getName(), player);
							cancelQuestPlayer.remove(p.getName());
							getServer().getScheduler().cancelTask(cancelTask.get(p.getName()));
							p.sendMessage("��6[�ճ�����] ��c�ѷ�����ǰ������!");
							return true;
						}
						else
						{
							p.sendMessage("��6[�ճ�����] ��d��ȷ��Ҫ������������?����10�����ٴ�����һ���c/rw quit��d��ȷ��");
							cancelQuestPlayer.add(p.getName());
							cancelTask.put(p.getName(), getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
							{
								public void run()
								{
									if(cancelQuestPlayer.contains(p.getName()))
									{
										p.sendMessage("��6[�ճ�����] ��7ȡ����������");
										cancelQuestPlayer.remove(p.getName());
									}
									getServer().getScheduler().cancelTask(cancelTask.get(p.getName()));
								}
							} ,10*20,20));
							return true;
						}
					}

					p.sendMessage("��6[�ճ�����] ��a��Ŀǰû���κ�����!");
				}
				return true;
			}

		}
		return false;
		
	}
	
	
}

