package dailyQuest;

import dailyQuest.api.DailyQuestAPI;
import dailyQuest.config.ConfigManager;
import dailyQuest.expansion.DailyQuestExpansion;
import dailyQuest.gui.GuiManager;
import dailyQuest.listener.DailyQuestListener;
import dailyQuest.listener.FinishQuestListener;
import dailyQuest.listener.GetQuestListener;
import dailyQuest.listener.MobQuestListener;
import dailyQuest.manager.QuestManager;
import dailyQuest.manager.QuestPlayerManager;
import dailyQuest.model.Quest;
import dailyQuest.model.QuestPlayer;
import dailyQuest.util.Util;
import net.citizensnpcs.api.CitizensAPI;
import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class DailyQuest extends JavaPlugin
{
	ArrayList<UUID> cancelQuestPlayer = new ArrayList<>();
	HashMap<UUID, BukkitTask> cancelTask = new HashMap<>();

	DailyQuestAPI api = new DailyQuestAPI(this);
	public static LuckPerms luckPermsApi;

	public ConfigManager configManager;
	public QuestPlayerManager questPlayerManager;
	public GuiManager guiManager;
	public Economy economy;
	public CitizensAPI citizensAPI;

	private String previousDate = null;
	
	private boolean setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServicesManager().getRegistration(Economy.class);
		if (economyProvider != null)
		{
			economy = economyProvider.getProvider();
		}
		return (economy!=null);
	}

	private boolean hookLP() {
		RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
		if (provider != null) {
			luckPermsApi = provider.getProvider();
			return true;
		}
		return false;
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
		if(!setupEconomy()) {
			Bukkit.getConsoleSender().sendMessage("§a[DailyQuest] §cVault插件未加载!");
			return;
		}

		if(!hookLP()) {
			Bukkit.getConsoleSender().sendMessage("§a[DailyQuest] §cLuckPerms插件未加载!");
			return;
		}

		if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
			new DailyQuestExpansion(this).register();
		}

		configManager = new ConfigManager(this);
		questPlayerManager = new QuestPlayerManager(this);
		guiManager = new GuiManager(this);

		configManager.loadConfig();
		configManager.initDatabase();

		previousDate = configManager.date.format(new Date());
		task();
		getServer().getPluginManager().registerEvents(new MobQuestListener(this), this);
		getServer().getPluginManager().registerEvents(new FinishQuestListener(this), this);
		getServer().getPluginManager().registerEvents(new GetQuestListener(this), this);
		CitizensAPI.registerEvents(new DailyQuestListener(this));
		Bukkit.getConsoleSender().sendMessage("§a[DailyQuest] §e日常任务系统加载完毕");
	}



	public void onDisable() 
	{
		for(QuestPlayer questPlayer:questPlayerManager.getQuestPlayers().values()) {
			try {
				configManager.savePlayerConfig(questPlayer);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		configManager.saveConfig();
		Bukkit.getConsoleSender().sendMessage("§a[DailyQuest] §e日常任务系统卸载完毕");
	}
	
	public void task()
	{
		new BukkitRunnable()
		{

			public void run()
			{
				if((!previousDate.equalsIgnoreCase(configManager.date.format(new Date()))))
				{
					for(Player p:Bukkit.getOnlinePlayers())
					{
						QuestPlayer questPlayer = questPlayerManager.getQuestPlayer(p);
						questPlayer.clearQuestData();
					}
					previousDate = configManager.date.format(new Date());
				}
			}
		}.runTaskTimer(this, 0L, 20L);
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
					QuestPlayer questPlayer = configManager.loadPlayerConfig(p);
					if(questPlayer.getCurrentNumber()==0)
					{
						p.sendMessage("§6[日常任务] §a你目前没有接受任务，输入/rw get获取任务!");
						return true;
					}
					Quest quest = questPlayer.getCurrentQuest();
					p.sendMessage("§6[第 "+ questPlayer.getCurrentNumber()+" 环] §a"+ quest.getQuestDescribe());
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("help"))
			{
				sender.sendMessage("§6=========[日常任务]=========");
				sender.sendMessage("§6/rw §8- §e查看当前任务");
				if(configManager.enableCommandGetQuest)
					sender.sendMessage("§6/rw get §8- §e获取新任务");
				sender.sendMessage("§6/rw quit §8- §e放弃当前任务");
				sender.sendMessage("§6/rw info §8- §e查看今天的任务状态");
				sender.sendMessage("§6/rw help §8- §e查看帮助");
				if(sender.isOp())
				{
					sender.sendMessage("§6/rw set [NPCID] §8- §e设置领取任务的NPCID");
					sender.sendMessage("§6/rw clear [玩家名] §8- §e重置该玩家的任务数据");
					sender.sendMessage("§6/rw quest §8- §e显示所有任务物品");
					sender.sendMessage("§6/rw reward §8- §e显示所有奖励物品");
					sender.sendMessage("§6/rw reload §8- §e重载配置");
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
							if(CitizensAPI.getNPCRegistry().getById(Integer.parseInt(args[1]))!=null)
							{
								configManager.getQuestNPCId = Integer.parseInt(args[1]);
								sender.sendMessage("§6[日常任务] §a设置完成");
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
						Player player = Bukkit.getServer().getPlayer(args[1]);
						if(player==null) {
							sender.sendMessage("§6[日常任务] §c该玩家不存在或不在线");
							return true;
						}

						if(!questPlayerManager.containPlayer(player)) {
							sender.sendMessage("§6[日常任务] §c没有该玩家的任务数据，无法重置");
							return true;
						}

						QuestPlayer questPlayer = questPlayerManager.getQuestPlayer(player);
						questPlayer.clearQuestData();

						player.sendMessage("§6[日常任务] §3你的任务数据已被重置");
						sender.sendMessage("§6[日常任务] §e已重置玩家§d"+args[1]+"§e的任务数据");

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
					p.openInventory(guiManager.rewardItemGUI(p).get(0));
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
					p.openInventory(guiManager.questItemGUI(p).get(0));
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
					configManager.loadConfig();
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
					if(questPlayerManager.containPlayer(p))
					{
						QuestPlayer questPlayer = questPlayerManager.getQuestPlayer(p);
						int questLimit = questPlayer.getDailyLimit();
						String msg = "§e你的任务上限:§d"+questLimit+"§e,你已完成:§d"+ questPlayer.getTotalQuest()+"§e,";
						if(questPlayer.getCurrentNumber()==0)
							msg += "你目前没有接受任务";
						else
							msg += "当前为第§d"+ questPlayer.getCurrentNumber()+"§e环任务";
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

					if(!configManager.enableCommandGetQuest && !p.isOp()) {
						String npcName = CitizensAPI.getNPCRegistry().getById(configManager.getQuestNPCId).getFullName();
						p.sendMessage("§6[日常任务] §c无法使用指令获取任务，请在"+npcName+"处领取");
						return true;
					}
					QuestPlayer questPlayer = questPlayerManager.getQuestPlayer(p);
					if(questPlayer.getCurrentNumber()!=0) {
						p.sendMessage("§6[日常任务] §a你目前正在做任务，请先取消任务再重新领取任务!");
						return true;
					}
					int questLimit = questPlayer.getDailyLimit();
					if(questPlayer.getTotalQuest()>=questLimit) {
						p.sendMessage("§6[日常任务] §a你今天的任务已达上限，请明天再来!");
						return true;
					}
					
					// Get the state of this player
					// the first index means the current index of the quest
					// the second index means what the quest is
					// the last index means how many quests totally this player has already finished
					questPlayer.getNextQuest();

					p.sendMessage("§6[第 1 环] §a"+questPlayer.getCurrentQuest().getQuestDescribe());
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("quit"))
			{
				if(sender instanceof Player)
				{
					Player p = (Player)sender;
					UUID uniqueId = p.getUniqueId();
					QuestPlayer questPlayer = questPlayerManager.getQuestPlayer(p);
					if(questPlayer.getCurrentNumber()!=0)
					{
						if(cancelQuestPlayer.contains(uniqueId))
						{
							questPlayer.giveUpCurrentQuest();

							cancelQuestPlayer.remove(uniqueId);
							cancelTask.get(uniqueId).cancel();
							p.sendMessage("§6[日常任务] §c已放弃当前的任务!");
						} else {
							p.sendMessage("§6[日常任务] §d你确定要放弃该任务吗?请在10秒内再次输入一遍§c/rw quit§d来确认");
							cancelQuestPlayer.add(uniqueId);
							cancelTask.put(uniqueId, Bukkit.getScheduler().runTaskLater(this, () -> {
								if(cancelQuestPlayer.contains(uniqueId)) {
									p.sendMessage("§6[日常任务] §7取消放弃任务");
									cancelQuestPlayer.remove(uniqueId);
								}
							}, 10*20));
						}
						return true;
					}
					p.sendMessage("§6[日常任务] §a你目前没有任何任务!");
				}
				return true;
			}
		}
		return false;
		
	}
	
	
}

