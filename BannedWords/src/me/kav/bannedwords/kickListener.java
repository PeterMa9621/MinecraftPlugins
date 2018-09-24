/*** Eclipse Class Decompiler plugin, copyright (c) 2016 Chen Chao (cnfree2000@hotmail.com) ***/
package me.kav.bannedwords;

import java.util.List;
import me.kav.bannedwords.Main;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class kickListener implements Listener {
	Main plugin;

	public kickListener(Main instance) {
		this.plugin = instance;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onChat(AsyncPlayerChatEvent event) {
		final Player player = event.getPlayer();
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
							player.kickPlayer("§6[净化系统] §c你试图发送含非法的内容(§4"+bannedword+"§c).");
						}
					}).runTaskLater(this.plugin, 1L);
				}
			}
		}

	}
}