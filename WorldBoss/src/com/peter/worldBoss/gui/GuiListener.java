package com.peter.worldBoss.gui;

import com.peter.worldBoss.WorldBoss;
import com.peter.worldBoss.model.BossGroup;
import com.peter.worldBoss.model.BossGroupSetting;
import com.peter.worldBoss.util.Util;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class GuiListener implements Listener
{
	private WorldBoss plugin;

	public GuiListener(WorldBoss plugin)
	{
		this.plugin=plugin;
	}

	@EventHandler
	public void onPlayerOpenInventory(InventoryClickEvent event) {
		if(event.getView().getTitle().equalsIgnoreCase(GuiManager.GuiTitle)){
			event.setCancelled(true);
			Player player = (Player) event.getWhoClicked();

			int index = event.getRawSlot();

			if(index < GuiManager.inventorySize && index >= 0) {
				ItemStack itemStack = event.getCurrentItem();
				if(itemStack==null)
					return;

				if(index == GuiManager.refreshIndex){
					new BukkitRunnable() {
						@Override
						public void run() {
							GuiManager.openWorldBossGui(player);
						}
					}.runTaskLater(plugin, 1);
					return;
				}

				String groupName = Util.getPersistentData(itemStack, new NamespacedKey(plugin, "groupName"));
				BossGroup bossGroup = WorldBoss.bossGroups.get(groupName);
				ItemStack icon;
				BossGroupSetting setting = WorldBoss.bossGroupSetting.get(bossGroup.getGroupName());
				if(bossGroup.containsPlayer(player)){
					bossGroup.removePlayer(player);
					icon = GuiManager.createJoinIcon(bossGroup, setting);
				} else {
					bossGroup.addPlayer(player);
					icon = GuiManager.createLeaveIcon(bossGroup, setting.getDisplayName());
				}
				event.getInventory().setItem(index, icon);
			}
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		for(BossGroup bossGroup:WorldBoss.bossGroups.values()){
			if(bossGroup.containsPlayer(player))
				bossGroup.removePlayer(player);
		}
	}
}
