package dailyQuest;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.citizensnpcs.api.CitizensAPI;
import net.milkbowl.vault.economy.Economy;
import net.minecraft.server.v1_10_R1.EntitySpider.GroupDataSpider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class DailyQuest extends JavaPlugin
{
	Random rand = new Random();
	
	//HashMap<Integer, String> npc = new HashMap<Integer, String>();
	ArrayList<Integer> npcID = new ArrayList<Integer>();
	ArrayList<QuestInfo> quests = new ArrayList<QuestInfo>();
	/**
	 *  In the hash map, keys are players' names. The value means the status of players
	 *  The first index means the current index of the quest		
	 *  The second index means what the quest is
	 *	The last index means how many quests totally this player has already finished
	 */
	HashMap<String, PlayerData> playerData = new HashMap<String, PlayerData>();
	ArrayList<String> cancelQuestPlayer = new ArrayList<String>();
	HashMap<String, Integer> cancelTask = new HashMap<String, Integer>();
	HashMap<String, ArrayList<ItemStack>> item = new HashMap<String, ArrayList<ItemStack>>();
	HashMap<String, Integer> group = new HashMap<String, Integer>();
	
	SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
	
	int defaultQuantity = 0;
	
	DailyQuestAPI api = new DailyQuestAPI(this);
	
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
			Bukkit.getConsoleSender().sendMessage("§a[DailyQuest] §cVault插件未加载!");
		loadConfig();
		loadItemConfig();
		loadQuestConfig();
		loadPlayerConfig();
		getServer().getPluginManager().registerEvents(new FinishQuestListener(this), this);
		CitizensAPI.registerEvents(new DailyQuestListener(this));
		Bukkit.getConsoleSender().sendMessage("§a[DailyQuest] §e日常任务系统加载完毕");
	}

	public void onDisable() 
	{
		savePlayerConfig();
		Bukkit.getConsoleSender().sendMessage("§a[DailyQuest] §e日常任务系统卸载完毕");
	}
	
	public void loadItemConfig()
	{
		File file=new File(getDataFolder(),"item.yml");
		FileConfiguration config;
		if (!file.exists())
		{
			config = load(file);
			config.set("Quest1.Item.1.ID", 1);
			config.set("Quest1.Item.1.Data", 0);
			config.set("Quest1.Item.1.Amount", 1);

			config.set("Quest1.Item.2.ID", 263);
			config.set("Quest1.Item.2.Data", 1);
			config.set("Quest1.Item.2.Amount", 1);
			config.set("Quest1.Item.2.Name", "§f未鉴定的宝石");
			config.set("Quest1.Item.2.Lore", "§e[未鉴定]%§6一块看起来普通的石头");
			config.set("Quest1.Item.2.Enchantment.ID", -1);
			config.set("Quest1.Item.2.Enchantment.Level", 0);
			
			try {
				config.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			loadItemConfig();
		}
		config = load(file);
		
		for(String quest:config.getKeys(false))
		{
			ArrayList<ItemStack> items = new ArrayList<ItemStack>();
			for(int i=0; config.contains(quest+".Item."+(i+1)); i++)
			{
				ItemStack item = null;
				int id = config.getInt(quest+".Item."+(i+1)+".ID");
				int data = config.getInt(quest+".Item."+(i+1)+".Data");
				int amount = config.getInt(quest+".Item."+(i+1)+".Amount");
				
				String name = config.getString(quest+".Item."+(i+1)+".Name");
				String lore = config.getString(quest+".Item."+(i+1)+".Lore");
				int enchantID = config.getInt(quest+".Item."+(i+1)+".Enchantment.ID");
				int enchantLevel = config.getInt(quest+".Item."+(i+1)+".Enchantment.Level");
				
				if(name==null || lore==null)
					item = new ItemStack(id, amount, (short)data);
				else
					item = createItem(id, amount, data, name, lore);
				
				if(enchantID!=-1 && enchantLevel>0)
					item.addUnsafeEnchantment(Enchantment.getById(enchantID), enchantLevel);
				items.add(item);
			}
			item.put(quest, items);
		}
	}
	
	public void loadConfig()
	{
		File file=new File(getDataFolder(),"config.yml");
		FileConfiguration config;
		if (!file.exists())
		{
			config = load(file);
			
			String[] group = {"VIP1:30","VIP2:40","VIP3:50"};
			
			config.set("DefaultQuantity", 10);
			
			config.set("Group", group);
			
			
			try {
				config.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			loadConfig();
		}
		
		config = load(file);
		
		defaultQuantity = config.getInt("DefaultQuantity");
		
		List<String> group = config.getStringList("Group");
		
		for(String g:group)
		{
			this.group.put(g.split(":")[0], Integer.valueOf(g.split(":")[1]));
		}
	}
	
	public void loadPlayerConfig()
	{
		for(OfflinePlayer p:Bukkit.getServer().getOfflinePlayers())
		{
			File file=new File(getDataFolder(),"/Data/" + p.getName()+ ".yml");
			FileConfiguration config;
			
			if(file.exists())
			{
				config = load(file);
				
				int currentQuestNumber = config.getInt("CurrentQuestNumber");
				int currentQuestIndex = config.getInt("CurrentQuestIndex");
				int totalQuestNumber = config.getInt("TotalQuestNumber");
				
				String lastLogout = config.getString("LastLogout");
				PlayerData player = new PlayerData(currentQuestNumber, currentQuestIndex, totalQuestNumber, lastLogout);
				playerData.put(p.getName(), player);
			}
			
		}
		for(String playerName:playerData.keySet())
		{
			File file=new File(getDataFolder(),"/Data/" + playerName+ ".yml");
			FileConfiguration config;
			
			config = load(file);
			
			PlayerData player = playerData.get(playerName);
			config.set("LastLogout", player.getLastLogout());
			
			config.set("CurrentQuestNumber", player.getCurrentNumber());
			config.set("CurrentQuestIndex", player.getWhatTheQuestIs());
			config.set("TotalQuestNumber", player.getTotalQuest());
			
			try {
				config.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void savePlayerConfig()
	{
		for(String playerName:playerData.keySet())
		{
			File file=new File(getDataFolder(),"Data/" + playerName+ ".yml");
			FileConfiguration config;
			
			config = load(file);
			
			PlayerData player = playerData.get(playerName);
			config.set("LastLogout", player.getLastLogout());
			
			config.set("CurrentQuestNumber", player.getCurrentNumber());
			config.set("CurrentQuestIndex", player.getWhatTheQuestIs());
			config.set("TotalQuestNumber", player.getTotalQuest());
			
			try {
				config.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		for(Player p:Bukkit.getOnlinePlayers())
		{
			File file=new File(getDataFolder(),"Data/" + p.getName()+ ".yml");
			FileConfiguration config;
			
			config = load(file);
			
			config.set("LastLogout", date.format(new Date()));
			
			try {
				config.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void loadQuestConfig()
	{
		File file=new File(getDataFolder(),"quest.yml");
		FileConfiguration config;
		if (!file.exists())
		{
			config = load(file);
			
			config.set("Quest.1.Type", "mob");
			config.set("Quest.1.Describe", "§a请前往坐标(-344,100,256)附近上交石头10个");
			config.set("Quest.1.RewardMessage", "§7你完成了任务");
			config.set("Quest.1.NPCID", 9);
			config.set("Quest.1.Item.ID", 1);
			config.set("Quest.1.Item.Data", 0);
			config.set("Quest.1.Item.Amount", 10);
			config.set("Quest.1.Item.Name", "AAA");
			config.set("Quest.1.Item.Lore", "BBB%CCC");
			config.set("Quest.1.Item.Enchantment.ID", 0);
			config.set("Quest.1.Item.Enchantment.Level", 1);
			config.set("Quest.1.MobID", 0);
			config.set("Quest.1.Reward.Money", 50);
			config.set("Quest.1.Reward.Item", "Quest1");
			
			for(int i=1; i<10; i++)
			{
				config.set("Quest."+(i+1)+".Type", "item");
				config.set("Quest."+(i+1)+".Describe", "§a请前往坐标(-344,100,256)附近上交石头10个");
				config.set("Quest."+(i+1)+".RewardMessage", "§7你完成了任务");
				config.set("Quest."+(i+1)+".NPCID", 9);
				config.set("Quest."+(i+1)+".Item.ID", 1);
				config.set("Quest."+(i+1)+".Item.Data", 0);
				config.set("Quest."+(i+1)+".Item.Amount", 10);
				config.set("Quest."+(i+1)+".Item.Enchantment.ID", -1);
				config.set("Quest."+(i+1)+".Item.Enchantment.Level", 0);
				config.set("Quest."+(i+1)+".Reward.Money", 100);
				config.set("Quest."+(i+1)+".Reward.Item", "Quest1");
			}

			try 
			{
				config.save(file);
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			
			loadQuestConfig();
		}
		
		npcID.clear();
		quests.clear();
		
		config = load(file);
		
		for(int i=0; config.contains("Quest."+(i+1)); i++)
		{
			String type = config.getString("Quest."+(i+1)+".Type");
			String describe = config.getString("Quest."+(i+1)+".Describe");
			String rewardMessage = config.getString("Quest."+(i+1)+".RewardMessage");
			int NPCID = config.getInt("Quest."+(i+1)+".NPCID");
			int itemID = config.getInt("Quest."+(i+1)+".Item.ID");
			int itemData = config.getInt("Quest."+(i+1)+".Item.Data");
			int amount = config.getInt("Quest."+(i+1)+".Item.Amount");
			String name = config.getString("Quest."+(i+1)+".Item.Name");
			String lore = config.getString("Quest."+(i+1)+".Item.Lore");
			int enchantID = config.getInt("Quest."+(i+1)+".Item.Enchantment.ID");
			int enchantLevel = config.getInt("Quest."+(i+1)+".Item.Enchantment.Level");
			int mobID = config.getInt("Quest."+(i+1)+".MobID");
			int money = config.getInt("Quest."+(i+1)+".Reward.Money");
			
			String rewardItemList = config.getString("Quest."+(i+1)+".Reward.Item");
			
			/*
			int rewardItemID = config.getInt("Quest."+(i+1)+".Reward.Item.ID");
			int rewardItemData = config.getInt("Quest."+(i+1)+".Reward.Item.Data");
			int rewardItemAmount = config.getInt("Quest."+(i+1)+".Reward.Item.Amount");
			String rewardItemName = config.getString("Quest."+(i+1)+".Reward.Item.Name");
			String rewardItemLore = config.getString("Quest."+(i+1)+".Reward.Item.Lore");
			int rewardItemEnchantID = config.getInt("Quest."+(i+1)+".Reward.Item.Enchantment.ID");
			int rewardItemEnchantLevel = config.getInt("Quest."+(i+1)+".Reward.Item.Enchantment.Level");
			*/
			
			// Get the npcID
			if(!npcID.contains(NPCID))
				npcID.add(NPCID);
			
			//npc.put(NPCID, CitizensAPI.getNPCRegistry().getById(NPCID).getName());

			// Get the quest item
			ItemStack item = null;
			if(name==null || lore==null)
			{
				item = new ItemStack(itemID, amount, (short)itemData);
			}
			else
			{
				item = createItem(itemID, amount, itemData, name, lore);
			}
			if(enchantID!=-1 && enchantLevel>0)
				item.addUnsafeEnchantment(Enchantment.getById(enchantID), enchantLevel);

			// Get the reward item
			ArrayList<ItemStack> itemList = null;
			if(rewardItemList!=null)
			{
				itemList = this.item.get(rewardItemList);
			}
			/*
			ItemStack rewardItem = null;
			if(rewardItemName==null && rewardItemLore==null)
			{
				rewardItem = new ItemStack(rewardItemID, rewardItemAmount, (short)rewardItemData);
			}
			else
			{
				rewardItem = createItem(rewardItemID, rewardItemAmount, rewardItemData, rewardItemName, rewardItemLore);
			}
			if(rewardItemEnchantID!=-1 && rewardItemEnchantLevel>0)
				rewardItem.addUnsafeEnchantment(Enchantment.getById(rewardItemEnchantID), rewardItemEnchantLevel);
			 */
			// Save data in Quest class
			
			Quest quest = null;
			if(mobID!=0)
				quest = new Quest(type, mobID);
			else
				quest = new Quest(type, item);
			
			// Save data in QuestInfo class
			QuestInfo questInfo = new QuestInfo(quest, NPCID, money, itemList, describe, rewardMessage);
			quests.add(questInfo);
		}
		return;
		
	}
	
	public int random(int range)
	{
		int i = rand.nextInt(range); //生成随机数
		return i;
	}
	
	public ItemStack createItem(int ID, int quantity, int durability, String displayName, String lore)
	{
		ItemStack item = new ItemStack(ID, quantity, (short)durability);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(displayName);
		ArrayList<String> loreList = new ArrayList<String>();
		for(String l:lore.split("%"))
		{
			loreList.add(l);
		}
		meta.setLore(loreList);
		item.setItemMeta(meta);
		
		return item;
	}
	
	public ItemStack createItem(int ID, int quantity, int durability, String displayName)
	{
		ItemStack item = new ItemStack(ID, quantity, (short)durability);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(displayName);
		item.setItemMeta(meta);
		
		return item;
	}
	
	public FileConfiguration load(File file)
	{
        if (!(file.exists())) 
        { //假如文件不存在
        	try   //捕捉异常，因为有可能创建不成功
        	{
        		file.createNewFile();
        	}
        	catch(IOException e)
        	{
        		e.printStackTrace();
        	}
        }
        return YamlConfiguration.loadConfiguration(file);
	}
	public FileConfiguration load(String path)
	{
		File file=new File(path);
		if(!file.exists())
		{
			try
		{
				file.createNewFile();
		}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		return YamlConfiguration.loadConfiguration(new File(path));
	}
	
	public Inventory createGUI(Player p, String name, int NPCID)
	{
		Inventory inv = Bukkit.createInventory(p, 9, "§8NPC");
		ItemStack describe = null;
		ItemStack button1 = createItem(368, 1, 0, "§6我是来交任务的");
		ItemStack button2 = createItem(331, 1, 0, "§6我点错了");
		describe = createItem(397, 1, 3, "§3"+name+"§a对你说:", "§5你找我有什么事吗?");
		inv.setItem(0, describe);
		if(playerData.get(p.getName()).getCurrentNumber()!=0 && 
				quests.get(playerData.get(p.getName()).getWhatTheQuestIs()).getNPCId()==NPCID)
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
		Inventory inv = Bukkit.createInventory(p, 54, "所有任务物品-页数:1");
		ItemStack next = createItem(351, 1, 13, "§3点击进入下一页");
		ItemStack previous = createItem(351, 1, 8, "§3点击进入上一页");
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
				inv = Bukkit.createInventory(p, 54, "所有任务物品-页数:"+(((i+1)/44)+1));
				inv.setItem(47, previous);
				inv.setItem(51, next);
			}
		}
		
		list.add(inv);
		
		return list;
	}
	
	public ArrayList<Inventory> rewardItemGUI(Player p)
	{
		Inventory inv = Bukkit.createInventory(p, 54, "所有奖励物品-页数:1");
		ItemStack next = createItem(351, 1, 13, "§3点击进入下一页");
		ItemStack previous = createItem(351, 1, 8, "§3点击进入上一页");
		ItemStack interval = createItem(160, 1, 0, " ");
		inv.setItem(47, previous);
		inv.setItem(51, next);
		ArrayList<Inventory> list = new ArrayList<Inventory>();
		int index = 0;
		for(int i=0; i<quests.size(); i++)
		{
			if(quests.get(i).getQuest().getType().equalsIgnoreCase("item"))
			{
				ArrayList<ItemStack> itemList = quests.get(i).getRewardItem();
				for(ItemStack item:itemList)
				{
					inv.setItem(index%44, item);
					index++;
					if(index>43 && index/44==0)
					{
						list.add(inv);
						inv = Bukkit.createInventory(p, 54, "所有奖励物品-页数:"+(((index+1)/44)+1));
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
				inv = Bukkit.createInventory(p, 54, "所有奖励物品-页数:"+(((index+1)/44)+1));
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
					if(playerData.get(p.getName()).getCurrentNumber()==0)
					{
						p.sendMessage("§6[日常任务] §a你目前没有接受任务，输入/rw get获取任务!");
						return true;
					}
					p.sendMessage("§6[第"+playerData.get(p.getName()).getCurrentNumber()+"环] §a"+quests.get(playerData.get(p.getName()).getWhatTheQuestIs()).getQuestDescribe());
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("help"))
			{
				sender.sendMessage("§6=========[日常任务]=========");
				sender.sendMessage("§6/rw §8- §e查看当前任务");
				sender.sendMessage("§6/rw get §8- §e获取新任务");
				sender.sendMessage("§6/rw quit §8- §e放弃当前任务");
				sender.sendMessage("§6/rw info §8- §e查看今天的任务状态");
				sender.sendMessage("§6/rw help §8- §e查看帮助");
				if(sender.isOp())
				{
					sender.sendMessage("§6/rw clear [玩家名] §8- §e重置该玩家的任务数据");
					sender.sendMessage("§6/rw quest §8- §e显示所有任务物品");
					sender.sendMessage("§6/rw reward §8- §e显示所有奖励物品");
					sender.sendMessage("§6/rw reload §8- §e重载配置");
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("clear"))
			{
				if(sender.isOp())
				{
					if(args.length==2)
					{
						if(!this.playerData.containsKey(args[1]))
						{
							sender.sendMessage("§6[日常任务] §c没有该玩家的任务数据，无法重置");
							return true;
						}
						if(Bukkit.getServer().getPlayer(args[1])!=null)
						{
							PlayerData player = playerData.get(args[1]);
							player.setCurrentNumber(0);
							player.setWhatTheQuestIs(0);
							player.setTotalQuest(0);
							playerData.put(args[1], player);
							Bukkit.getServer().getPlayer(args[1]).sendMessage("§6[日常任务] §3你的任务数据已被重置");
							sender.sendMessage("§6[日常任务] §e已重置玩家§d"+args[1]+"§e的任务数据");
						}
						else
						{
							sender.sendMessage("§6[日常任务] §c该玩家不存在或不在线");
						}
					}
					else
					{
						sender.sendMessage("§6/rw clear [玩家名] §8- §e重置该玩家的任务数据");
					}
				}
				else
				{
					sender.sendMessage("§6[日常任务] §c你没有权限!");
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
					sender.sendMessage("§6[日常任务] §c你没有权限!");
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
					sender.sendMessage("§6[日常任务] §c你没有权限!");
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
					sender.sendMessage("§6[日常任务] §3配置重载成功!");
				}
				else
				{
					sender.sendMessage("§6[日常任务] §c你没有权限!");
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("info"))
			{
				if(sender instanceof Player)
				{
					Player p = (Player)sender;
					if(playerData.containsKey(p.getName()))
					{
						int questLimit = defaultQuantity;
						for(String permission:group.keySet())
						{
							if(p.hasPermission("dailyQuest.limit."+permission))
							{
								questLimit = group.get(permission);
							}
						}
						String msg = "§e你的任务上限:§d"+questLimit+"§e,你已完成:§d"+playerData.get(p.getName()).getTotalQuest()+"§e,";
						if(playerData.get(p.getName()).getCurrentNumber()==0)
							msg += "你目前没有接受任务";
						else
							msg += "当前为第§d"+playerData.get(p.getName()).getCurrentNumber()+"§e环任务";
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
					if(playerData.get(p.getName()).getCurrentNumber()!=0)
					{
						p.sendMessage("§6[日常任务] §a你目前正在做任务，请先取消任务再重新领取任务!");
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
					if(playerData.get(p.getName()).getTotalQuest()>=questLimit)
					{
						p.sendMessage("§6[日常任务] §a你今天的任务已达上限，请明天再来!");
						return true;
					}
					
					// Get the state of this player
					// the first index means the current index of the quest
					// the second index means what the quest is
					// the last index means how many quests totally this player has already finished
					PlayerData player = playerData.get(p.getName());
					player.setCurrentNumber(1);
					player.setWhatTheQuestIs(index);
					playerData.put(p.getName(), player);
					p.sendMessage("§6[第 1 环] §a"+quests.get(index).getQuestDescribe());
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("quit"))
			{
				if(sender instanceof Player)
				{
					Player p = (Player)sender;
					if(playerData.get(p.getName()).getCurrentNumber()!=0)
					{
						if(cancelQuestPlayer.contains(p.getName()))
						{
							PlayerData player = playerData.get(p.getName());
							player.setCurrentNumber(0);
							player.setWhatTheQuestIs(0);

							playerData.put(p.getName(), player);
							cancelQuestPlayer.remove(p.getName());
							getServer().getScheduler().cancelTask(cancelTask.get(p.getName()));
							p.sendMessage("§6[日常任务] §c已放弃当前的任务!");
							return true;
						}
						else
						{
							p.sendMessage("§6[日常任务] §d你确定要放弃该任务吗?请在10秒内再次输入一遍§c/rw quit§d来确认");
							cancelQuestPlayer.add(p.getName());
							cancelTask.put(p.getName(), getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
							{
								public void run()
								{
									if(cancelQuestPlayer.contains(p.getName()))
									{
										p.sendMessage("§6[日常任务] §7取消放弃任务");
										cancelQuestPlayer.remove(p.getName());
									}
									getServer().getScheduler().cancelTask(cancelTask.get(p.getName()));
								}
							} ,10*20,20));
							return true;
						}
					}

					p.sendMessage("§6[日常任务] §a你目前没有任何任务!");
				}
				return true;
			}

		}
		return false;
		
	}
	
	
}

