package com.peter.worldBoss.gui;

import com.peter.worldBoss.WorldBoss;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

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
			Player player = (Player) event.getWhoClicked();

		}
	}
}
