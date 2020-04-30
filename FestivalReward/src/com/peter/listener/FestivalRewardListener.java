package com.peter.listener;

import com.peter.FestivalReward;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import com.peter.util.Util;

import java.io.IOException;

public class FestivalRewardListener implements Listener
{
	private FestivalReward plugin;

	public FestivalRewardListener(FestivalReward plugin)
	{
		this.plugin=plugin;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		plugin.configManager.loadPlayerData(event.getPlayer());
	}

	@EventHandler
	private void onPlayerClickInventory(InventoryClickEvent event) throws IOException {
		if(event.getView().getTitle().contains(Util.guiTitle)) {
			event.setCancelled(true);
			Player player = (Player)event.getWhoClicked();

			ItemStack clickedItem = event.getCurrentItem();
			String command = "vexrmb kit %s %s %s";
			String playerName = player.getName();
			String kitName = Util.getPersistentData(clickedItem, new NamespacedKey(plugin, "kitName"));
			switch (event.getRawSlot()) {
				case Util.qqIndex:
					Bukkit.dispatchCommand(player, String.format(command, kitName, "Q", playerName));
					player.closeInventory();
					break;
				case Util.wechatIndex:
					Bukkit.dispatchCommand(player, String.format(command, kitName, "W", playerName));
					player.closeInventory();
					break;
				case Util.alipayIndex:
					Bukkit.dispatchCommand(player, String.format(command, kitName, "A", playerName));
					player.closeInventory();
					break;
				case Util.webIndex:
					String webCommand = "tellraw %s [\"\",{\"text\":\"�����֧����ҳ\",\"color\":\"red\",\"bold\":true,\"underlined\":false,\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://vexrmb.i8mc.cn/index.php/serverpay/index/id/2124.html\"}}]";
					webCommand = String.format(webCommand, playerName);
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), webCommand);
					player.closeInventory();
					break;
			}
		}
	}
}
