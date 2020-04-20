package com.peter.dungeonManager.model;

import com.peter.dungeonManager.DungeonManager;
import com.peter.dungeonManager.util.Util;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DungeonSetting implements Comparable<DungeonSetting> {
    private String dungeonName = "";
    private String displayName = "";
    private int minPlayers;
    private int maxPlayers;
    private int minLevel;
    private ItemStack icon = null;

    public DungeonSetting(String dungeonName, String displayName, int minPlayers, int maxPlayers, int minLevel) {
        this.dungeonName = dungeonName;
        this.displayName = displayName;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.minLevel = minLevel;
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

    public int getMinLevel() {
        return minLevel;
    }

    public void setIcon(ItemStack icon) {
        this.icon = icon;
    }

    public ItemStack getIcon() {
        if(icon==null){
            icon = Util.createItem(Material.getMaterial("DIAMOND_SWORD"), "§f创建" + displayName + "§f的队伍");
        }
        return icon;
    }

    public Boolean isSatisfyLevelRequirement(Player player) {
        int playerLevel = DungeonManager.levelSystemAPI.getLevel(player);
        if(playerLevel < minLevel) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(DungeonSetting dungeonSetting) {
        return minLevel - dungeonSetting.getMinLevel();
    }
}
