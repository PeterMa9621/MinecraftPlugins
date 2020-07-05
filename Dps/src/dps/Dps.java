package dps;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import dps.model.DpsPlayer;
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


	DpsAPI api = new DpsAPI(this);
	
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

	public DpsAPI getAPI() {
		return api;
	}
	
	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args)  
	{
		if (cmd.getName().equalsIgnoreCase("dps"))
		{
			if (args.length==0)
			{
				if(sender.isOp())
				{
					sender.sendMessage("§a=========[DPS系统]=========");
					sender.sendMessage("§a/dps reload §3重载配置");
				}
				return true;
			}

			if(args[0].equalsIgnoreCase("reload")){
				ConfigUtil.loadConfig(this);
				sender.sendMessage("§a[Dps] §e配置重载成功");
			}

			return true;
		}
		return false;
	}
}
