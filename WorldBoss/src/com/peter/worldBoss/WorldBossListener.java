package com.peter.worldBoss;

import com.peter.worldBoss.model.BossGroup;
import de.erethon.dungeonsxl.player.DGroup;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;

public class WorldBossListener implements Listener
{
	private WorldBoss plugin;

	public WorldBossListener(WorldBoss plugin)
	{
		this.plugin=plugin;
	}

	/*
	@EventHandler
	public void onPlayerJoinBossGroup(DPlayerJoinDGroupEvent event) {
		DGroup group = event.getDGroup();
		Bukkit.getConsoleSender().sendMessage("Join A Group " + group.getRawName());
		if(plugin.bossGroupSetting.containsKey(group.getRawName())){
			String groupName = group.getRawName();
			Bukkit.getConsoleSender().sendMessage("Get A Boss Group " + groupName);
			if(!plugin.bossGroups.containsKey(groupName)){
				BossGroup bossGroup = new BossGroup(groupName, group);
				plugin.bossGroups.put(groupName, bossGroup);
				Bukkit.getConsoleSender().sendMessage("Group Size: " + bossGroup.getDGroup().getPlayers().size());
			}
		}
	}

	 */
}
