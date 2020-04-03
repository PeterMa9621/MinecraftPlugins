package com.peter.worldBoss;

import com.peter.worldBoss.WorldBossListener;
import com.peter.worldBoss.config.ConfigManager;
import com.peter.worldBoss.expansion.WorldBossExpansion;

import com.peter.worldBoss.gui.GuiListener;
import com.peter.worldBoss.gui.GuiManager;
import com.peter.worldBoss.model.BossGroup;
import com.peter.worldBoss.model.BossGroupSetting;
import de.erethon.dungeonsxl.api.DungeonsAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;

public class WorldBoss extends JavaPlugin
{
	public static HashMap<String, BossGroupSetting> bossGroupSetting = new HashMap<>();
	public static HashMap<String, BossGroup> bossGroups = new HashMap<>();

	public BukkitTask timer;
	@Override
	public void onEnable()
	{
		if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
			new WorldBossExpansion(this).register();
		}
		ConfigManager.loadConfig(this);
		getServer().getPluginManager().registerEvents(new WorldBossListener(this), this);
		getServer().getPluginManager().registerEvents(new GuiListener(this), this);
		runTimerToCheckBeginTime();
		GuiManager.plugin = this;
		Bukkit.getConsoleSender().sendMessage("§a[WorldBoss] §eWorldBoss loaded");
	}

	public void onDisable()
	{
		Bukkit.getConsoleSender().sendMessage("§a[WorldBoss] §eWorldBoss unloaded");
	}

	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args)
	{
		if (cmd.getName().equalsIgnoreCase("boss")) {

			if (args.length==0 && sender instanceof Player) {
				Player player = (Player) sender;
				GuiManager.openWorldBossGui(player);
				return true;
			} else {
				if(args[0].equalsIgnoreCase("reload")){
					ConfigManager.loadConfig(this);
					sender.addAttachment(this, "dxl.group", true);
					sender.sendMessage("§6[WorldBoss] §3重载配置成功!");
					return true;
				} else if(args[0].equalsIgnoreCase("help")){
					sender.sendMessage("§a=========[WorldBoss]=========");
					sender.sendMessage("§a/boss §3打开BOSS活动界面");
					sender.sendMessage("§a/boss help §3显示帮助菜单");
					sender.sendMessage("§a/boss reload §3重载配置");
					return true;
				}
			}

		}
		return false;
	}

	public void runTimerToCheckBeginTime() {
		new BukkitRunnable() {
			@Override
			public void run() {
				LocalDateTime now = LocalDateTime.now();
				for(BossGroupSetting setting:bossGroupSetting.values()){
					if(setting.isStartedToday()){
						continue;
					}
					setting.notifyPlayers(WorldBoss.this);
					if(setting.canStart()){
						setting.setPrevStartTime(now);
						Bukkit.broadcastMessage("§6[WorldBoss] §2世界BOSS活动§5" + setting.getDisplayName() + "§2开始了!");
						BossGroup bossGroup = bossGroups.get(setting.getGroupName());
						if(bossGroup!=null){
							bossGroup.startGame(setting.getStartGameCmd(), WorldBoss.this);
							bossGroups.remove(bossGroup.getGroupName());
						}
					}
				}
			}
		}.runTaskTimer(this, 0, 200);
	}
}

