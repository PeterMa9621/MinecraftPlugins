package com.peter.dungeonManager.gui;

import com.peter.dungeonManager.DungeonManager;
import com.peter.dungeonManager.model.DungeonGroup;
import com.peter.dungeonManager.model.DungeonPlayer;
import com.peter.dungeonManager.model.DungeonSetting;
import com.peter.dungeonManager.util.DataManager;
import com.peter.dungeonManager.util.GuiType;
import com.peter.dungeonManager.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class GuiListener implements Listener
{
	private DungeonManager plugin;

	public GuiListener(DungeonManager plugin)
	{
		this.plugin=plugin;
	}

	@EventHandler
	public void onPlayerOpenInventory(InventoryClickEvent event) {
		if(event.getView().getTitle().contains(GuiManager.teamGuiTitle)){
			event.setCancelled(true);
			Player player = (Player) event.getWhoClicked();
			int index = event.getRawSlot();
			ItemStack itemStack = event.getCurrentItem();
			Inventory inventory = event.getClickedInventory();
			handleEvent(player, index, itemStack, inventory, GuiType.Team);
		} else if(event.getView().getTitle().contains(GuiManager.dungeonGuiTitle)){
			event.setCancelled(true);
			Player player = (Player) event.getWhoClicked();
			int index = event.getRawSlot();
			ItemStack itemStack = event.getCurrentItem();
			Inventory inventory = event.getClickedInventory();
			handleEvent(player, index, itemStack, inventory, GuiType.Dungeon);
		}
	}

	public void handleEvent(Player player, int index, ItemStack itemStack, Inventory inventory, GuiType guiType) {
		DungeonPlayer dungeonPlayer = DataManager.getDungeonPlayer(player.getUniqueId());

		if(index < GuiManager.inventorySize && index >= 0) {
			if(itemStack==null)
				return;

			if(index == GuiManager.refreshIndex) {
				if(guiType.equals(GuiType.Team)) {
					openJoinTeamGui(player);
				} else if (guiType.equals(GuiType.Dungeon)) {
					openCreateTeamGui(player);
				}
				return;
			}

			if(index == GuiManager.createTeamIndex) {
				if(guiType.equals(GuiType.Team)) {
					openCreateTeamGui(player);
				} else if (guiType.equals(GuiType.Dungeon)) {
					openJoinTeamGui(player);
				}
				return;
			}

			if(index == GuiManager.nextPageIndex) {
				dungeonPlayer.setCurrentJoinTeamViewPage(dungeonPlayer.getCurrentJoinTeamViewPage() + 1);
				openJoinTeamGui(player);
				return;
			}

			if(index == GuiManager.previousPageIndex) {
				dungeonPlayer.setCurrentJoinTeamViewPage(dungeonPlayer.getCurrentJoinTeamViewPage() - 1);
				openJoinTeamGui(player);
				return;
			}

			if(guiType.equals(GuiType.Team))
				handleJoinTeamEvent(dungeonPlayer, index, itemStack, inventory);
			else if(guiType.equals(GuiType.Dungeon))
				handleCreateTeamEvent(dungeonPlayer, itemStack);
		}
	}

	public void handleJoinTeamEvent(DungeonPlayer dungeonPlayer, int index, ItemStack itemStack, Inventory inventory) {
		String groupName = Util.getPersistentData(itemStack, new NamespacedKey(plugin, "groupName"));

		if((dungeonPlayer.isInDungeonGroup() &&
				dungeonPlayer.getDungeonGroup().getGroupName().equalsIgnoreCase(groupName)) || !dungeonPlayer.isInDungeonGroup()) {
			DungeonGroup dungeonGroup = DataManager.getDungeonGroup(groupName);
			DungeonSetting dungeonSetting = dungeonGroup.getDungeonSetting();

			ItemStack icon;
			// Leave team
			if(dungeonGroup.containsPlayer(dungeonPlayer)) {
				if(dungeonGroup.isLeader(dungeonPlayer)) {
					dungeonGroup.disband();
					DataManager.removeDungeonGroup(dungeonGroup);
					icon = null;
				} else {
					dungeonGroup.removePlayer(dungeonPlayer);
					icon = GuiManager.createJoinIcon(dungeonGroup, dungeonSetting.getDisplayName());
				}
			}
			// Join team
			else {
				dungeonGroup.addPlayer(dungeonPlayer);
				icon = GuiManager.createLeaveIcon(dungeonGroup, dungeonSetting.getDisplayName());
			}
			inventory.setItem(index, icon);
		} else {
			ItemMeta itemMeta = itemStack.getItemMeta();
			itemMeta.setLore(new ArrayList<String>() {{
				add(ChatColor.RED + "你已经加入一个队伍了");
			}});
			itemStack.setItemMeta(itemMeta);
		}
	}

	public void handleCreateTeamEvent(DungeonPlayer dungeonPlayer, ItemStack itemStack) {
		String dungeonName = Util.getPersistentData(itemStack, new NamespacedKey(plugin, "dungeonName"));

		if(!dungeonPlayer.isInDungeonGroup()) {
			DungeonSetting dungeonSetting = DataManager.dungeonGroupSetting.get(dungeonName);
			DungeonGroup dungeonGroup = new DungeonGroup(dungeonPlayer.getPlayer().getName(), dungeonName, dungeonSetting, dungeonPlayer);
			dungeonGroup.addPlayer(dungeonPlayer);
			DataManager.addDungeonGroup(dungeonGroup);
			GuiManager.openDungeonGui(dungeonPlayer.getPlayer(), GuiType.Team);
		} else {
			ItemMeta itemMeta = itemStack.getItemMeta();
			itemMeta.setLore(new ArrayList<String>() {{
				add(ChatColor.RED + "你已经加入一个队伍了");
			}});
			itemStack.setItemMeta(itemMeta);
		}
	}

	private void openJoinTeamGui(Player player) {
		new BukkitRunnable() {
			@Override
			public void run() {
				GuiManager.openDungeonGui(player, GuiType.Team);
			}
		}.runTaskLater(plugin, 1);
	}

	private void openCreateTeamGui(Player player) {
		new BukkitRunnable() {
			@Override
			public void run() {
				GuiManager.openDungeonGui(player, GuiType.Dungeon);
			}
		}.runTaskLater(plugin, 1);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		for(DungeonGroup dungeonGroup : DataManager.getDungeonGroups()){
			dungeonGroup.removePlayer(player);
		}
	}
}
