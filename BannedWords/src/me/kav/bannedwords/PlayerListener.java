/*** Eclipse Class Decompiler plugin, copyright (c) 2016 Chen Chao (cnfree2000@hotmail.com) ***/
package me.kav.bannedwords;

import java.util.List;
import me.kav.bannedwords.Main;
import me.kav.bannedwords.hashmaps;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerListener implements Listener, hashmaps {
	Main plugin;

	public PlayerListener(Main instance) {
		this.plugin = instance;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (bans.containsKey(player.getName().toString())) {
			long current = System.currentTimeMillis();
			long bannedplayer = this.plugin.getConfig().getLong("bannedplayers." + player.getName().toString());
			if (current >= bannedplayer) {
				bans.remove(player.getName().toString(), player.getName().toString());
				this.plugin.getConfig().set(player.getName().toString(), (Object) null);
				this.plugin.saveConfig();
			} else {
				player.kickPlayer(ChatColor.YELLOW + "Banned" + ChatColor.RED + "Words " + ChatColor.GRAY + "> "
						+ ChatColor.RED + "You are temporarily banned for saying a banned line/word");
			}
		}

	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onChat(AsyncPlayerChatEvent event) {
		final Player player = event.getPlayer();
		final String kplayer = (String) kicks.get(player.getName().toString());
		String message = event.getMessage();
		List configList = this.plugin.getConfig().getList("bannedwords");
		String[] bannedwords = new String[configList.toArray().length];
		if (!player.hasPermission("bannedwords.exempt")) {
			for (int i = 0; i < configList.toArray().length; ++i) {
				bannedwords[i] = (String) configList.get(i);
				if (message.toLowerCase().contains(bannedwords[i].toLowerCase())) {
					event.setCancelled(true);
					final String bannedword = bannedwords[i].toString();
					(new BukkitRunnable() {
						public void run() {
							if (kplayer == null) {
								PlayerListener.kicks.put(player.getName().toString(), "1");
								player.kickPlayer(ChatColor.YELLOW + "Banned" + ChatColor.RED + "Words "
										+ ChatColor.GRAY + "> " + ChatColor.RED + "Kicked for saying banned word/line "
										+ ChatColor.DARK_RED + bannedword);
							} else if (kplayer == "1") {
								PlayerListener.kicks.put(player.getName().toString(), "2");
								player.kickPlayer(ChatColor.YELLOW + "Banned" + ChatColor.RED + "Words "
										+ ChatColor.GRAY + "> " + ChatColor.RED + "Kicked for saying banned word/line "
										+ ChatColor.DARK_RED + bannedword);
							} else if (kplayer == "2") {
								PlayerListener.kicks.put(player.getName().toString(), "3");
								player.kickPlayer(ChatColor.YELLOW + "Banned" + ChatColor.RED + "Words "
										+ ChatColor.GRAY + "> " + ChatColor.RED + "Kicked for saying banned word/line "
										+ ChatColor.DARK_RED + bannedword + ChatColor.RED
										+ " This is your last warning!");
							} else if (kplayer == "3") {
								PlayerListener.kicks.put(player.getName().toString(), "4");
								player.kickPlayer(
										ChatColor.YELLOW + "Banned" + ChatColor.RED + "Words " + ChatColor.GRAY + "> "
												+ ChatColor.RED + "You were banned for " + ChatColor.DARK_RED
												+ PlayerListener.this.plugin.getConfig().getInt("bantime") + " minutes "
												+ ChatColor.RED + "for saying the banned word " + ChatColor.DARK_RED
												+ bannedword.toString() + ChatColor.RED + " We warned you!");
								PlayerListener.kicks.put(player.getName().toString(), null);
								PlayerListener.bans.put(player.getName().toString(), player.getName().toString());
								PlayerListener.loggingsequence.put(player.getName().toString(),
										player.getName().toString());
								long currentstamp = System.currentTimeMillis();
								long addstamp = PlayerListener.this.plugin.getConfig().getLong("bantime") * 60L * 1000L;
								long finalstamp = currentstamp + addstamp;
								PlayerListener.this.plugin.getConfig()
										.set("bannedplayers." + player.getName().toString(), Long.valueOf(finalstamp));
								PlayerListener.this.plugin.saveConfig();
							}

						}
					}).runTask(this.plugin);
				}
			}
		}

	}
}