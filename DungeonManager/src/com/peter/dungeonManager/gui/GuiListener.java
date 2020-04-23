package com.peter.dungeonManager.gui;

import com.peter.dungeonManager.DungeonManager;
import com.peter.dungeonManager.event.LeaveGroupEvent;
import com.peter.dungeonManager.manager.DungeonGroupManager;
import com.peter.dungeonManager.model.DungeonGroup;
import com.peter.dungeonManager.model.DungeonPlayer;
import com.peter.dungeonManager.model.DungeonSetting;
import com.peter.dungeonManager.model.GroupResponse;
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
	public void onPlayerClickInventory(InventoryClickEvent event) {
		if(event.getView().getTitle().contains(GuiManager.teamGuiTitle)){
			event.setCancelled(true);
			Player player = (Player) event.getWhoClicked();
			int index = event.getRawSlot();
			ItemStack itemStack = event.getCurrentItem();
			Inventory inventory = event.getClickedInventory();
			handleEvent(player, index, itemStack, inventory, GuiType.Group);
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
		DungeonPlayer dungeonPlayer = plugin.dataManager.getDungeonPlayer(player);

		if(index < GuiManager.inventorySize && index >= 0) {
			if(itemStack==null)
				return;

			switch (index) {
				case GuiManager.refreshIndex:
					if(guiType.equals(GuiType.Group)) {
						openJoinTeamGui(player);
					} else if (guiType.equals(GuiType.Dungeon)) {
						openCreateTeamGui(player);
					}
					return;
				case GuiManager.createTeamIndex:
					if(guiType.equals(GuiType.Group)) {
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
							plugin.dataManager.removeDungeonGroup(dungeonGroup);
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

			if(guiType.equals(GuiType.Group))
				handleJoinGroupEvent(dungeonPlayer, index, itemStack, inventory);
			else if(guiType.equals(GuiType.Dungeon))
				handleCreateGroupEvent(dungeonPlayer, itemStack);
		}
	}

	public void handleJoinGroupEvent(DungeonPlayer dungeonPlayer, int index, ItemStack itemStack, Inventory inventory) {
		if(isLocked(itemStack)) {
			return;
		}
		String groupName = Util.getPersistentData(itemStack, new NamespacedKey(plugin, "groupName"));

		if((dungeonPlayer.isInDungeonGroup() &&
				dungeonPlayer.getDungeonGroup().getGroupName().equalsIgnoreCase(groupName)) || !dungeonPlayer.isInDungeonGroup()) {
			DungeonGroup dungeonGroup = plugin.dataManager.getDungeonGroup(groupName);
			if(dungeonGroup==null){
				showNotificationOnLore(itemStack, GuiManager.missGroupNotification);
				return;
			}
			// Leave group
			if(dungeonGroup.containsPlayer(dungeonPlayer)) {
				LeaveGroupEvent event = new LeaveGroupEvent(dungeonPlayer);
				Bukkit.getPluginManager().callEvent(event);

				if(!dungeonGroup.isLeader(dungeonPlayer))
					itemStack = plugin.guiManager.createGroupIcon(dungeonGroup, dungeonPlayer, true);
				else {
					itemStack = null;
					inventory.setItem(GuiManager.startDungeonIndex, null);
				}
			}
			// Join group
			else {
				GroupResponse groupResponse = DungeonGroupManager.canJoinGroup(dungeonGroup, dungeonPlayer);
				if(groupResponse.canCreateOrJoinGroup()) {
					dungeonGroup.addPlayer(dungeonPlayer);
					itemStack = plugin.guiManager.createGroupIcon(dungeonGroup, dungeonPlayer, false);
				} else {
					Util.setLoreForItem(itemStack, groupResponse.getReason());
				}
			}
			inventory.setItem(index, itemStack);
		} else {
			showNotificationOnLore(itemStack, GuiManager.duplicateGroupNotification);
		}
	}

	public void handleCreateGroupEvent(DungeonPlayer dungeonPlayer, ItemStack itemStack) {
		if(isLocked(itemStack)) {
			return;
		}
		String dungeonName = Util.getPersistentData(itemStack, new NamespacedKey(plugin, "dungeonName"));

		if(!dungeonPlayer.isInDungeonGroup()) {
			DungeonSetting dungeonSetting = plugin.dataManager.dungeonGroupSetting.get(dungeonName);

			//GroupResponse groupResponse = DungeonGroupManager.canCreateGroup(dungeonSetting, dungeonPlayer);
			DungeonGroup dungeonGroup = new DungeonGroup(dungeonPlayer.getPlayer().getName(), dungeonName, dungeonSetting, dungeonPlayer);
			dungeonGroup.addPlayer(dungeonPlayer);
			plugin.dataManager.addDungeonGroup(dungeonGroup);
			plugin.guiManager.openDungeonGui(dungeonPlayer.getPlayer(), GuiType.Group);
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
				plugin.guiManager.openDungeonGui(player, GuiType.Group);
			}
		}.runTaskLater(plugin, 1);
	}

	private void openCreateTeamGui(Player player) {
		new BukkitRunnable() {
			@Override
			public void run() {
				plugin.guiManager.openDungeonGui(player, GuiType.Dungeon);
			}
		}.runTaskLater(plugin, 1);
	}

	private boolean isLocked(ItemStack itemStack) {
		String lockInfo = Util.getPersistentData(itemStack, new NamespacedKey(plugin, "lock"));
		if(lockInfo!=null && lockInfo.equalsIgnoreCase("lock"))
			return true;
		return false;
	}
}
