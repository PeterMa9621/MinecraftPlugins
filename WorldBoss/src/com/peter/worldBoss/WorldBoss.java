package com.peter.worldBoss;

import com.peter.worldBoss.config.ConfigManager;
import com.peter.worldBoss.expansion.WorldBossExpansion;
import com.peter.worldBoss.gui.GuiListener;
import com.peter.worldBoss.gui.GuiManager;
import com.peter.worldBoss.listener.BungeecordListener;
import com.peter.worldBoss.manager.BossGroupManager;
import com.peter.worldBoss.model.BossGroup;
import com.peter.worldBoss.model.BossGroupSetting;
import com.peter.worldBoss.util.BungeecordUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.time.LocalDateTime;

public class WorldBoss extends JavaPlugin
{
	public BukkitTask timer;
	@Override
	public void onEnable()
	{
		checkIfBungee();
		getServer().getMessenger().registerOutgoingPluginChannel( this, "BungeeCord" );
		getServer().getMessenger().registerIncomingPluginChannel( this, "peter:worldboss", new BungeecordListener(this)); // we register the incoming channel
		if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
			new WorldBossExpansion(this).register();
		}
		ConfigManager.loadConfig(this);
		getServer().getPluginManager().registerEvents(new WorldBossListener(this), this);
		getServer().getPluginManager().registerEvents(new GuiListener(this), this);
		runTimerToCheckBeginTime();
		GuiManager.plugin = this;
		BossGroupManager.plugin = this;
		Bukkit.getConsoleSender().sendMessage("§a[世界Boss] §eWorldBoss loaded");
	}

	public void onDisable()
	{
		Bukkit.getConsoleSender().sendMessage("§a[世界Boss] §eWorldBoss unloaded");
	}

	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args)
	{
		if (cmd.getName().equalsIgnoreCase("boss")) {

			if (args.length==0 && sender instanceof Player) {
				Player player = (Player) sender;
				player.sendMessage(String.valueOf(Bukkit.getServer().getPort()));
				GuiManager.openWorldBossGui(player);
				return true;
			} else {
				if(!sender.isOp())
					return true;
				if(args[0].equalsIgnoreCase("reload")){
					ConfigManager.loadConfig(this);

					sender.sendMessage("§6[世界Boss] §3重载配置成功!");
					return true;
				} else if(args[0].equalsIgnoreCase("help")){
					sender.sendMessage("§a=========[世界Boss]=========");
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
		timer = new BukkitRunnable() {
			@Override
			public void run() {
				LocalDateTime now = LocalDateTime.now();
				for(BossGroupSetting setting: BossGroupManager.bossGroupSetting.values()){
					if(!setting.isTodayBossActivity())
						continue;
					if(setting.isStartedToday())
						continue;

					setting.notifyPlayers(WorldBoss.this);
					if(setting.canStart()){
						setting.setPrevStartTime(now);
						Bukkit.broadcastMessage("§6[世界Boss] §2世界BOSS活动§5" + setting.getDisplayName() + "§2开始了!");
						BossGroup bossGroup = BossGroupManager.bossGroups.get(setting.getGroupName());
						if(bossGroup!=null){
							bossGroup.startGame(setting.getStartGameCmd(), WorldBoss.this);
							BossGroupManager.bossGroups.remove(bossGroup.getGroupName());
						}
					}
				}
			}
		}.runTaskTimer(this, 0, 200);
	}

	// we check like that if the specified server is BungeeCord.
	private void checkIfBungee()
	{
		// we check if the server is Spigot/Paper (because of the spigot.yml file)
		if ( !getServer().getVersion().contains( "Spigot" ) && !getServer().getVersion().contains( "Paper" ) )
		{
			getLogger().severe( "You probably run CraftBukkit... Please update atleast to spigot for this to work..." );
			getLogger().severe( "Plugin disabled!" );
			getServer().getPluginManager().disablePlugin( this );
			return;
		}
		if ( getServer().spigot().getConfig().getConfigurationSection("settings").getBoolean( "settings.bungeecord" ) )
		{
			getLogger().severe( "This server is not BungeeCord." );
			getLogger().severe( "If the server is already hooked to BungeeCord, please enable it into your spigot.yml aswell." );
			getLogger().severe( "Plugin disabled!" );
			getServer().getPluginManager().disablePlugin( this );
		}
	}
}

