package com.peter.dungeonManager;

import com.peter.dungeonManager.config.ConfigManager;
import com.peter.dungeonManager.expansion.DungeonManagerExpansion;
import com.peter.dungeonManager.gui.GuiListener;
import com.peter.dungeonManager.gui.GuiManager;
import com.peter.dungeonManager.listener.DungeonGroupListener;
import com.peter.dungeonManager.model.DungeonGroup;
import com.peter.dungeonManager.util.DataManager;
import com.peter.dungeonManager.util.GuiType;
import com.peter.dungeonManager.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class DungeonManager extends JavaPlugin
{
	@Override
	public void onEnable()
	{
		if(!getDataFolder().exists()) {
			getDataFolder().mkdir();
		}

		if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
			new DungeonManagerExpansion(this).register();
		}

		// To do: add support for citizens
		/*
		if(Bukkit.getPluginManager().getPlugin("Citizens") == null) {
			Bukkit.getConsoleSender().sendMessage("��a[DungeonManager] ��eCitizens does not install");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		 */

		ConfigManager.loadConfig(this);
		getServer().getPluginManager().registerEvents(new DungeonManagerListener(this), this);
		getServer().getPluginManager().registerEvents(new GuiListener(this), this);
		getServer().getPluginManager().registerEvents(new DungeonGroupListener(), this);

		GuiManager.plugin = this;
		Bukkit.getConsoleSender().sendMessage("��a[DungeonManager] ��eDungeonManager loaded");
	}

	public void onDisable()
	{
		Bukkit.getConsoleSender().sendMessage("��a[DungeonManager] ��eDungeonManager unloaded");
	}

	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args)
	{
		if (cmd.getName().equalsIgnoreCase("dm")) {

			if (args.length==0 && sender instanceof Player) {
				Player player = (Player) sender;

				GuiManager.openDungeonGui(player, GuiType.Team);
				return true;
			} else {
				if(args[0].equalsIgnoreCase("reload")){
					ConfigManager.loadConfig(this);

					sender.sendMessage("��6[DungeonManager] ��3�������óɹ�!");
					return true;
				} else if(args[0].equalsIgnoreCase("help")){
					sender.sendMessage("��a=========[DungeonManager]=========");
					sender.sendMessage("��a/dm ��3��BOSS�����");
					sender.sendMessage("��a/dm help ��3��ʾ�����˵�");
					sender.sendMessage("��a/dm reload ��3��������");
					return true;
				}
			}

		}
		return false;
	}
}

