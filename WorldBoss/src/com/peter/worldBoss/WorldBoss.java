package com.peter.worldBoss;

import com.peter.worldBoss.WorldBossListener;
import com.peter.worldBoss.expansion.WorldBossExpansion;

import de.erethon.dungeonsxl.api.DungeonsAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class WorldBoss extends JavaPlugin
{
	public static DungeonsAPI dxl;

	@Override
	public void onEnable()
	{
		if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
			new WorldBossExpansion(this).register();
		}

		getServer().getPluginManager().registerEvents(new WorldBossListener(this), this);
		Bukkit.getConsoleSender().sendMessage("��a[PeterHelper] ��eWorldBoss loaded");
	}

	public void onDisable()
	{
		Bukkit.getConsoleSender().sendMessage("��a[PeterHelper] ��eWorldBoss unloaded");
	}
	
	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args)  
	{
		if (cmd.getName().equalsIgnoreCase("worldboss") && sender.isOp()) {

			if (args.length==0) {
				sender.sendMessage("��a=========[WorldBoss]=========");
				sender.sendMessage("��a/worldboss ��3��ʾ�����˵�");

				return true;
			}

		}
		return false;
	}
}

