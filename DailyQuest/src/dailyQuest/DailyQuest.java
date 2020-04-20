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
			Bukkit.getConsoleSender().sendMessage("��a[DailyQuest] ��cVault���δ����!");
			return;
		}

		if(!hookLP()) {
			Bukkit.getConsoleSender().sendMessage("��a[DailyQuest] ��cLuckPerms���δ����!");
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
		Bukkit.getConsoleSender().sendMessage("��a[DailyQuest] ��e�ճ�����ϵͳ�������");
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
		Bukkit.getConsoleSender().sendMessage("��a[DailyQuest] ��e�ճ�����ϵͳж�����");
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
						p.sendMessage("��6[�ճ�����] ��a��Ŀǰû�н�����������/rw get��ȡ����!");
						return true;
					}
					Quest quest = questPlayer.getCurrentQuest();
					p.sendMessage("��6[�� "+ questPlayer.getCurrentNumber()+" ��] ��a"+ quest.getQuestDescribe());
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("help"))
			{
				sender.sendMessage("��6=========[�ճ�����]=========");
				sender.sendMessage("��6/rw ��8- ��e�鿴��ǰ����");
				if(configManager.enableCommandGetQuest)
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
							if(CitizensAPI.getNPCRegistry().getById(Integer.parseInt(args[1]))!=null)
							{
								configManager.getQuestNPCId = Integer.parseInt(args[1]);
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
						Player player = Bukkit.getServer().getPlayer(args[1]);
						if(player==null) {
							sender.sendMessage("��6[�ճ�����] ��c����Ҳ����ڻ�����");
							return true;
						}

						if(!questPlayerManager.containPlayer(player)) {
							sender.sendMessage("��6[�ճ�����] ��cû�и���ҵ��������ݣ��޷�����");
							return true;
						}

						QuestPlayer questPlayer = questPlayerManager.getQuestPlayer(player);
						questPlayer.clearQuestData();

						player.sendMessage("��6[�ճ�����] ��3������������ѱ�����");
						sender.sendMessage("��6[�ճ�����] ��e��������ҡ�d"+args[1]+"��e����������");

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
					p.openInventory(guiManager.rewardItemGUI(p).get(0));
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
					p.openInventory(guiManager.questItemGUI(p).get(0));
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
					configManager.loadConfig();
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
					if(questPlayerManager.containPlayer(p))
					{
						QuestPlayer questPlayer = questPlayerManager.getQuestPlayer(p);
						int questLimit = questPlayer.getDailyLimit();
						String msg = "��e�����������:��d"+questLimit+"��e,�������:��d"+ questPlayer.getTotalQuest()+"��e,";
						if(questPlayer.getCurrentNumber()==0)
							msg += "��Ŀǰû�н�������";
						else
							msg += "��ǰΪ�ڡ�d"+ questPlayer.getCurrentNumber()+"��e������";
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
						p.sendMessage("��6[�ճ�����] ��c�޷�ʹ��ָ���ȡ��������"+npcName+"����ȡ");
						return true;
					}
					QuestPlayer questPlayer = questPlayerManager.getQuestPlayer(p);
					if(questPlayer.getCurrentNumber()!=0) {
						p.sendMessage("��6[�ճ�����] ��a��Ŀǰ��������������ȡ��������������ȡ����!");
						return true;
					}
					int questLimit = questPlayer.getDailyLimit();
					if(questPlayer.getTotalQuest()>=questLimit) {
						p.sendMessage("��6[�ճ�����] ��a�����������Ѵ����ޣ�����������!");
						return true;
					}
					
					// Get the state of this player
					// the first index means the current index of the quest
					// the second index means what the quest is
					// the last index means how many quests totally this player has already finished
					questPlayer.getNextQuest();

					p.sendMessage("��6[�� 1 ��] ��a"+questPlayer.getCurrentQuest().getQuestDescribe());
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
							p.sendMessage("��6[�ճ�����] ��c�ѷ�����ǰ������!");
						} else {
							p.sendMessage("��6[�ճ�����] ��d��ȷ��Ҫ������������?����10�����ٴ�����һ���c/rw quit��d��ȷ��");
							cancelQuestPlayer.add(uniqueId);
							cancelTask.put(uniqueId, Bukkit.getScheduler().runTaskLater(this, () -> {
								if(cancelQuestPlayer.contains(uniqueId)) {
									p.sendMessage("��6[�ճ�����] ��7ȡ����������");
									cancelQuestPlayer.remove(uniqueId);
								}
							}, 10*20));
						}
						return true;
					}
					p.sendMessage("��6[�ճ�����] ��a��Ŀǰû���κ�����!");
				}
				return true;
			}
		}
		return false;
		
	}
	
	
}

