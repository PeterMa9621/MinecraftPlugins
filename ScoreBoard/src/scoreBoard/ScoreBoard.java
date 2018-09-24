package scoreBoard;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import checkInSystem.CheckInSystem;
import dailyQuest.DailyQuest;
import levelSystem.LevelSystem;
import net.milkbowl.vault.economy.Economy;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import pvpSwitch.PVPSwitch;
import vipSystem.VipSystem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class ScoreBoard extends JavaPlugin
{
	Calendar cal = new GregorianCalendar();
    int year = cal.get(Calendar.YEAR);
    int month = cal.get(Calendar.MONTH) + 1;
    int day = cal.get(Calendar.DAY_OF_MONTH);
    String playername = getName();
    boolean useSecond = false;
    int time = 1;
    int interval = 5;
    
    
    
    ArrayList<ArrayList<String>> scoreBoardInfo = new ArrayList<ArrayList<String>>();
    
    ScoreBoardAPI api = new ScoreBoardAPI(this);
    
    public boolean isEco = false;
    
    //==================Plugin's API==================
    private Economy economy;
    private CheckInSystem checkInSystem;
    private PlayerPoints playerPoints;
    private PVPSwitch pvpSwitch;
    private VipSystem vipSystem;
    private DailyQuest dailyQuest;
    private SimpleClans core;
    private LevelSystem levelSystem;
    //==================Plugin's API==================
    
    HashMap<String, Integer> task = new HashMap<String, Integer>();
	
    public ScoreBoardAPI getAPI()
    {
    	return api;
    }
    
	private boolean hookSimpleClans()
	{
		try 
		{
            for (Plugin plugin : getServer().getPluginManager().getPlugins()) 
            {
                if (plugin instanceof SimpleClans) 
                {
                    this.core = (SimpleClans) plugin;
                    return true;
                }
            }
        } 
		catch (NoClassDefFoundError e) 
		{
            return false;
        }
        return false;
	}
    
	private boolean hookLevelSystem() 
    {
	    final Plugin plugin = this.getServer().getPluginManager().getPlugin("LevelSystem");
	    levelSystem = LevelSystem.class.cast(plugin);
	    return levelSystem != null; 
	}
	
    private boolean hookPlayerPoints() 
    {
	    final Plugin plugin = this.getServer().getPluginManager().getPlugin("PlayerPoints");
	    playerPoints = PlayerPoints.class.cast(plugin);
	    return playerPoints != null; 
	}
    
    private boolean hookCheckInSystem() 
    {
	    final Plugin plugin = this.getServer().getPluginManager().getPlugin("CheckInSystem");
	    checkInSystem = CheckInSystem.class.cast(plugin);
	    return checkInSystem != null; 
	}
    
    private boolean hookPVPSwitch() 
    {
	    final Plugin plugin = this.getServer().getPluginManager().getPlugin("PVPSwitch");
	    pvpSwitch = PVPSwitch.class.cast(plugin);
	    return pvpSwitch != null; 
	}
    
    private boolean hookVipSystem() 
    {
	    final Plugin plugin = this.getServer().getPluginManager().getPlugin("VipSystem");
	    vipSystem = VipSystem.class.cast(plugin);
	    return vipSystem != null;
	}
    
    private boolean hookDailyQuest() 
    {
	    final Plugin plugin = this.getServer().getPluginManager().getPlugin("DailyQuest");
	    dailyQuest = DailyQuest.class.cast(plugin);
	    return dailyQuest != null;
	}
    
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
		if(Bukkit.getPluginManager().getPlugin("Vault")!=null)
		{
			isEco=setupEconomy();
		}
		if(hookPlayerPoints()==false)
		{
			Bukkit.getConsoleSender().sendMessage("§a[ScoreBoard] §cPlayerPoints未加载");
		}
		if(hookCheckInSystem()==false)
		{
			Bukkit.getConsoleSender().sendMessage("§a[ScoreBoard] §cCheckInSystem未加载");
		}
		if(hookPVPSwitch()==false)
		{
			Bukkit.getConsoleSender().sendMessage("§a[ScoreBoard] §cPVPSwitch未加载");
		}
		if(hookVipSystem()==false)
		{
			Bukkit.getConsoleSender().sendMessage("§a[ScoreBoard] §cVipSystem未加载");
		}
		if(hookDailyQuest()==false)
		{
			Bukkit.getConsoleSender().sendMessage("§a[ScoreBoard] §cDailyQuest未加载");
		}
		if(hookSimpleClans()==false)
		{
			Bukkit.getConsoleSender().sendMessage("§a[ScoreBoard] §cSimpleClans未加载");
		}
		if(hookLevelSystem()==false)
		{
			Bukkit.getConsoleSender().sendMessage("§a[ScoreBoard] §cLevelSystem未加载");
		}
		if(!getDataFolder().exists()) 
		{
			getDataFolder().mkdir();
		}
		loadConfig();
		getServer().getPluginManager().registerEvents(new ScoreBoardListener(this), this);
		Bukkit.getConsoleSender().sendMessage("§a[ScoreBoard] §e记分板系统加载完成");
	}

	public void onDisable() 
	{
		Bukkit.getConsoleSender().sendMessage("§a[ScoreBoard] §e记分板系统卸载完成");
	}
	
	public void loadConfig()
	{
		File file=new File(getDataFolder(),"config.yml");
		FileConfiguration config;
		if (!file.exists())
		{
			config = load(file);
			config.set("Variable", "{player}玩家名，{checkInDays}签到天数，{judgeCheckIn}是否签到，"
					+ "{playerPoints}点券数量，{money}金币数量，{pvpSwitch}是否可PVP");
			config.set("ScoreBoard.Time", 5);
			config.set("ScoreBoard.1.Title", "§7欢迎进入我的世界");
			config.set("ScoreBoard.2.Title", "§7个人信息");
			
			for(int i=0; i<2; i++)
			{
				config.set("ScoreBoard."+(i+1)+".Line1", "§e§l{player} {vipGroup}");
				config.set("ScoreBoard."+(i+1)+".Line2", "{vipTime}");
				config.set("ScoreBoard."+(i+1)+".Line3", "§e§l已签到§a§l{checkInDays}§e§l天");
				config.set("ScoreBoard."+(i+1)+".Line4", "§e§l今天你§a§l{judgeCheckIn}§e§l签到");
				config.set("ScoreBoard."+(i+1)+".Line5", "§e§l{dailyQuest}");
				config.set("ScoreBoard."+(i+1)+".Line6", "§e§l金币: §f§l{money}");
				config.set("ScoreBoard."+(i+1)+".Line7", "§e§l{simpleClans}");
				config.set("ScoreBoard."+(i+1)+".Line8", "§e§l点券: §f§l{playerPoints}");
				config.set("ScoreBoard."+(i+1)+".Line9", "§e§l你当前的PVP状态: §f§l{pvpState}");
				config.set("ScoreBoard."+(i+1)+".Line10", "§e§l祝您玩的愉快");
				config.set("ScoreBoard."+(i+1)+".Line11", "§e§l等级:{level}");
			}
			
			
			try 
			{
				config.save(file);
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			loadConfig();
			return;
		}
		config = load(file);
		scoreBoardInfo.clear();
		for(int i=0; config.contains("ScoreBoard."+(i+1)); i++)
		{
			ArrayList<String> everyPage = new ArrayList<String>();
			everyPage.add(config.getString("ScoreBoard."+(i+1)+".Title"));
			for(int j=0; config.contains("ScoreBoard."+(i+1)+".Line"+(j+1)); j++)
			{
				if(j>=15)
					break;
				everyPage.add(config.getString("ScoreBoard."+(i+1)+".Line"+(j+1)));
			}
			scoreBoardInfo.add(everyPage);
		}
		if(scoreBoardInfo.size()==2)
			useSecond = true;
		interval = config.getInt("ScoreBoard.Time");
		return;
		
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
	
	public void runTask(Player p)
	{
		if(!task.containsKey(p.getName()))
		{
			task.put(p.getName(), getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
			{
				public void run()
				{
					if(useSecond==true)
					{
						if(time==1)
						{
							displayYourGameBoard1(p);
							time=2;
						}
						else
						{
							displayYourGameBoard2(p);
							time=1;
						}
					}
					else
					{
						displayYourGameBoard1(p);
					}
				}
			} ,5*20, interval*20));
		}
	}
	
	public void displayYourGameBoard1(Player p)
    {
		
		Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		
        
        Objective randomObjective = scoreboard.registerNewObjective("scoreboard1", "dummy1");
    	String title = scoreBoardInfo.get(0).get(0);
    	
    	if(title.contains("{player}"))
    		title = title.replace("{player}", p.getName());
    	
    	if(title.contains("{checkInDays}"))
    		title = title.replace("{checkInDays}", String.valueOf(checkInSystem.getAPI().getDays(p.getName())));
    	
    	if(title.contains("{judgeCheckIn}"))
    		if(checkInSystem.getAPI().isCheckIn(p.getName())==true)
    			title = title.replace("{judgeCheckIn}", "已");
    		else
    			title = title.replace("{judgeCheckIn}", "未");
    	
    	if(title.contains("{playerPoints}"))
    		title = title.replace("{playerPoints}", String.valueOf(playerPoints.getAPI().look(p.getName())));
    	
    	if(title.contains("{money}"))
    		title = title.replace("{money}", String.valueOf(economy.getBalance(p.getName())));
		
    	randomObjective.setDisplayName(title);
    	randomObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
    	for(int j=0; j<this.scoreBoardInfo.get(0).size()-1; j++)
    	{
    		String content = scoreBoardInfo.get(0).get(j+1);
    		
    		
    		if(content.contains("{player}"))
    			content = content.replace("{player}", p.getName());
        	
    		if(content.contains("{checkInDays}"))
        		content = content.replace("{checkInDays}", String.valueOf(checkInSystem.getAPI().getDays(p.getName())));
        	
        	if(content.contains("{judgeCheckIn}"))
        		if(checkInSystem.getAPI().isCheckIn(p.getName())==true)
        			content = content.replace("{judgeCheckIn}", "已");
        		else
        			content = content.replace("{judgeCheckIn}", "未");
        	
        	if(content.contains("{playerPoints}"))
        		content = content.replace("{playerPoints}", String.valueOf(playerPoints.getAPI().look(p.getName())));
        	
        	if(content.contains("{money}"))
        		content = content.replace("{money}", String.valueOf(economy.getBalance(p.getName())));
        	
        	if(content.contains("{pvpState}"))
        		if(pvpSwitch.getAPI().getPVPState(p)==true)
        			content = content.replace("{pvpState}", "开");
        		else
        			content = content.replace("{pvpState}", "关");
        	
        	if(content.contains("{vipGroup}"))
        		if(vipSystem.getAPI().getVipGroupName(p)==null)
        			content = content.replace("{vipGroup}", "§f§l普通玩家");
        		else
        			content = content.replace("{vipGroup}", vipSystem.getAPI().getVipGroupName(p));
        	
        	if(content.contains("{vipTime}"))
        		if(vipSystem.getAPI().getLeftHour(p)==0)
    			{
    				vipSystem.getAPI().removeVip(p);
    				content = content.replace("{vipTime}", " ");
    			}
    			else
    				content = content.replace("{vipTime}", vipSystem.getAPI().getLeftTime(p));
        	
        	if(content.contains("{dailyQuest}"))
        		if(dailyQuest.getAPI().getHowMnayQuestLeft(p)!=0)
        			content = content.replace("{dailyQuest}", "剩余日常任务:§c§l"+dailyQuest.getAPI().getHowMnayQuestLeft(p));
        		else
        			content = content.replace("{dailyQuest}", "日常任务已全部完成");
        	
        	if(content.contains("{simpleClans}"))
        		if(core.getClanManager().getClanByPlayerName(p.getName())==null)
        			content = content.replace("{simpleClans}", "§b§l你还没有家族");
        		else
        		{
        			String clanTag = core.getClanManager().getClanByPlayerName(p.getName()).getTagLabel(false);
        			clanTag = clanTag.substring(3, clanTag.length()-3);
        			content = content.replace("{simpleClans}", clanTag);
        		}
        	
        	if(content.contains("{level}"))
        		content = content.replace("{level}", String.valueOf(levelSystem.getAPI().getLevel(p)));
        	
    		randomObjective.getScore(content).setScore(15-j);
    	}
    	p.setScoreboard(scoreboard);
    }
	
	public void displayYourGameBoard2(Player p)
    {
		
		Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        p.setScoreboard(scoreboard);
        
        Objective randomObjective = scoreboard.registerNewObjective("scoreboard2", "dummy2");
    	String title = scoreBoardInfo.get(1).get(0);
    	
    	if(title.contains("{player}"))
    		title = title.replace("{player}", p.getName());
    	
    	if(title.contains("{checkInDays}"))
    		title = title.replace("{checkInDays}", String.valueOf(checkInSystem.getAPI().getDays(p.getName())));
    	
    	if(title.contains("{judgeCheckIn}"))
    		if(checkInSystem.getAPI().isCheckIn(p.getName())==true)
    			title = title.replace("{judgeCheckIn}", "已");
    		else
    			title = title.replace("{judgeCheckIn}", "未");
    	
    	if(title.contains("{playerPoints}"))
    		title = title.replace("{playerPoints}", String.valueOf(playerPoints.getAPI().look(p.getName())));
    	
    	if(title.contains("{money}"))
    		title = title.replace("{money}", String.valueOf(economy.getBalance(p.getName())));
    	randomObjective.setDisplayName(title);
    	randomObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
    	
    	for(int j=0; j<this.scoreBoardInfo.get(1).size()-1; j++)
    	{
    		String content = scoreBoardInfo.get(1).get(j+1);
    		
    		if(content.contains("{player}"))
    			content = content.replace("{player}", p.getName());
        	
    		if(content.contains("{checkInDays}"))
        		content = content.replace("{checkInDays}", String.valueOf(checkInSystem.getAPI().getDays(p.getName())));
        	
        	if(content.contains("{judgeCheckIn}"))
        		if(checkInSystem.getAPI().isCheckIn(p.getName())==true)
        			content = content.replace("{judgeCheckIn}", "已");
        		else
        			content = content.replace("{judgeCheckIn}", "未");
        	
        	if(content.contains("{playerPoints}"))
        		content = content.replace("{playerPoints}", String.valueOf(playerPoints.getAPI().look(p.getName())));
        	
        	if(content.contains("{money}"))
        		content = content.replace("{money}", String.valueOf(economy.getBalance(p.getName())));
        	
        	if(content.contains("{pvpState}"))
        		if(pvpSwitch.getAPI().getPVPState(p)==true)
        			content = content.replace("{pvpState}", "开");
        		else
        			content = content.replace("{pvpState}", "关");
        	
        	if(content.contains("{vipGroup}"))
        		if(vipSystem.getAPI().getVipGroupName(p)==null)
        			content = content.replace("{vipGroup}", "§f普通玩家");
        		else
        			content = content.replace("{vipGroup}", "§c"+vipSystem.getAPI().getVipGroupName(p)+",剩余时间:"+vipSystem.getAPI().getLeftTime(p));
        	
        	if(content.contains("{vipTime}"))
        		if(vipSystem.getAPI().getLeftHour(p)==0)
    			{
    				vipSystem.getAPI().removeVip(p);
    				content = content.replace("{vipTime}", " ");
    			}
    			else
    				content = content.replace("{vipTime}", vipSystem.getAPI().getLeftTime(p));
        	
        	if(content.contains("{dailyQuest}"))
        		if(dailyQuest.getAPI().getHowMnayQuestLeft(p)!=0)
        			content = content.replace("{dailyQuest}", "剩余日常任务:§c§l"+dailyQuest.getAPI().getHowMnayQuestLeft(p));
        		else
        			content = content.replace("{dailyQuest}", "日常任务已全部完成");
        	
        	if(content.contains("{simpleClans}"))
        		if(core.getClanManager().getClanByPlayerName(p.getName())==null)
        			content = content.replace("{simpleClans}", "§b§l你还没有家族");
        		else
        		{
        			String clanTag = core.getClanManager().getClanByPlayerName(p.getName()).getTagLabel(false);
        			clanTag = clanTag.substring(3, clanTag.length()-3);
        			content = content.replace("{simpleClans}", clanTag);
        		}
        	
        	if(content.contains("{level}"))
        		content = content.replace("{level}", String.valueOf(levelSystem.getAPI().getLevel(p)));
    		randomObjective.getScore(content).setScore(15-j);
    	}

    }
	
	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args)  
	{
		if (cmd.getName().equalsIgnoreCase("score"))
		{
			if (args.length==0)
			{
				if(sender.isOp())
				{
					sender.sendMessage("§a=========[记分板系统]=========");
					sender.sendMessage("§a/score show1 §e查看记分板第一页");
					sender.sendMessage("§a/score show2 §e查看记分板第二页");
					sender.sendMessage("§a/score reload §3重载配置");
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("show1"))
			{
				if(sender.isOp())
				{
					displayYourGameBoard1((Player)sender);
				}
			}
			
			if(args[0].equalsIgnoreCase("show2"))
			{
				if(sender.isOp())
				{
					displayYourGameBoard2((Player)sender);
				}
			}
			
			if(args[0].equalsIgnoreCase("reload"))
			{
				if(sender.isOp())
				{
					loadConfig();
					displayYourGameBoard1((Player)sender);
					if(this.useSecond==true)
						time=2;
					sender.sendMessage("§a[记分板系统] 配置重载成功!");
				}
			}
			return true;
		}

		return false;
		
	}
	
	
}

