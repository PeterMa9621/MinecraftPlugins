package com.peter.dungeonManager.model;

import com.peter.dungeonManager.DungeonManager;

public class DungeonSetting {
    private String dungeonName = "";
    private String displayName = "";
    private int minPlayers;
    private int maxPlayers;

    public DungeonSetting(String dungeonName, String displayName, int minPlayers, int maxPlayers) {
        this.dungeonName = dungeonName;
        this.displayName = displayName;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    public void setDungeonName(String dungeonName) {
        this.dungeonName = dungeonName;
    }

    public String getDungeonName() {
        return dungeonName;
    }
}
