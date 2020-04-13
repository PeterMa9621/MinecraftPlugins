package com.peter.dungeonManager;

import org.bukkit.event.Listener;

public class DungeonManagerListener implements Listener
{
	private DungeonManager plugin;

	public DungeonManagerListener(DungeonManager plugin)
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
