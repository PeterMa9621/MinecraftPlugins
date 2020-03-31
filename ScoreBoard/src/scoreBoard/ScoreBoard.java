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
		Bukkit.getConsoleSender().sendMessage("��a[ScoreBoard] ��e�Ƿְ�ϵͳ�������");
	}

	public void onDisable() 
	{
		Bukkit.getConsoleSender().sendMessage("��a[ScoreBoard] ��e�Ƿְ�ϵͳж�����");
	}
	
	public void loadConfig()
	{
		File file=new File(getDataFolder(),"config.yml");
		FileConfiguration config;
		if (!file.exists())
		{
			config = load(file);
			config.set("ScoreBoard.Time", 5);
			config.set("ScoreBoard.1.Title", "��7��ӭ�����ҵ�����");
			config.set("ScoreBoard.2.Title", "��7������Ϣ");
			
			for(int i=0; i<2; i++)
			{
				config.set("ScoreBoard."+(i+1)+".Line1", "��e��l{player} {vipGroup}");
				config.set("ScoreBoard."+(i+1)+".Line2", "{vipTime}");
				config.set("ScoreBoard."+(i+1)+".Line3", "��e��l��ǩ����a��l{checkInDays}��e��l��");
				config.set("ScoreBoard."+(i+1)+".Line4", "��e��l�������a��l{judgeCheckIn}��e��lǩ��");
				config.set("ScoreBoard."+(i+1)+".Line5", "��e��l{dailyQuest}");
				config.set("ScoreBoard."+(i+1)+".Line6", "��e��l���: ��f��l{money}");
				config.set("ScoreBoard."+(i+1)+".Line7", "��e��l{simpleClans}");
				config.set("ScoreBoard."+(i+1)+".Line8", "��e��l��ȯ: ��f��l{playerPoints}");
				config.set("ScoreBoard."+(i+1)+".Line9", "��e��l�㵱ǰ��PVP״̬: ��f��l{pvpState}");
				config.set("ScoreBoard."+(i+1)+".Line10", "��e��lף��������");
				config.set("ScoreBoard."+(i+1)+".Line11", "��e��l�ȼ�:{level}");
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
					sender.sendMessage("��a=========[�Ƿְ�ϵͳ]=========");
					sender.sendMessage("��a/score show1 ��e�鿴�Ƿְ��һҳ");
					sender.sendMessage("��a/score show2 ��e�鿴�Ƿְ�ڶ�ҳ");
					sender.sendMessage("��a/score reload ��3��������");
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
					sender.sendMessage("��a[�Ƿְ�ϵͳ] �������سɹ�!");
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

