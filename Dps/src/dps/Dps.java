package dps;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import dps.rewardBox.RewardBoxListener;
import dps.rewardBox.RewardTable;
import dps.util.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import scoreBoard.ScoreBoard;

public class Dps extends JavaPlugin
{
	public static ScoreBoard scoreBoard;
	/**
	 *  This hash map is used to store every group's name and its members' data which includes the damage that
	 *  every member did.
	 */
	//HashMap<String, HashMap<String, Double>> groupDps = new HashMap<String, HashMap<String, Double>>();
	
	/**
	 *  This hash map stores every player's name that is using dps module and the value is this player's
	 *  group's name.
	 */
	//HashMap<String, String> singleDps = new HashMap<String, String>();
	
	//ArrayList<String> whoNeedDps = new ArrayList<String>();
	
	/**
	 *  This is used to store which player need to use the scoreboard to show dps information.
	 */
	public ArrayList<String> dpsTask = new ArrayList<String>();
	
	HashMap<String, HashMap<String, Integer>> groupRank = new HashMap<String, HashMap<String, Integer>>();
	
	//DpsAPI api = new DpsAPI(this);
	
	private boolean hookScoreBoard()
    {
	    final Plugin plugin = this.getServer().getPluginManager().getPlugin("ScoreBoard");
	    scoreBoard = ScoreBoard.class.cast(plugin);
	    return scoreBoard != null; 
	}
	
	public void onEnable() 
	{
		if(!getDataFolder().exists()) 
		{
			getDataFolder().mkdir();
		}
		if(!hookScoreBoard())
		{
			Bukkit.getConsoleSender().sendMessage("§a[Dps] §cScoreBoard未加载");
			return;
		}

		ConfigUtil.loadConfig(this);
		getServer().getPluginManager().registerEvents(new DpsListener(this), this);
		getServer().getPluginManager().registerEvents(new RewardBoxListener(this), this);
		Bukkit.getConsoleSender().sendMessage("§a[Dps] §eDps已加载");
	}

	public void onDisable() 
	{
		Bukkit.getConsoleSender().sendMessage("§a[Dps] §eDps已卸载");
	}

	/*
	public DpsAPI getAPI()
	{
		return api;
	}

	 */
	
	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args)  
	{
		if (cmd.getName().equalsIgnoreCase("dps"))
		{
			if (args.length==0)
			{
				if(sender.isOp())
				{
					sender.sendMessage("§a=========[DPS系统]=========");
					sender.sendMessage("§a/dps start §3启动该玩家的队伍DPS模式");
					sender.sendMessage("§a/dps end §3关闭该玩家的队伍DPS模式");
					sender.sendMessage("§a/dps start [玩家名] §3启动该玩家DPS模式");
					sender.sendMessage("§a/dps close [玩家名] §3关闭该玩家DPS模式");
					sender.sendMessage("§a/dps put [玩家名] [队伍名] §3将该玩家放入另一个队伍中");
				}
				return true;
			}

			if(args.length > 0){
				if(args[0].equalsIgnoreCase("reload")){
					ConfigUtil.loadConfig(this);
				}
			}
			return true;
		}
		return false;
	}
}
