package com.peter.dungeonManager;

import com.peter.dungeonManager.config.ConfigManager;
import com.peter.dungeonManager.expansion.DungeonManagerExpansion;
import com.peter.dungeonManager.gui.GuiListener;
import com.peter.dungeonManager.gui.GuiManager;
import com.peter.dungeonManager.listener.DungeonGroupListener;
import com.peter.dungeonManager.listener.DungeonManagerListener;
import com.peter.dungeonManager.util.DataManager;
import com.peter.dungeonManager.util.GuiType;
import dps.Dps;
import dps.DpsAPI;
import levelSystem.API;
import levelSystem.LevelSystem;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class DungeonManager extends JavaPlugin
{
	public static API levelSystemAPI;

	public ConfigManager configManager;
	public DataManager dataManager;
	public GuiManager guiManager;
	@Override
	public void onEnable()
	{
		if(!getDataFolder().exists()) {
			getDataFolder().mkdir();
		}

		if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
			new DungeonManagerExpansion(this).register();
		}

		if(Bukkit.getPluginManager().getPlugin("LevelSystem") != null){
			levelSystemAPI = ((LevelSystem) Bukkit.getPluginManager().getPlugin("LevelSystem")).getAPI();
			Bukkit.getConsoleSender().sendMessage("§a[DungeonManager] §e等级系统已加载");
		}

		// To do: add support for citizens
		/*
		if(Bukkit.getPluginManager().getPlugin("Citizens") == null) {
			Bukkit.getConsoleSender().sendMessage("§a[DungeonManager] §eCitizens does not install");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		 */
		configManager = new ConfigManager(this);
		dataManager = new DataManager(this);
		guiManager = new GuiManager(this);
		configManager.loadConfig();
		getServer().getPluginManager().registerEvents(new DungeonManagerListener(this), this);
		getServer().getPluginManager().registerEvents(new GuiListener(this), this);
		getServer().getPluginManager().registerEvents(new DungeonGroupListener(this), this);

		Bukkit.getConsoleSender().sendMessage("§a[DungeonManager] §eDungeonManager loaded");
	}

	public void onDisable()
	{
		Bukkit.getConsoleSender().sendMessage("§a[DungeonManager] §eDungeonManager unloaded");
	}

	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args)
	{
		if (cmd.getName().equalsIgnoreCase("dm")) {

			if (args.length==0 && sender instanceof Player) {
				Player player = (Player) sender;

				guiManager.openDungeonGui(player, GuiType.Group);
				return true;
			} else {
				if(!sender.isOp()) {
					return true;
				}
				if(args[0].equalsIgnoreCase("reload")){
					configManager.loadConfig();

					sender.sendMessage("§6[DungeonManager] §3重载配置成功!");
					return true;
				} else if(args[0].equalsIgnoreCase("help")){
					sender.sendMessage("§a=========[DungeonManager]=========");
					sender.sendMessage("§a/dm §3打开BOSS活动界面");
					sender.sendMessage("§a/dm help §3显示帮助菜单");
					sender.sendMessage("§a/dm reload §3重载配置");
					return true;
				}
			}

		}
		return false;
	}
}

