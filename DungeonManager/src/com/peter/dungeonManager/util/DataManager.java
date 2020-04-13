package com.peter.dungeonManager.util;

import com.peter.dungeonManager.model.DungeonGroup;
import com.peter.dungeonManager.model.DungeonSetting;
import com.peter.dungeonManager.model.DungeonPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class DataManager {
    /**
     *  Key is the name of dungeons, value is the DungeonGroupSetting class
     */
    public static HashMap<String, DungeonSetting> dungeonGroupSetting = new HashMap<>();

    /**
     *  Key is the name of groups, value is the DungeonGroup class
     */
    private static HashMap<String, DungeonGroup> dungeonGroupHashMap = new HashMap<>();

    private static ArrayList<DungeonGroup> dungeonGroups = new ArrayList<>();

    public static HashMap<UUID, DungeonPlayer> dungeonPlayers = new HashMap<>();

    public static DungeonGroup getDungeonGroup(String groupName) {
        return dungeonGroupHashMap.get(groupName);
    }

    public static ArrayList<DungeonGroup> getDungeonGroups() {
        return dungeonGroups;
    }

    public static void addDungeonGroup(DungeonGroup dungeonGroup) {
        String groupName = dungeonGroup.getGroupName();
        if(!dungeonGroupHashMap.containsKey(groupName)){
            dungeonGroupHashMap.put(groupName, dungeonGroup);
            dungeonGroups.add(dungeonGroup);
        }
    }

    public static void removeDungeonGroup(DungeonGroup dungeonGroup) {
        String groupName = dungeonGroup.getGroupName();
        dungeonGroupHashMap.remove(groupName);
        dungeonGroups.remove(dungeonGroup);
    }

    public static String getDungeonDisplayName(String dungeonName) {
        return dungeonGroupSetting.get(dungeonName).getDisplayName();
    }

    public static DungeonPlayer getDungeonPlayer(UUID uuid) {
        return dungeonPlayers.get(uuid);
    }

    public static void removeDungeonPlayer(UUID uuid) {
        dungeonPlayers.remove(uuid);
    }
}
