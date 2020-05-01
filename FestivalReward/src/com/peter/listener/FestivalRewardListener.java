package com.peter.listener;

import com.peter.FestivalReward;
import com.peter.model.Festival;
import com.peter.model.FestivalPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import com.peter.util.Util;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

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
	private void onPlayerClickInventory(InventoryClickEvent event) throws SQLException {
		if(event.getView().getTitle().equalsIgnoreCase(Util.guiTitle)) {
			event.setCancelled(true);
			Player player = (Player)event.getWhoClicked();

			ItemStack clickedItem = event.getCurrentItem();
			String playerName = player.getName();
			String date = Util.getPersistentData(clickedItem, new NamespacedKey(plugin, "date"));
			if(date==null)
				return;
			FestivalPlayer festivalPlayer = plugin.festivalPlayerManager.getFestivalPlayer(player.getUniqueId());
			if(plugin.configManager.hasThisIpReceivedReward(festivalPlayer.getIp())) {
				ItemStack itemStack = Util.createItemStack(Material.PAPER, "§c你已经领过礼物了", 43, null);
				event.setCurrentItem(itemStack);
				return;
			}
			if (event.getRawSlot() == Util.festivalIndex) {
				Festival festival = plugin.festivalManager.getFestival(date);
				List<String> commands = festival.getCommands();
				Collections.shuffle(commands);
				for(int i=0; i<festival.getNumReward(); i++) {
					String command = commands.get(i);
					command = command.replace("%player%", playerName);
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
				}
				festivalPlayer.setReceiveDateToToday();
				plugin.configManager.savePlayerData(festivalPlayer);
				ItemStack itemStack = Util.createItemStack(Material.PAPER, "§6领取礼物成功", 43, null);
				event.setCurrentItem(itemStack);
			}
		}
	}
}
