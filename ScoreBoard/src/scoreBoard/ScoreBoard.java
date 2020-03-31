package scoreBoard;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import me.clip.placeholderapi.PlaceholderAPI;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class ScoreBoard extends JavaPlugin implements Listener
{
    boolean useSecond = false;
    int time = 1;
    int interval = 5;

    ArrayList<ArrayList<String>> scoreBoardInfo = new ArrayList<ArrayList<String>>();
    
    ScoreBoardAPI api = new ScoreBoardAPI(this);
    
    HashMap<String, Integer> task = new HashMap<String, Integer>();
	
    public ScoreBoardAPI getAPI()
    {
    	return api;
    }
    
    private void hookPlaceHolderAPI()
    {
		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
			/*
			 * We register the EventListeneres here, when PlaceholderAPI is installed.
			 * Since all events are in the main class (this class), we simply use "this"
			 */
			Bukkit.getPluginManager().registerEvents(this, this);
			Bukkit.getConsoleSender().sendMessage("Found PlaceholderAPI");
		}
	}
    
	public void onEnable() 
	{
		if(!getDataFolder().exists()) 
		{
			getDataFolder().mkdir();
		}
		hookPlaceHolderAPI();
		loadConfig();
		getServer().getPluginManager().registerEvents(this, this);
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

		for(Player p:Bukkit.getOnlinePlayers()){
			runTask(p);
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
	
	public void runTask(Player p)
	{
		if(!task.containsKey(p.getName()))
		{
			task.put(p.getName(), getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
			{
				public void run()
				{
					if(useSecond)
					{
						if(time==1)
						{
							displayYourGameBoard(p, 0);
							time=2;
						}
						else
						{
							displayYourGameBoard(p, 1);
							time=1;
						}
					}
					else
					{
						displayYourGameBoard(p, 0);
					}
				}
			} ,5*20, interval*20));
		}
	}
	
	public void displayYourGameBoard(Player p, int index)
    {
		Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		ArrayList<String> lines = scoreBoardInfo.get(index);
        
        Objective randomObjective = scoreboard.registerNewObjective("scoreboard"+(index+1), "dummy"+(index+1));
    	String title = lines.get(0);
    	
    	title = PlaceholderAPI.setPlaceholders(p, title);
		
    	randomObjective.setDisplayName(title);
    	randomObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
    	for(int j=0; j<lines.size()-1; j++)
    	{
    		String content = lines.get(j+1);
    		content = PlaceholderAPI.setPlaceholders(p, content);
        	
    		randomObjective.getScore(content).setScore(15-j);
    	}
    	p.setScoreboard(scoreboard);
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
					displayYourGameBoard((Player)sender, 0);
				}
			}
			
			if(args[0].equalsIgnoreCase("show2"))
			{
				if(sender.isOp())
				{
					displayYourGameBoard((Player)sender, 1);
				}
			}
			
			if(args[0].equalsIgnoreCase("reload"))
			{
				if(sender.isOp())
				{
					loadConfig();
					displayYourGameBoard((Player)sender, 0);
					if(this.useSecond)
						time=2;
					sender.sendMessage("§a[记分板系统] 配置重载成功!");
				}
			}
			return true;
		}

		return false;
		
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		if(task.containsKey(event.getPlayer().getName()))
		{
			getServer().getScheduler().cancelTask(task.get(event.getPlayer().getName()));
			task.remove(event.getPlayer().getName());
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		runTask(event.getPlayer());
	}
}

