package personalQuest;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PersonalQuest extends JavaPlugin
{
	/** 
	 * Comments for vipItemQuest and itemQuests
	 * The first index of the ArrayList which is in the HashMap is about the TYPE of the quest
	 * The second index of the ArrayList is about the NAME of the quest
	 * The third index of the ArrayList is about the DESCRIPTION of the quest(every line separates with %)
	 * The fourth index is about the REWARD (Money)
	**/
	HashMap<String, HashMap<String, ArrayList<String>>> vipItemQuests = new HashMap<String, HashMap<String, ArrayList<String>>>();
	HashMap<String, HashMap<String, ArrayList<String>>> itemQuests = new HashMap<String, HashMap<String, ArrayList<String>>>();
	
	/** 
	 * Comments for vipHeadQuest and headQuests
	 * The first index of the ArrayList which is in the HashMap is about the TYPE of the quest
	 * The second index of the ArrayList is about the NAME of the quest
	 * The third index of the ArrayList is about the DESCRIPTION of the quest(every line separates with %)
	 * The fourth index is the NAME that you want to kill
	 * The fifth index is about the REWARD (Money)
	**/
	HashMap<String, HashMap<String, ArrayList<String>>> vipHeadQuests = new HashMap<String, HashMap<String, ArrayList<String>>>();
	HashMap<String, HashMap<String, ArrayList<String>>> headQuests = new HashMap<String, HashMap<String, ArrayList<String>>>();
	/**
	 *  This list is used to store the data which is about who takes quests
	 *  The second ArrayList limits every player's maximum quests, which is 2.
	 *  In every ArrayList in the first ArrayList, the poster name is in the last index
	 */
	HashMap<String, ArrayList<ArrayList<String>>> playerQuest = new HashMap<String, ArrayList<ArrayList<String>>>();
	HashMap<String, ArrayList<ArrayList<String>>> posterQuest = new HashMap<String, ArrayList<ArrayList<String>>>();
	 
	
	HashMap<String, String> message = new HashMap<String, String>();
	
	ArrayList<String> playerPostQuest1 = new ArrayList<String>();
	ArrayList<String> playerPostQuest2 = new ArrayList<String>();

	public Economy economy;
	
	private PersonalQuestInitGui gui = new PersonalQuestInitGui(this);
	
	private boolean setupEconomy()
	{
		RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServicesManager().getRegistration(Economy.class);
		if (economyProvider != null)
		{
			economy = economyProvider.getProvider();
		}
		return (economy!=null);
	}
	
	public void onEnable() 
	{
		if(!new File(getDataFolder(),"Data").exists()) 
		{
			new File(getDataFolder(),"Data").mkdirs();
		}
		if(setupEconomy()==false)
			Bukkit.getConsoleSender().sendMessage("§a[PersonalQuest] §e经济插件没有找到");
		//test();
		loadConfig();
		loadQuestsConfig();
		loadPlayerQuest();
		getServer().getPluginManager().registerEvents(new PersonalQuestListener(this, gui), this);
		getServer().getPluginManager().registerEvents(new PersonalQuestGetQuestListener(this), this);
		getServer().getPluginManager().registerEvents(new PersonalQuestMyQuestListener(this, gui), this);
		Bukkit.getConsoleSender().sendMessage("§a[PersonalQuest] §e任务系统加载完毕");
	}

	public void onDisable() 
	{
		saveQuestConfig();
		savePlayerQuest();
		Bukkit.getConsoleSender().sendMessage("§a[PersonalQuest] §e任务系统卸载完毕");
	}
	
	public void saveQuestConfig()
	{
		File file=new File(getDataFolder(),"/Data/quests.yml");
		if(file.exists())
			file.delete();
		file=new File(getDataFolder(),"/Data/quests.yml");
		FileConfiguration config;
		config = load(file);
		if(!itemQuests.isEmpty())
		{
			for(String playerName:itemQuests.keySet())
			{
				ArrayList<String> everyQuest = itemQuests.get(playerName).get("quest");
				
				if(!everyQuest.isEmpty())
				{
					config.set(playerName+".ItemQuest.QuestType", everyQuest.get(0));
					config.set(playerName+".ItemQuest.QuestName", everyQuest.get(1));
					config.set(playerName+".ItemQuest.QuestContent", everyQuest.get(2));
					
					ArrayList<String> players = itemQuests.get(playerName).get("player");
					config.set(playerName+".ItemQuest.Players", players);
				}
			}
		}
		
		if(!headQuests.isEmpty())
		{
			for(String playerName:headQuests.keySet())
			{
				ArrayList<String> everyQuest = headQuests.get(playerName).get("quest");
				
				if(!everyQuest.isEmpty())
				{
					config.set(playerName+".HeadQuest.QuestType", everyQuest.get(0));
					config.set(playerName+".HeadQuest.QuestName", everyQuest.get(1));
					config.set(playerName+".HeadQuest.QuestContent", everyQuest.get(2));
					
					ArrayList<String> players = headQuests.get(playerName).get("player");
					config.set(playerName+".HeadQuest.Players", players);
				}
			}
		}
		
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//========================================================================

		file=new File(getDataFolder(),"/Data/vipquests.yml");
		if(file.exists())
			file.delete();
		file=new File(getDataFolder(),"/Data/vipquests.yml");
		config = load(file);
		if(!vipItemQuests.isEmpty())
		{

			for(String playerName:vipItemQuests.keySet())
			{
				ArrayList<String> everyQuest = vipItemQuests.get(playerName).get("quest");
				if(!everyQuest.isEmpty())
				{

					config.set(playerName+".ItemQuest.QuestType", everyQuest.get(0));
					config.set(playerName+".ItemQuest.QuestName", everyQuest.get(1));
					config.set(playerName+".ItemQuest.QuestContent", everyQuest.get(2));
					
					ArrayList<String> players = vipItemQuests.get(playerName).get("player");
					config.set(playerName+".ItemQuest.Players", players);

				}
			}
		}
		if(!vipHeadQuests.isEmpty())
		{

			for(String playerName:vipHeadQuests.keySet())
			{
				ArrayList<String> everyQuest = vipHeadQuests.get(playerName).get("quest");
				if(!everyQuest.isEmpty())
				{

					config.set(playerName+".HeadQuest.QuestType", everyQuest.get(0));
					config.set(playerName+".HeadQuest.QuestName", everyQuest.get(1));
					config.set(playerName+".HeadQuest.QuestContent", everyQuest.get(2));
					
					ArrayList<String> players = vipHeadQuests.get(playerName).get("player");
					config.set(playerName+".HeadQuest.Players", players);

				}
			}
		}
		
		
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void test()
	{
		File file=new File(getDataFolder(),"/Data/quests.yml");
		FileConfiguration config;
		config = load(file);
		
		for(int i=0; i<10; i++)
		{
			config.set("BiShop"+i+".ItemQuest.QuestType", "item");
			config.set("BiShop"+i+".ItemQuest.QuestName", "为了测试");
			config.set("BiShop"+i+".ItemQuest.QuestContent", "啊啊啊啊");
		}
		for(int i=10; i<30; i++)
		{
			config.set("BiShop"+i+".ItemQuest.QuestType", "item");
			config.set("BiShop"+i+".ItemQuest.QuestName", "测试啊");
			config.set("BiShop"+i+".ItemQuest.QuestContent", "真的是测试");
		}
		for(int i=30; i<50; i++)
		{
			config.set("BiShop"+i+".ItemQuest.QuestType", "item");
			config.set("BiShop"+i+".ItemQuest.QuestName", "测试啊啊啊啊");
			config.set("BiShop"+i+".ItemQuest.QuestContent", "真啊的啊是测试");
		}
		
		/*
		String a = null;
		for(int i=0; i<50; i++)
		{
			a+=",BiShop"+String.valueOf(i);
		}
		config.set("a", a);
		*/
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void initList()
	{
		vipItemQuests.clear();
		vipHeadQuests.clear();
		itemQuests.clear();
		headQuests.clear();
	}
	
	public void loadConfig()
	{
		File file=new File(getDataFolder(),"config.yml");
		FileConfiguration config;
		if (!file.exists())
		{
			config = load(file);
			
			config.set("MainGui.Name", "§5任务系统");
			config.set("MainGui.QuestButton.Name", "§e点击打开任务平台");
			config.set("MainGui.JobButton.Name", "§e点击打开招聘平台");
			
			config.set("AddGui.Name","§5发布任务");
			config.set("AddGui.ItemQuestButton.Name","§a点击发布物品需求任务");
			config.set("AddGui.ItemQuestButton.Lore","§c需要支付500金币%§e请点击按钮后打开聊天框根据提示输入");
			config.set("AddGui.HeadQuestButton.Name","§a点击发布头颅悬赏任务");
			config.set("AddGui.HeadQuestButton.Lore","§c需要支付1000金币%§e请点击按钮后打开聊天框根据提示输入");
			config.set("AddGui.ItemQuestButtonVIP.Name","§a点击发布VIP物品需求任务");
			config.set("AddGui.ItemQuestButtonVIP.Lore","§c需要支付2000金币%§e请点击按钮后打开聊天框根据提示输入");
			config.set("AddGui.HeadQuestButtonVIP.Name","§a点击发布VIP头颅悬赏任务");
			config.set("AddGui.HeadQuestButtonVIP.Lore","§c需要支付3000金币%§e请点击按钮后打开聊天框根据提示输入");
			config.set("AddGui.CustomButton.Name","§a点击发布自定义任务");
			config.set("AddGui.CustomQuestButton.Lore","§c需要支付1000金币%§e请点击按钮后打开聊天框根据提示输入");
			config.set("AddGui.CustomButtonVIP.Name","§a点击发布VIP自定义任务");
			config.set("AddGui.CustomQuestButtonVIP.Lore","§c需要支付2000金币%§e请点击按钮后打开聊天框根据提示输入");
			
			config.set("QuestGui.Name","§5任务平台");
			config.set("QuestGui.PreviousPageButton.Name","§e点击打开上一页");
			config.set("QuestGui.NextPageButton.Name","§e点击打开下一页");
			config.set("QuestGui.PostButton.Name","§e点击发布我的任务");
			config.set("QuestGui.ItemQuestTag","物品需求");
			config.set("QuestGui.HeadQuestTag","头颅悬赏");
			
			config.set("MyQuestGui.Name","§5我的任务");
			
			try {
				config.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			loadConfig();
		}
		config = load(file);
		
		message.put("MainGui.Name", config.getString("MainGui.Name"));
		message.put("MainGui.QuestButton.Name", config.getString("MainGui.QuestButton.Name"));
		message.put("MainGui.JobButton.Name", config.getString("MainGui.JobButton.Name"));
		
		message.put("AddGui.Name", config.getString("AddGui.Name"));
		message.put("AddGui.ItemQuestButton.Name", config.getString("AddGui.ItemQuestButton.Name"));
		message.put("AddGui.ItemQuestButton.Lore", config.getString("AddGui.ItemQuestButton.Lore"));
		message.put("AddGui.HeadQuestButton.Name", config.getString("AddGui.HeadQuestButton.Name"));
		message.put("AddGui.HeadQuestButton.Lore", config.getString("AddGui.HeadQuestButton.Lore"));
		message.put("AddGui.ItemQuestButtonVIP.Name", config.getString("AddGui.ItemQuestButtonVIP.Name"));
		message.put("AddGui.ItemQuestButtonVIP.Lore", config.getString("AddGui.ItemQuestButtonVIP.Lore"));
		message.put("AddGui.HeadQuestButtonVIP.Name", config.getString("AddGui.HeadQuestButtonVIP.Name"));
		message.put("AddGui.HeadQuestButtonVIP.Lore", config.getString("AddGui.HeadQuestButtonVIP.Lore"));
		message.put("AddGui.CustomButton.Name", config.getString("AddGui.CustomButton.Name"));
		message.put("AddGui.CustomQuestButton.Lore", config.getString("AddGui.CustomQuestButton.Lore"));
		message.put("AddGui.CustomButtonVIP.Name", config.getString("AddGui.CustomButtonVIP.Name"));
		message.put("AddGui.CustomQuestButtonVIP.Lore", config.getString("AddGui.CustomQuestButtonVIP.Lore"));
		
		message.put("QuestGui.Name", config.getString("QuestGui.Name"));
		message.put("QuestGui.PreviousPageButton.Name", config.getString("QuestGui.PreviousPageButton.Name"));
		message.put("QuestGui.NextPageButton.Name", config.getString("QuestGui.NextPageButton.Name"));
		message.put("QuestGui.PostButton.Name", config.getString("QuestGui.PostButton.Name"));
		message.put("QuestGui.ItemQuestTag", config.getString("QuestGui.ItemQuestTag"));
		message.put("QuestGui.HeadQuestTag", config.getString("QuestGui.HeadQuestTag"));
		
		message.put("MyQuestGui.Name", config.getString("MyQuestGui.Name"));
	}
	
	public void loadPlayerQuest()
	{
		File file=new File(getDataFolder(),"/Data/player.yml");
		FileConfiguration config;

		config = load(file);

		for(OfflinePlayer p:Bukkit.getOfflinePlayers())
		{
			ArrayList<ArrayList<String>> quests = new ArrayList<ArrayList<String>>();
			for(int number=1; config.contains(p.getName()+"."+number); number++)
			{
				ArrayList<String> everyQuest = new ArrayList<String>();
				everyQuest.add(config.getString(p.getName()+"."+number+".QuestType"));
				everyQuest.add(config.getString(p.getName()+"."+number+".QuestName"));
				everyQuest.add(config.getString(p.getName()+"."+number+".QuestContent"));
				everyQuest.add(config.getString(p.getName()+"."+number+".QuestPoster"));
				quests.add(everyQuest);
			}
			playerQuest.put(p.getName(), quests);
		}

	}
	
	public void savePlayerQuest()
	{
		File file=new File(getDataFolder(),"/Data/player.yml");
		if(file.exists())
			file.delete();
		FileConfiguration config;

		config = load(file);
		for(String playerName:playerQuest.keySet())
		{
			int number = 1;
			for(ArrayList<String> everyQuest:playerQuest.get(playerName))
			{
				config.set(playerName+"."+number+".QuestType", everyQuest.get(0));
				config.set(playerName+"."+number+".QuestName", everyQuest.get(1));
				config.set(playerName+"."+number+".QuestContent", everyQuest.get(2));
				config.set(playerName+"."+number+".QuestPoster", everyQuest.get(everyQuest.size()-1));
				number += 1;
			}
		}
		
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void loadQuestsConfig()
	{
		File file=new File(getDataFolder(),"/Data/quests.yml");
		FileConfiguration config;
		if (!file.exists())
		{
			config = load(file);
			
			try 
			{
				config.save(file);
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			loadQuestsConfig();
			return;
		}
		config = load(file);
		/*
		String[] a = {"BiShop0","BiShop1","BiShop2","BiShop3","BiShop4","BiShop5","BiShop6","BiShop7","BiShop8","BiShop9","BiShop10","BiShop11","BiShop12","BiShop13","BiShop14","BiShop15","BiShop16","BiShop17","BiShop18","BiShop19","BiShop20","BiShop21","BiShop22","BiShop23","BiShop24","BiShop25","BiShop26","BiShop27","BiShop28","BiShop29","BiShop30","BiShop31","BiShop32","BiShop33","BiShop34","BiShop35","BiShop36","BiShop37","BiShop38","BiShop39","BiShop40","BiShop41","BiShop42","BiShop43","BiShop44","BiShop45","BiShop46","BiShop47","BiShop48","BiShop49"};
		for(String i:a)
		{
			ArrayList<String> itemQuest = new ArrayList<String>();
			String type = config.getString(i+".ItemQuest.QuestType");
			String name = config.getString(i+".ItemQuest.QuestName");
			String content = config.getString(i+".ItemQuest.QuestContent");
			itemQuest.add(type);
			itemQuest.add(name);
			itemQuest.add(content);
			
			itemQuests.put(i, itemQuest);
		}
		*/
		for(OfflinePlayer p:Bukkit.getOfflinePlayers())
		{
			HashMap<String, ArrayList<String>> questInfo = new HashMap<String, ArrayList<String>>();
			if(config.contains(p.getName()+".ItemQuest"))
			{
				ArrayList<String> itemQuest = new ArrayList<String>();
				itemQuest.add(config.getString(p.getName()+".ItemQuest.QuestType"));
				itemQuest.add(config.getString(p.getName()+".ItemQuest.QuestName"));
				itemQuest.add(config.getString(p.getName()+".ItemQuest.QuestContent"));
				questInfo.put("quest", itemQuest);
				
				ArrayList<String> players = (ArrayList<String>) config.getStringList(p.getName()+".ItemQuest.Players");
				questInfo.put("player", players);
				itemQuests.put(p.getName(), questInfo);
			}
			if(config.contains(p.getName()+".HeadQuest"))
			{
				ArrayList<String> headQuest = new ArrayList<String>();
				headQuest.add(config.getString(p.getName()+".HeadQuest.QuestType"));
				headQuest.add(config.getString(p.getName()+".HeadQuest.QuestName"));
				headQuest.add(config.getString(p.getName()+".HeadQuest.QuestContent"));
				questInfo.put("quest", headQuest);
				
				ArrayList<String> players = (ArrayList<String>) config.getStringList(p.getName()+".HeadQuest.Players");
				questInfo.put("player", players);
				headQuests.put(p.getName(), questInfo);
			}
		}
		
		//========================================================================
		
		file=new File(getDataFolder(),"/Data/vipquests.yml");
		config = load(file);
		
		for(OfflinePlayer p:Bukkit.getOfflinePlayers())
		{
			HashMap<String, ArrayList<String>> questInfo = new HashMap<String, ArrayList<String>>();
			if(config.contains(p.getName()+".ItemQuest"))
			{
				ArrayList<String> itemQuest = new ArrayList<String>();
				itemQuest.add(config.getString(p.getName()+".ItemQuest.QuestType"));
				itemQuest.add(config.getString(p.getName()+".ItemQuest.QuestName"));
				itemQuest.add(config.getString(p.getName()+".ItemQuest.QuestContent"));
				questInfo.put("quest", itemQuest);
				
				ArrayList<String> players = (ArrayList<String>) config.getStringList(p.getName()+".ItemQuest.Players");
				questInfo.put("player", players);
				vipItemQuests.put(p.getName(), questInfo);
			}
			if(config.contains(p.getName()+".HeadQuest"))
			{
				ArrayList<String> headQuest = new ArrayList<String>();
				headQuest.add(config.getString(p.getName()+".HeadQuest.QuestType"));
				headQuest.add(config.getString(p.getName()+".HeadQuest.QuestName"));
				headQuest.add(config.getString(p.getName()+".HeadQuest.QuestContent"));
				questInfo.put("quest", headQuest);
				
				ArrayList<String> players = (ArrayList<String>) config.getStringList(p.getName()+".HeadQuest.Players");
				questInfo.put("player", players);
				vipHeadQuests.put(p.getName(), questInfo);
			}
		}
		
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
	
	/*
	public Inventory initMain(Player p)
	{		
				
		Inventory inv = Bukkit.createInventory(p, 9, message.get("MainGui.Name"));
		ItemStack quest = createItem(Material.BOOK, message.get("MainGui.QuestButton.Name"));
		
		ItemStack job = createItem(Material.PAPER, message.get("MainGui.JobButton.Name"));

		inv.setItem(2, quest);
		inv.setItem(4, job);

		return inv;
	}
	*/
	
	public ItemStack createItem(Material material, String displayName)
	{
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(displayName);
		item.setItemMeta(meta);
		
		return item;
	}
	
	public ItemStack createItem(Material material, String displayName, String lore)
	{
		ItemStack item = new ItemStack(material);
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
	
	public boolean isSame(ArrayList<String> list1, ArrayList<String> list2)
	{
		if(list1.size()!=list2.size())
			return false;
		for(int i=0; i<list1.size(); i++)
		{
			if(!list1.get(i).equals(list2.get(i)))
			{
				return false;
			}
		}
		return true;
	}
	
	/*
	public Inventory initAdd(Player p)
	{
				
		Inventory inv = Bukkit.createInventory(p, 18, message.get("AddGui.Name"));
		
		ItemStack quest = createItem(Material.BOOK, message.get("AddGui.ItemQuestButton.Name"), message.get("AddGui.ItemQuestButton.Lore"));

		ItemStack head = createItem(397, 1, (short)3, message.get("AddGui.HeadQuestButton.Name"), message.get("AddGui.HeadQuestButton.Lore"));

		ItemStack questVIP = createItem(Material.NETHER_STAR, message.get("AddGui.ItemQuestButtonVIP.Name"), message.get("AddGui.ItemQuestButtonVIP.Lore"));
		
		ItemStack headVIP = createItem(397, 1, 0, message.get("AddGui.HeadQuestButtonVIP.Name"), message.get("AddGui.HeadQuestButtonVIP.Lore"));
		
		ItemStack custom = createItem(Material.PAPER, message.get("AddGui.CustomButton.Name"), message.get("AddGui.CustomQuestButton.Lore"));

		ItemStack customVIP = createItem(Material.MAP, message.get("AddGui.CustomButtonVIP.Name"), message.get("AddGui.CustomQuestButtonVIP.Lore"));
		
		inv.setItem(2, quest);
		inv.setItem(4, custom);
		inv.setItem(6, head);
		inv.setItem(11, questVIP);
		inv.setItem(13, customVIP);
		inv.setItem(15, headVIP);

		return inv;
	}
	
	public Inventory initMyQuest(Player p)
	{
		Inventory inv = Bukkit.createInventory(p, 45, message.get("MyQuestGui.Name"));

		ItemStack window = createItem(160, 1, 0, " ");

		for(int i=0; i<9; i++)
		{
			inv.setItem(9+i, window);
			inv.setItem(27+i, window);
		}
		
		
		// ===========================================================================
		// The following codes are used to show the quests that I had
		// ===========================================================================
		int indexOfGetQuest = 0;
		if(!playerQuest.get(p.getName()).isEmpty())
		{
			for(ArrayList<String> everyQuest:playerQuest.get(p.getName()))
			{
				String type = everyQuest.get(0);
				String name = everyQuest.get(1);
				String content = everyQuest.get(2);
				String posterName = everyQuest.get(everyQuest.size()-1);
				ItemStack vipItem = null;
				
				String lore = "";
				
				// auto separate a string with 8 characters
				int length = 0;
				if(content.length()%8==0)
					length = content.length()/8;
				else
					length = (content.length()/8)+1;
				int index = 0;
				for(int c=0; c<length ; c++)
				{
					if(c==length-1)
					{
						lore += "§e"+content.substring(index, content.length());
						break;
					}
					lore += "§e"+content.substring(index, index+8)+"%";
					index +=8;
				}
				
				lore += "%§d发布人:"+posterName;
				lore += "%§c右键点击放弃该任务";
				
				if(type.equalsIgnoreCase("VIPitem"))
					vipItem = createItem(Material.NETHER_STAR, "§a["+message.get("QuestGui.ItemQuestTag")+"§a]§f"+name, lore);
				if(type.equalsIgnoreCase("VIPhead"))
					vipItem = createItem(397, 1, 0, "§a["+message.get("QuestGui.HeadQuestTag")+"§a]§f"+name, lore);
				if(type.equalsIgnoreCase("item"))
					vipItem = createItem(340, 1, 0, "§a["+message.get("QuestGui.ItemQuestTag")+"§a]§f"+name, lore);
				if(type.equalsIgnoreCase("head"))
					vipItem = createItem(397, 1, 3, "§a["+message.get("QuestGui.HeadQuestTag")+"§a]§f"+name, lore);
				inv.setItem(indexOfGetQuest, vipItem);
				indexOfGetQuest += 1;
			}
		}
		
		// ===========================================================================
		// The following codes are used to show the quests that I posted
		// ===========================================================================
		
		ArrayList<ArrayList<String>> totalQuest = new ArrayList<ArrayList<String>>();
		
		
		for(int i=0; i<19; i+=18)
		{
			totalQuest.clear();
			if(i==18)
			{
				if(headQuests.containsKey(p.getName()))
					totalQuest.add(headQuests.get(p.getName()).get("quest"));
				if(itemQuests.containsKey(p.getName()))
					totalQuest.add(itemQuests.get(p.getName()).get("quest"));
			}
			else if(i==0)
			{
				if(vipHeadQuests.containsKey(p.getName()))
					totalQuest.add(vipHeadQuests.get(p.getName()).get("quest"));
				if(vipItemQuests.containsKey(p.getName()))
					totalQuest.add(vipItemQuests.get(p.getName()).get("quest"));
			}

			int indexOfPostQuest = 18+i;
			if(!totalQuest.isEmpty())
			{
				for(ArrayList<String> everyQuest:totalQuest)
				{

					String type = everyQuest.get(0);
					String name = everyQuest.get(1);
					String content = everyQuest.get(2);
					String posterName = p.getName();
					ItemStack vipItem = null;
					
					String lore = "";
					
					// auto separate a string with 8 characters
					int length = 0;
					if(content.length()%8==0)
						length = content.length()/8;
					else
						length = (content.length()/8)+1;
					int index = 0;
					for(int c=0; c<length ; c++)
					{
						if(c==length-1)
						{
							lore += "§e"+content.substring(index, content.length());
							break;
						}
						lore += "§e"+content.substring(index, index+8)+"%";
						index +=8;
					}
					lore += "%§d发布人:"+posterName;
					lore += "%§b点击查看正在执行该任务的玩家";
					lore += "%§c右键点击放弃该任务";
					
					if(type.equalsIgnoreCase("VIPitem"))
						vipItem = createItem(Material.NETHER_STAR, "§a["+message.get("QuestGui.ItemQuestTag")+"§a]§f"+name, lore);
					if(type.equalsIgnoreCase("VIPhead"))
						vipItem = createItem(397, 1, 0, "§a["+message.get("QuestGui.HeadQuestTag")+"§a]§f"+name, lore);
					if(type.equalsIgnoreCase("item"))
						vipItem = createItem(340, 1, 0, "§a["+message.get("QuestGui.ItemQuestTag")+"§a]§f"+name, lore);
					if(type.equalsIgnoreCase("head"))
						vipItem = createItem(397, 1, 3, "§a["+message.get("QuestGui.HeadQuestTag")+"§a]§f"+name, lore);
					inv.setItem(indexOfPostQuest, vipItem);
					indexOfPostQuest += 1;
				}
			}

		}
		

		return inv;
	}
	
	public ArrayList<Inventory> initQuestGUI(Player p)
	{
		ArrayList<Inventory> inventory = new ArrayList<Inventory>();
		ArrayList<ArrayList<String>> questList = new ArrayList<ArrayList<String>>();
		int size = headQuests.size() + itemQuests.size();
		int totalSize = headQuests.size() + itemQuests.size() + vipHeadQuests.size() + vipItemQuests.size();

		if(totalSize==0)
		{
			Inventory inv = Bukkit.createInventory(p, 54, message.get("QuestGui.Name"));
			ItemStack next = createItem(351,1,(short)13, message.get("QuestGui.NextPageButton.Name"));
			
			ItemStack previous = createItem(351,1,(short)8, message.get("QuestGui.PreviousPageButton.Name"));
			
			ItemStack add = createItem(386, 1, 0, message.get("QuestGui.PostButton.Name"));
			
			ItemStack page = createItem(339, 1, 0, "§e第1页");
			
			inv.setItem(49, add);
			inv.setItem(53, next);
			inv.setItem(45, previous);
			inv.setItem(47, page);
			
			ArrayList<Inventory> invList = new ArrayList<Inventory>();
			invList.add(inv);
			return invList;
		}


		// put all quests into a total list, the last index of the everyQuest is the player's name
		for(String player:itemQuests.keySet())
		{
			if(!itemQuests.get(player).isEmpty())
			{
				ArrayList<String> result = itemQuests.get(player).get("quest");
				result.add(player);
				questList.add(result);
			}
		}

		for(String player:headQuests.keySet())
		{
			if(!itemQuests.get(player).isEmpty())
			{
				ArrayList<String> result = headQuests.get(player).get("quest");
				result.add(player);
				questList.add(result);
			}
		}

		int indexOfNormalQuest = 0;
		for(int j=0; j<totalSize/45+1; j++)
		{

			Inventory inv = Bukkit.createInventory(p, 54, message.get("QuestGui.Name"));
			ItemStack previous = createItem(351,1,(short)13, message.get("QuestGui.NextPageButton.Name"));
			
			ItemStack next = createItem(351,1,(short)8, message.get("QuestGui.PreviousPageButton.Name"));
			
			ItemStack add = createItem(386, 1, 0, message.get("QuestGui.PostButton.Name"));
			
			ItemStack page = createItem(339, 1, 0, "§e第"+(j+1)+"页");
			
			inv.setItem(49, add);
			inv.setItem(53, previous);
			inv.setItem(45, next);
			inv.setItem(47, page);

			int indexOfInv = 0;

			// get the VIP quests, show them between index 0 to 8
			
			ArrayList<ArrayList<String>> vipQuestList = new ArrayList<ArrayList<String>>();

			for(String player:vipItemQuests.keySet())
			{

				if(!vipItemQuests.get(player).isEmpty())
				{
					ArrayList<String> result = vipItemQuests.get(player).get("quest");
					result.add(player);
					vipQuestList.add(result);
				}
			}

			for(String player:vipHeadQuests.keySet())
			{
				if(!vipItemQuests.get(player).isEmpty())
				{
					ArrayList<String> result = vipHeadQuests.get(player).get("quest");
					result.add(player);
					vipQuestList.add(result);
				}
			}

			for(ArrayList<String> vip:vipQuestList)
			{
				ItemStack item = null;
				ItemMeta metaItem = null;

				String type = vip.get(0);
				String name = vip.get(1);
				String content = vip.get(2);

				ArrayList<String> lore = new ArrayList<String>();
				int length = 0;

				if(type.equalsIgnoreCase("VIPitem"))
				{

					item = new ItemStack(Material.NETHER_STAR);
					metaItem = item.getItemMeta();

					metaItem.setDisplayName("§a["+message.get("QuestGui.ItemQuestTag")+"§a]§f"+name);
				}

				if(type.equalsIgnoreCase("VIPhead"))
				{
					item = new ItemStack(397);
					metaItem = item.getItemMeta();
					metaItem.setDisplayName("§a["+message.get("QuestGui.HeadQuestTag")+"§a]§f"+name);
				}

				if(content.length()%8==0)
					length = content.length()/8;
				else
					length = (content.length()/8)+1;
				int index = 0;
				for(int c=0; c<length ; c++)
				{
					if(c==length-1)
					{
						lore.add("§e"+content.substring(index, content.length()));
						break;
					}
					lore.add("§e"+content.substring(index, index+8));
					index +=8;
				}
	
				lore.add("§d发布人:"+vip.get(vip.size()-1));
				boolean alreadyHad = false;
				for(ArrayList<String> my:playerQuest.get(p.getName()))
				{
					if(isSame(vip, my))
						alreadyHad = true;
				}
				if(!alreadyHad)
					lore.add("§a点击领取该任务");
				else
					lore.add("§c你已领取该任务");
				metaItem.setLore(lore);
				item.setItemMeta(metaItem);
				inv.setItem(indexOfInv, item);
				indexOfInv += 1;

			}

			if(size>0)
			{
				// get the normal type of quests, show them between index 9 to 45
				indexOfInv = 9;
				for(int x=0; x<36; x++)
				{
					if(size>36)
					{
						if(x==size-j)
							break;
					}
					else
					{
						if(x==size)
							break;
					}

					ArrayList<String> i = questList.get(indexOfNormalQuest);
					
					ItemStack item = null;
					ItemMeta metaItem = null;
					String type = i.get(0);
					String name = i.get(1);
					String content = i.get(2);
					String playerName = i.get(i.size()-1);
					ArrayList<String> lore = new ArrayList<String>();
					int length = 0;
					if(type.equalsIgnoreCase("item"))
					{
						item = new ItemStack(340);
						metaItem = item.getItemMeta();
						metaItem.setDisplayName("§a["+message.get("QuestGui.ItemQuestTag")+"§a]§f"+name);
					}
					else if(type.equalsIgnoreCase("head"))
					{
						item = new ItemStack(397, 1, (short)3);
						metaItem = item.getItemMeta();
						metaItem.setDisplayName("§a["+message.get("QuestGui.HeadQuestTag")+"§a]§f"+name);
					}

					if(content.length()%8==0)
						length = content.length()/8;
					else
						length = (content.length()/8)+1;
					int index = 0;
					for(int c=0; c<length ; c++)
					{
						if(c==length-1)
						{
							lore.add("§e"+content.substring(index, content.length()));
							break;
						}
						lore.add("§e"+content.substring(index, index+8));
						index +=8;
					}

					lore.add("§d发布人:"+playerName);
					boolean alreadyHad = false;
					for(ArrayList<String> my:playerQuest.get(p.getName()))
					{
						if(isSame(i, my))
							alreadyHad = true;
					}
					if(!alreadyHad)
						lore.add("§a点击领取该任务");
					else
						lore.add("§c你已领取该任务");
					metaItem.setLore(lore);
					item.setItemMeta(metaItem);
					inv.setItem(indexOfInv, item);
					indexOfNormalQuest+=1;
					indexOfInv+=1;
				}
			}

			inventory.add(inv);
		}

		return inventory;
	}
	*/
	
	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args)  
	{
		if (cmd.getName().equalsIgnoreCase("pq"))
		{
			if(args.length==0)
			{
				sender.sendMessage("§a=========[任务系统]=========");
				sender.sendMessage("§a/pq gui §3打开主菜单");
				sender.sendMessage("§a/pq my §3查看我的任务");
				if(sender.isOp())
				{
					sender.sendMessage("§a/pq reload §3重载配置");
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("my"))
			{
				if(sender instanceof Player)
				{
					Player p = (Player)sender;
					p.openInventory(gui.initMyQuest(p));
				}
				
				return true;
			}
			
			if(args[0].equalsIgnoreCase("reload"))
			{
				if(sender.isOp())
				{
					loadPlayerQuest();
					loadConfig();
					loadQuestsConfig();
					sender.sendMessage("§a[任务系统] §c重载配置成功!");
				}
				else
				{
					sender.sendMessage("§a[任务系统] §c你没有权限");
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("gui"))
			{
				if(sender instanceof Player)
				{
					Player p = (Player)sender;
					p.openInventory(gui.initMain(p));
					return true;
				}
			}
			return true;
		}
		return false;
	}
}

