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

public class muteListener implements Listener, hashmaps {
	Main plugin;

	public muteListener(Main instance) {
		this.plugin = instance;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		String message = event.getMessage();
		if (mutes.get(player.getName().toString()) == "4") {
			long configList = System.currentTimeMillis();
			long i = this.plugin.getConfig().getLong("mutedplayers." + player.getName().toString());
			if (configList >= i) {
				mutes.put(player.getName().toString(), null);
				this.plugin.getConfig().set("mutedplayers." + player.getName().toString(), (Object) null);
				this.plugin.saveConfig();
			} else if (configList <= i) {
				event.setCancelled(true);
				player.sendMessage("§6[净化系统] §4由于你发送过多包含非法的内容已被临时禁言!");
			}
		}

		List arg12 = this.plugin.getConfig().getList("bannedwords");
		String[] bannedwords = new String[arg12.toArray().length];

		for (int arg13 = 0; arg13 < arg12.toArray().length; ++arg13) {
			bannedwords[arg13] = (String) arg12.get(arg13);
			if (!player.hasPermission("bannedwords.exempt")
					&& message.toLowerCase().contains(bannedwords[arg13].toLowerCase())) {
				event.setCancelled(true);
				if (mutes.get(player.getName().toString()) == null) {
					mutes.put(player.getName().toString(), "1");
					player.sendMessage("§6[净化系统] §4你试图用包含非法的内容(§c"+bannedwords[arg13]+"§4)聊天");
				} else if (mutes.get(player.getName().toString()) == "1") {
					mutes.put(player.getName().toString(), "2");
					player.sendMessage("§6[净化系统] §4你试图用包含非法的内容(§c"+bannedwords[arg13]+"§4)聊天");
				} else if (mutes.get(player.getName().toString()) == "2") {
					mutes.put(player.getName().toString(), "3");
					player.sendMessage("§6[净化系统] §4你试图用包含非法的内容(§c"+bannedwords[arg13]+"§4)聊天" + ",这是你的最后一次警告!");
				} else if (mutes.get(player.getName().toString()) == "3") {
					mutes.put(player.getName().toString(), "4");
					player.sendMessage("§6[净化系统] §4你被禁言 " + ChatColor.DARK_RED
							+ this.plugin.getConfig().getInt("mutetime") + "分钟 " + ChatColor.RED
							+ "因为你发送过多非法的内容:" + ChatColor.DARK_RED + bannedwords[arg13] + ChatColor.RED);
					long current = System.currentTimeMillis();
					long addstamp = this.plugin.getConfig().getLong("mutetime") * 60L * 1000L;
					long finalstamp = current + addstamp;
					this.plugin.getConfig().set("mutedplayers." + player.getName().toString(),
							Long.valueOf(finalstamp));
					this.plugin.saveConfig();
				}
			}
		}

	}
}