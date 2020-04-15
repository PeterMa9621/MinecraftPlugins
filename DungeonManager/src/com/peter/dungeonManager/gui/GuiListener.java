package com.peter.dungeonManager.gui;

import com.peter.dungeonManager.DungeonManager;
import com.peter.dungeonManager.event.LeaveGroupEvent;
import com.peter.dungeonManager.model.DungeonGroup;
import com.peter.dungeonManager.model.DungeonPlayer;
import com.peter.dungeonManager.model.DungeonSetting;
import com.peter.dungeonManager.util.DataManager;
import com.peter.dungeonManager.util.GuiType;
import com.peter.dungeonManager.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
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

			switch (index) {
				case GuiManager.refreshIndex:
					if(guiType.equals(GuiType.Team)) {
						openJoinTeamGui(player);
					} else if (guiType.equals(GuiType.Dungeon)) {
						openCreateTeamGui(player);
					}
					return;
				case GuiManager.createTeamIndex:
					if(guiType.equals(GuiType.Team)) {
						openCreateTeamGui(player);
					} else if (guiType.equals(GuiType.Dungeon)) {
						openJoinTeamGui(player);
					}
					return;
				case GuiManager.nextPageIndex:
					dungeonPlayer.setCurrentJoinTeamViewPage(dungeonPlayer.getCurrentJoinTeamViewPage() + 1);
					openJoinTeamGui(player);
					return;
				case GuiManager.previousPageIndex:
					dungeonPlayer.setCurrentJoinTeamViewPage(dungeonPlayer.getCurrentJoinTeamViewPage() - 1);
					openJoinTeamGui(player);
					return;
				// Start dungeon
				case GuiManager.startDungeonIndex:
					DungeonGroup dungeonGroup = dungeonPlayer.getDungeonGroup();
					if(dungeonGroup!=null && dungeonGroup.isLeader(dungeonPlayer)){
						if(dungeonGroup.isSatisfyNumRequirement()) {
							DataManager.removeDungeonGroup(dungeonGroup);
							dungeonGroup.startGame(plugin);
							player.closeInventory();
						} else {
							DungeonSetting dungeonSetting = dungeonGroup.getDungeonSetting();
							int minPlayers = dungeonSetting.getMinPlayers();
							int maxPlayers = dungeonSetting.getMaxPlayers();
							showNotificationOnLore(itemStack, String.format(GuiManager.notSatisfyRequirementNotification, minPlayers, maxPlayers));
						}
					}
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
			if(dungeonGroup==null){
				showNotificationOnLore(itemStack, GuiManager.missGroupNotification);
				return;
			}
			// Leave team
			if(dungeonGroup.containsPlayer(dungeonPlayer)) {
				LeaveGroupEvent event = new LeaveGroupEvent(dungeonPlayer);
				Bukkit.getPluginManager().callEvent(event);

				if(!dungeonGroup.isLeader(dungeonPlayer))
					itemStack = GuiManager.createJoinIcon(dungeonGroup, dungeonPlayer);
				else {
					itemStack = null;
					inventory.setItem(GuiManager.startDungeonIndex, null);
				}
			}
			// Join team
			else {
				if(!dungeonGroup.isFull()) {
					if(dungeonGroup.addPlayer(dungeonPlayer)) {
						itemStack = GuiManager.createLeaveIcon(dungeonGroup, dungeonPlayer);
					} else {
						showNotificationOnLore(itemStack, GuiManager.minLevelNotSatisfy);
					}
				} else {
					showNotificationOnLore(itemStack, GuiManager.groupFullNotification);
				}
			}
			inventory.setItem(index, itemStack);
		} else {
			showNotificationOnLore(itemStack, GuiManager.duplicateGroupNotification);
		}
	}

	public void handleCreateTeamEvent(DungeonPlayer dungeonPlayer, ItemStack itemStack) {
		String dungeonName = Util.getPersistentData(itemStack, new NamespacedKey(plugin, "dungeonName"));

		if(!dungeonPlayer.isInDungeonGroup()) {
			DungeonSetting dungeonSetting = DataManager.dungeonGroupSetting.get(dungeonName);
			DungeonGroup dungeonGroup = new DungeonGroup(dungeonPlayer.getPlayer().getName(), dungeonName, dungeonSetting, dungeonPlayer);
			if(dungeonGroup.addPlayer(dungeonPlayer)) {
				DataManager.addDungeonGroup(dungeonGroup);
				GuiManager.openDungeonGui(dungeonPlayer.getPlayer(), GuiType.Team);
			} else {
				showNotificationOnLore(itemStack, GuiManager.minLevelNotSatisfy);
			}
		} else {
			showNotificationOnLore(itemStack, GuiManager.duplicateGroupNotification);
		}
	}

	private void showNotificationOnLore(ItemStack itemStack, String notification) {
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setLore(new ArrayList<String>() {{
			add(notification);
		}});
		itemStack.setItemMeta(itemMeta);
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
}
