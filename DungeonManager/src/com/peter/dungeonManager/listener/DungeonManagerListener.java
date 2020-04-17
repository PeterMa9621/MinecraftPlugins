package com.peter.dungeonManager.listener;

import com.peter.dungeonManager.DungeonManager;
import com.peter.dungeonManager.config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class DungeonManagerListener implements Listener
{
	private DungeonManager plugin;

	public DungeonManagerListener(DungeonManager plugin)
	{
		this.plugin=plugin;
	}

	@EventHandler
	public void onPlayerUsingItem(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Action action = event.getAction();
		if(action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
			World world = player.getWorld();
			String worldName = world.getName().toLowerCase();
			if(worldName.contains("dxl_game_") && !player.isOp()) {
				ItemStack itemStack = event.getItem();
				if(itemStack!=null && ConfigManager.itemForbid.contains(itemStack.getType())) {
					//player.sendMessage(ChatColor.DARK_RED + "你不能在副本里使用这个物品!");
					event.setCancelled(true);
				}
			}
		}
	}

}
