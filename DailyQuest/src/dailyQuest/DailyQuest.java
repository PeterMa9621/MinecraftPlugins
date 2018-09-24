package dailyQuest;

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
import org.bukkit.inventory.ItemFlag;
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
	ArrayList<ItemStack> rewardItem = new ArrayList<ItemStack>();
	HashMap<String, Integer> group = new HashMap<String, Integer>();
	
	HashMap<String, MobQuest> mobQuest = new HashMap<String, MobQuest>();
	
	SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
	
	int defaultQuantity = 0;
	
	int randomItemMaxQuantity = 1;
	
	int additionMoney = 10;
	
	int extraRewarItemQuantity = 1;
	
	int getQuestNPCId = 0;
	
	boolean enableCommandGetQuest = false;
	
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
			Bukkit.getConsoleSender().sendMessage("��a[DailyQuest] ��cVault���δ����!");
		loadConfig();
		loadItemConfig();
		loadQuestConfig();
		loadPlayerConfig();
		task();
		getServer().getPluginManager().registerEvents(new MobQuestListener(this), this);
		getServer().getPluginManager().registerEvents(new FinishQuestListener(this), this);
		getServer().getPluginManager().registerEvents(new GetQuestListener(this), this);
		CitizensAPI.registerEvents(new DailyQuestListener(this));
		Bukkit.getConsoleSender().sendMessage("��a[DailyQuest] ��e�ճ�����ϵͳ�������");
	}

	public void onDisable() 
	{
		savePlayerConfig();
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
						PlayerData pd = playerData.get(p.getName());
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
	
	public void loadItemConfig()
	{
		File file=new File(getDataFolder(),"item.yml");
		FileConfiguration config;
		if (!file.exists())
		{
			config = load(file);
			config.set("Item.1.ID", 1);
			config.set("Item.1.Data", 0);
			config.set("Item.1.Amount", 1);

			config.set("Item.2.ID", 263);
			config.set("Item.2.Data", 1);
			config.set("Item.2.Amount", 1);
			config.set("Item.2.Name", "��fδ�����ı�ʯ");
			config.set("Item.2.Lore", "��e[δ����]%��6һ�鿴������ͨ��ʯͷ");
			config.set("Item.2.Enchantment.ID", -1);
			config.set("Item.2.Enchantment.Level", 0);
			config.set("Item.2.HideEnchant", true);
			
			try {
				config.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			loadItemConfig();
		}
		config = load(file);
		
		for(int i=0; config.contains("Item."+(i+1)); i++)
		{
			ItemStack item = null;
			boolean hide = config.getBoolean("Item."+(i+1)+".HideEnchant");
			int id = config.getInt("Item."+(i+1)+".ID");
			int data = config.getInt("Item."+(i+1)+".Data");
			int amount = config.getInt("Item."+(i+1)+".Amount");
			
			String name = config.getString("Item."+(i+1)+".Name");
			String lore = config.getString("Item."+(i+1)+".Lore");
			int enchantID = config.getInt("Item."+(i+1)+".Enchantment.ID");
			int enchantLevel = config.getInt("Item."+(i+1)+".Enchantment.Level");
			
			if(name==null || lore==null)
				item = new ItemStack(id, amount, (short)data);
			else
				item = createItem(id, amount, data, name, lore);
			ItemMeta meta = item.getItemMeta();
			if(hide==true)
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			item.setItemMeta(meta);
			if(enchantID!=-1 && enchantLevel>0)
				item.addUnsafeEnchantment(Enchantment.getById(enchantID), enchantLevel);
			rewardItem.add(item);
		}
	}
	
	public void loadConfig()
	{
		File file=new File(getDataFolder(),"config.yml");
		FileConfiguration config;
		if (!file.exists())
		{
			config = load(file);
			
			String[] group = {"VIP1:40","VIP2:60","VIP3:70","VIP3:100"};
			
			config.set("DefaultQuantity", 30);
			
			config.set("Group", group);
			
			config.set("RandomItemMaxQuantity", 3);
			config.set("SeriesFinish.AdditionMoney", 100);
			config.set("SeriesFinish.ExtraRewardItemQuantity", 1);
			
			config.set("GetQuestNPCId", 9);
			
			config.set("EnableCommandGetQuest", false);
			try {
				config.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			loadConfig();
		}
		
		config = load(file);
		
		defaultQuantity = config.getInt("DefaultQuantity");
		randomItemMaxQuantity = config.getInt("RandomItemMaxQuantity");
		
		additionMoney = config.getInt("SeriesFinish.AdditionMoney");
		
		extraRewarItemQuantity = config.getInt("SeriesFinish.ExtraRewardItemQuantity");
		
		getQuestNPCId = config.getInt("GetQuestNPCId");
		
		enableCommandGetQuest = config.getBoolean("EnableCommandGetQuest");
		
		List<String> group = config.getStringList("Group");
		
		for(String g:group)
		{
			this.group.put(g.split(":")[0], Integer.valueOf(g.split(":")[1]));
		}
	}
	
	public void saveConfig()
	{
		File file=new File(getDataFolder(),"config.yml");
		FileConfiguration config;
		
		config = load(file);
		
		config.set("GetQuestNPCId", getQuestNPCId);
		
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void loadPlayerConfig()
	{
		File file1=new File(getDataFolder(), "/Data");
		String[] fileName = file1.list();
		for(String name:fileName)
		{
			String playerName = name.substring(0, name.length()-4);
			File file=new File(getDataFolder(),"/Data/" + playerName+ ".yml");
			FileConfiguration config;
			
			if(file.exists())
			{
				config = load(file);
				
				int currentQuestNumber = config.getInt("CurrentQuestNumber");
				int currentQuestIndex = config.getInt("CurrentQuestIndex");
				int totalQuestNumber = config.getInt("TotalQuestNumber");
				
				String lastLogout = config.getString("LastLogout");
				PlayerData player = new PlayerData(currentQuestNumber, currentQuestIndex, totalQuestNumber, lastLogout);
				playerData.put(playerName, player);
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
			config.set("Quest.1.Describe", "��a��ǰ������(-344,100,256)�����Ͻ�ʯͷ10��");
			config.set("Quest.1.RewardMessage", "��7�����������");
			config.set("Quest.1.NPCID", 9);
			config.set("Quest.1.Item.ID", 1);
			config.set("Quest.1.Item.Data", 0);
			config.set("Quest.1.Item.Amount", 10);
			config.set("Quest.1.Item.Name", "AAA");
			config.set("Quest.1.Item.Lore", "BBB%CCC");
			config.set("Quest.1.Item.Enchantment.ID", 0);
			config.set("Quest.1.Item.Enchantment.Level", 1);
			config.set("Quest.1.Mob.ID", 54);
			config.set("Quest.1.Mob.Amount", 2);
			config.set("Quest.1.Mob.CustomName", "��e��l������");
			config.set("Quest.1.Reward.Money", 50);
			//config.set("Quest.1.Reward.Item", "Quest1");
			
			for(int i=1; i<10; i++)
			{
				config.set("Quest."+(i+1)+".Type", "item");
				config.set("Quest."+(i+1)+".Describe", "��a��ǰ������(-344,100,256)�����Ͻ�ʯͷ10��");
				config.set("Quest."+(i+1)+".RewardMessage", "��7�����������");
				config.set("Quest."+(i+1)+".NPCID", 9);
				config.set("Quest."+(i+1)+".Item.ID", 1);
				config.set("Quest."+(i+1)+".Item.Data", 0);
				config.set("Quest."+(i+1)+".Item.Amount", 10);
				config.set("Quest."+(i+1)+".Item.Enchantment.ID", -1);
				config.set("Quest."+(i+1)+".Item.Enchantment.Level", 0);
				config.set("Quest."+(i+1)+".Reward.Money", 100);
				//config.set("Quest."+(i+1)+".Reward.Item", "Quest1");
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
			int mobID = config.getInt("Quest."+(i+1)+".Mob.ID");
			int mobAmount = config.getInt("Quest."+(i+1)+".Mob.Amount");
			int money = config.getInt("Quest."+(i+1)+".Reward.Money");
			String mobName = config.getString("Quest."+(i+1)+".Mob.CustomName");
			//String rewardItemList = config.getString("Quest."+(i+1)+".Reward.Item");
			
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
			
			/*ArrayList<ItemStack> itemList = null;
			if(rewardItemList!=null)
			{
				itemList = this.item.get(rewardItemList);
			}
			*/
			
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
			if(mobID!=0 && mobName==null)
				quest = new Quest(type, mobID, mobAmount);
			else if(mobID!=0 && mobName!=null)
				quest = new Quest(type, mobID, mobAmount, mobName);
			else
				quest = new Quest(type, item);
			
			// Save data in QuestInfo class
			if(rewardMessage==null)
				rewardMessage = "null";
			QuestInfo questInfo = new QuestInfo(quest, NPCID, money, describe, rewardMessage);
			quests.add(questInfo);
		}
		return;
		
	}
	
	public int random(int range)
	{
		int i = rand.nextInt(range); //���������
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
        { //�����ļ�������
        	try   //��׽�쳣����Ϊ�п��ܴ������ɹ�
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
				for(ItemStack item:rewardItem)
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
					if(playerData.get(p.getName()).getCurrentNumber()==0)
					{
						p.sendMessage("��6[�ճ�����] ��a��Ŀǰû�н�����������/rw get��ȡ����!");
						return true;
					}
					p.sendMessage("��6[��"+playerData.get(p.getName()).getCurrentNumber()+"��] ��a"+quests.get(playerData.get(p.getName()).getWhatTheQuestIs()).getQuestDescribe());
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
						if(!this.playerData.containsKey(args[1]))
						{
							sender.sendMessage("��6[�ճ�����] ��cû�и���ҵ��������ݣ��޷�����");
							return true;
						}
						if(Bukkit.getServer().getPlayer(args[1])!=null)
						{
							PlayerData player = playerData.get(args[1]);
							player.setCurrentNumber(0);
							player.setWhatTheQuestIs(0);
							player.setTotalQuest(0);
							playerData.put(args[1], player);
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
						String msg = "��e�����������:��d"+questLimit+"��e,�������:��d"+playerData.get(p.getName()).getTotalQuest()+"��e,";
						if(playerData.get(p.getName()).getCurrentNumber()==0)
							msg += "��Ŀǰû�н�������";
						else
							msg += "��ǰΪ�ڡ�d"+playerData.get(p.getName()).getCurrentNumber()+"��e������";
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
					if(playerData.get(p.getName()).getCurrentNumber()!=0)
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
					if(playerData.get(p.getName()).getTotalQuest()>=questLimit)
					{
						p.sendMessage("��6[�ճ�����] ��a�����������Ѵ����ޣ�����������!");
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
					p.sendMessage("��6[�� 1 ��] ��a"+quests.get(index).getQuestDescribe());
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

