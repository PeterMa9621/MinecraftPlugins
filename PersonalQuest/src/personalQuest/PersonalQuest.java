package personalQuest;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
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
	/**
	 *  This hash map is used to store every items in player1's inventory 
	 *  and to show them in player2's inventory 
	 */
	HashMap<String, ItemStack> comfirmGuiItems = new HashMap<String, ItemStack>();
	/**
	 *  The key is the player who wants to finish the quest, the value is the player who posts the quest
	 */
	HashMap<String, String> confirmPlayers = new HashMap<String, String>();
	HashMap<String, String> playersConfirm2 = new HashMap<String, String>();
	HashMap<String, String> playersConfirm1 = new HashMap<String, String>();
	
	HashMap<String, String> message = new HashMap<String, String>();
	HashMap<String, String> config = new HashMap<String, String>();
	
	ArrayList<String> playerPostQuest1 = new ArrayList<String>();
	ArrayList<String> playerPostQuest2 = new ArrayList<String>();
	ArrayList<String> playerPostQuest3 = new ArrayList<String>();
	/**
	 *  This list is used to store the players who need to be killed
	 */
	ArrayList<String> needToBeKilled = new ArrayList<String>();

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
		loadMessageConfig();
		loadQuestsConfig();
		loadPlayerQuest();
		loadWhoNeedToBeKilledConfig();
		getServer().getPluginManager().registerEvents(new PersonalQuestListener(this, gui), this);
		getServer().getPluginManager().registerEvents(new PersonalQuestGetQuestListener(this), this);
		getServer().getPluginManager().registerEvents(new PersonalQuestMyQuestListener(this, gui), this);
		getServer().getPluginManager().registerEvents(new PersonalQuestConfirmListener(this, gui), this);
		getServer().getPluginManager().registerEvents(new PersonalQuestGetHeadListener(this), this);
		
		Bukkit.getConsoleSender().sendMessage("§a[PersonalQuest] §e任务系统加载完毕");
	}

	public void onDisable() 
	{
		saveQuestConfig();
		savePlayerQuest();
		saveWhoNeedToBeKilledConfig();
		Bukkit.getConsoleSender().sendMessage("§a[PersonalQuest] §e任务系统卸载完毕");
	}
	
	public void saveWhoNeedToBeKilledConfig()
	{
		File file=new File(getDataFolder(),"/Data/killer.yml");
		if(file.exists())
			file.delete();
		file=new File(getDataFolder(),"/Data/killer.yml");
		FileConfiguration config;
		config = load(file);
		
		for(int i=0; i<needToBeKilled.size(); i++)
		{
			config.set("NeedToBeKilled."+(i+1), needToBeKilled.get(i));
		}
		
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void loadWhoNeedToBeKilledConfig()
	{
		File file=new File(getDataFolder(),"/Data/killer.yml");
		FileConfiguration config;
		config = load(file);
		
		for(int i=0; config.contains("NeedToBeKilled."+(i+1)); i++)
		{
			needToBeKilled.add(config.getString("NeedToBeKilled."+(i+1)));
		}
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
					config.set(playerName+".ItemQuest.Poster", everyQuest.get(everyQuest.size()-1));
					
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
					config.set(playerName+".HeadQuest.WhoNeedToBeKilled", everyQuest.get(3));
					config.set(playerName+".HeadQuest.Poster", everyQuest.get(everyQuest.size()-1));
					
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
					config.set(playerName+".ItemQuest.Poster", everyQuest.get(everyQuest.size()-1));
					
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
					config.set(playerName+".HeadQuest.WhoNeedToBeKilled", everyQuest.get(3));
					config.set(playerName+".HeadQuest.Poster", everyQuest.get(everyQuest.size()-1));
					
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
	
	/*
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
		
		
		String a = null;
		for(int i=0; i<50; i++)
		{
			a+=",BiShop"+String.valueOf(i);
		}
		config.set("a", a);
		
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	*/	

	public void initList()
	{
		vipItemQuests.clear();
		vipHeadQuests.clear();
		itemQuests.clear();
		headQuests.clear();
	}
	
	public boolean isExist(int number, int[] numberList)
	{
		int left = 0;
		int right = numberList.length-1;
		int half = (right+left)/2;
		while(left<=right)
		{
			if(numberList[half]>number)
			{
				right=half-1;
			}
			else if(numberList[half]<number)
			{
				left=half+1;
			}
			else if(numberList[half]==number)
			{
				return true;
			}
			half = (right+left)/2;
		};

		return false;
	}
	
	public void loadConfig()
	{
		File file=new File(getDataFolder(),"config.yml");
		FileConfiguration config;
		if (!file.exists())
		{
			config = load(file);
			
			config.set("NormalItemQuest.Price", "500");
			config.set("VIPItemQuest.Price", "1000");
			config.set("NormalHeadQuest.Price", "1000");
			config.set("VIPHeadQuest.Price", "2000");
			config.set("CustomQuest.Price", "1500");
			config.set("VIPCustomQuest.Price", "3000");
			
			try {
				config.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		config = load(file);
		
		this.config.put("NormalItemQuest.Price", config.getString("NormalItemQuest.Price"));
		this.config.put("VIPItemQuest.Price", config.getString("VIPItemQuest.Price"));
		this.config.put("NormalHeadQuest.Price", config.getString("NormalHeadQuest.Price"));
		this.config.put("VIPHeadQuest.Price", config.getString("VIPHeadQuest.Price"));
		this.config.put("CustomQuest.Price", config.getString("CustomQuest.Price"));
		this.config.put("VIPCustomQuest.Price", config.getString("VIPCustomQuest.Price"));
		
		
	}
	
	public void loadMessageConfig()
	{
		File file=new File(getDataFolder(),"message.yml");
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
			
			config.set("ConfirmGui.ConfirmButton1","§6点击确认任务已完成");
			config.set("ConfirmGui.ConfirmButton2","§c再次点击确认奖励物品或需求物品");
			
			try {
				config.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			loadMessageConfig();
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
		
		message.put("ConfirmGui.ConfirmButton1", config.getString("ConfirmGui.ConfirmButton1"));
		message.put("ConfirmGui.ConfirmButton2", config.getString("ConfirmGui.ConfirmButton2"));
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
				if(config.getString(p.getName()+"."+number+".QuestType").equalsIgnoreCase("VIPhead") || 
						config.getString(p.getName()+"."+number+".QuestType").equalsIgnoreCase("head"))
				{
					everyQuest.add(config.getString(p.getName()+"."+number+".WhoNeedToBeKilled"));
				}
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
				if(everyQuest.get(0).equalsIgnoreCase("VIPhead") || 
						everyQuest.get(0).equalsIgnoreCase("head"))
				{
					config.set(playerName+"."+number+".WhoNeedToBeKilled", everyQuest.get(3));
				}
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
				itemQuest.add(config.getString(p.getName()+".ItemQuest.Poster"));
				
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
				headQuest.add(config.getString(p.getName()+".HeadQuest.WhoNeedToBeKilled"));
				
				needToBeKilled.add(config.getString(p.getName()+".HeadQuest.WhoNeedToBeKilled"));
				
				headQuest.add(config.getString(p.getName()+".HeadQuest.Poster"));
				
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
				itemQuest.add(config.getString(p.getName()+".ItemQuest.Poster"));
				
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
				headQuest.add(config.getString(p.getName()+".HeadQuest.WhoNeedToBeKilled"));
				headQuest.add(config.getString(p.getName()+".HeadQuest.Poster"));
				
				needToBeKilled.add(config.getString(p.getName()+".HeadQuest.WhoNeedToBeKilled"));
				
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
	
	
	public ItemStack createItem(Material material, String displayName)
	{
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(displayName);
		item.setItemMeta(meta);
		
		return item;
	}
	
	public ItemStack createItem(Material material, String displayName, ArrayList<String> lore)
	{
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(displayName);
		
		meta.setLore(lore);
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
					sender.sendMessage("§a/pq qr [玩家名] §3打开确认界面(调试)");
					sender.sendMessage("§a/pq reload §3重载配置");
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("qr"))
			{
				if(sender instanceof Player)
				{
					if(args.length==2)
					{
						if(Bukkit.getPlayer(args[1])!=null)
						{
							Player p = (Player)sender;
							Player p2 = Bukkit.getPlayer(args[1]);
							p.openInventory(gui.initConfirmForPlayer(p, p2, "", ""));
							p2.openInventory(gui.initConfirmForPlayer(p2, p, "", ""));
							confirmPlayers.put(p.getName(), args[1]);
							confirmPlayers.put(args[1], p.getName());
						}
					}
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
					loadMessageConfig();
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

