package com.peter.dungeonManager.util;

import com.peter.dungeonManager.DungeonManager;
import com.peter.dungeonManager.config.ConfigManager;
import com.peter.dungeonManager.model.DungeonGroup;
import com.peter.dungeonManager.model.DungeonSetting;
import com.peter.dungeonManager.model.DungeonPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class DataManager {
    /**
     *  Key is the name of dungeons, value is the DungeonGroupSetting class
     */
    public HashMap<String, DungeonSetting> dungeonGroupSetting = new HashMap<>();

    /**
     *  Key is the name of groups, value is the DungeonGroup class
     */
    private HashMap<String, DungeonGroup> dungeonGroupHashMap = new HashMap<>();

    private ArrayList<DungeonGroup> dungeonGroups = new ArrayList<>();

    public HashMap<UUID, DungeonPlayer> dungeonPlayers = new HashMap<>();

    private DungeonManager plugin;
    public DataManager(DungeonManager plugin) {
        this.plugin = plugin;
    }

    public DungeonGroup getDungeonGroup(String groupName) {
        return dungeonGroupHashMap.get(groupName);
    }

    public ArrayList<DungeonGroup> getDungeonGroups() {
        return dungeonGroups;
    }

    public void addDungeonGroup(DungeonGroup dungeonGroup) {
        String groupName = dungeonGroup.getGroupName();
        if(!dungeonGroupHashMap.containsKey(groupName)){
            dungeonGroupHashMap.put(groupName, dungeonGroup);
            dungeonGroups.add(dungeonGroup);
        }
    }

    public void removeDungeonGroup(DungeonGroup dungeonGroup) {
        String groupName = dungeonGroup.getGroupName();
        dungeonGroupHashMap.remove(groupName);
        dungeonGroups.remove(dungeonGroup);
    }

    public String getDungeonDisplayName(String dungeonName) {
        return dungeonGroupSetting.get(dungeonName).getDisplayName();
    }

    public DungeonPlayer getDungeonPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        DungeonPlayer dungeonPlayer = dungeonPlayers.get(uuid);
        if(dungeonPlayer==null){
            dungeonPlayer = new DungeonPlayer(player);
            dungeonPlayer.setDungeonTimestamps(plugin.configManager.loadPlayerDungeonTime(player));
            plugin.dataManager.dungeonPlayers.put(uuid, dungeonPlayer);
        }
        return dungeonPlayers.get(uuid);
    }

    public void removeDungeonPlayer(UUID uuid) {
        dungeonPlayers.remove(uuid);
    }
}
