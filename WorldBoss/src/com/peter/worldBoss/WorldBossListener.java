package com.peter.worldBoss;

import de.erethon.dungeonsxl.event.dplayer.DPlayerJoinDGroupEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class WorldBossListener implements Listener
{
	private WorldBoss plugin;
	
	public WorldBossListener(WorldBoss plugin)
	{
		this.plugin=plugin;
	}

	@EventHandler
	public void onDungeonGroupCreated(DPlayerJoinDGroupEvent event) {
		Bukkit.getConsoleSender().sendMessage("Join Group Event");
		Bukkit.getConsoleSender().sendMessage(event.getDGroup().getName());
		event.getDGroup().setDungeon("boss");
		Bukkit.getConsoleSender().sendMessage(event.getDGroup().getDungeon().getName());
		//event.getDGroup().startGame();
	}
}
