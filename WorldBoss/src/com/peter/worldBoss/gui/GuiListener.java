package com.peter.worldBoss.gui;

import com.peter.worldBoss.WorldBoss;
import com.peter.worldBoss.manager.BossGroupManager;
import com.peter.worldBoss.model.BossGroup;
import com.peter.worldBoss.model.BossGroupSetting;
import com.peter.worldBoss.util.GroupResponse;
import com.peter.worldBoss.util.Util;
import org.bukkit.ChatColor;
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

import java.util.ArrayList;

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
				BossGroup bossGroup = BossGroupManager.bossGroups.get(groupName);
				ItemStack icon;
				if(bossGroup!=null) {
					BossGroupSetting setting = BossGroupManager.bossGroupSetting.get(bossGroup.getGroupName());
					GroupResponse groupResponse = BossGroupManager.joinGroup(player, bossGroup);
					if(groupResponse.equals(GroupResponse.joinSameGroup)){
						icon = GuiManager.createJoinIcon(bossGroup, setting);
					} else if(groupResponse.equals(GroupResponse.canJoin)) {
						icon = GuiManager.createLeaveIcon(bossGroup, setting.getDisplayName());
					} else {
						Util.setLoreForItem(itemStack, new ArrayList<String>() {{
							add(ChatColor.RED + "你已经加入一个队伍了!");
						}});
						icon = itemStack;
					}
				} else {
					Util.setLoreForItem(itemStack, new ArrayList<String>() {{
						add(ChatColor.RED + "这个活动" + ChatColor.GREEN + "已经" + ChatColor.RED + "开始了!");
					}});
					icon = itemStack;
				}


				event.getInventory().setItem(index, icon);
			}
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		BossGroupManager.leaveGroup(player);
		BossGroupManager.removePlayer(player);
	}
}
