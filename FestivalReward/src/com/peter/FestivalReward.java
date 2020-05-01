package com.peter;

import com.peter.config.ConfigManager;
import com.peter.listener.FestivalRewardListener;
import com.peter.manager.FestivalManager;
import com.peter.manager.FestivalPlayerManager;
import com.peter.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public class FestivalReward extends JavaPlugin
{
	public ConfigManager configManager;
	public FestivalPlayerManager festivalPlayerManager;
	public FestivalManager festivalManager;
	public void onEnable() {
		festivalManager = new FestivalManager(this);
		festivalPlayerManager = new FestivalPlayerManager(this);
		configManager = new ConfigManager(this);
		configManager.loadConfig();
		getServer().getPluginManager().registerEvents(new FestivalRewardListener(this), this);
		Bukkit.getConsoleSender().sendMessage("§a[FestivalReward] §eFestivalReward loaded ");
	}

	public void onDisable() {
		Bukkit.getConsoleSender().sendMessage("§a[FestivalReward] §eFestivalReward unload");
	}
	
	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args)  
	{
		if (cmd.getName().equalsIgnoreCase("reward")) {
			if (args.length==0 && sender instanceof Player) {
				Player player = (Player) sender;
				try {
					Util.openGui(this, player);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return true;
			}

			if (args.length == 1 && sender instanceof Player) {
				if(args[0].equalsIgnoreCase("reload")) {
					configManager.loadConfig();
					return true;
				}
				if(args[0].equalsIgnoreCase("help")) {
					if(sender.isOp()) {
						sender.sendMessage("§6=========[FestivalReward]=========");
						sender.sendMessage("§6/reward §a打开界面");
						sender.sendMessage("§6/reward reload  §a重载配置");
					}
					return true;
				}
			}
		}
		return false;
	}
}

