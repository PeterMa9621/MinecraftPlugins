package com.peter.dungeonManager.gui;

import com.peter.dungeonManager.DungeonManager;
import com.peter.dungeonManager.model.DungeonGroup;
import com.peter.dungeonManager.model.DungeonSetting;
import com.peter.dungeonManager.model.DungeonPlayer;
import com.peter.dungeonManager.util.DataManager;
import com.peter.dungeonManager.util.GuiType;
import com.peter.dungeonManager.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;

public class GuiManager {
    public static String teamGuiTitle = "§2加入副本队伍";
    public static String dungeonGuiTitle = "§2创建副本队伍";
    public static String joinPrefix = "§6点击加入§5";
    public static String leavePrefix = "§6点击退出§5";
    public static String refreshTitle = "§3刷新";
    public static String numPeopleLore = "§7已加入玩家:";
    public static String nextPageTitle = "§f下一页";
    public static String previousPageTitle = "§f上一页";
    public static String createTeamTitle = "§f创建副本队伍";
    public static String goBackTitle = "§f返回";
    public static DungeonManager plugin;
    public static int inventorySize = 54;
    public static int maxDungeonGroupPerPage = 45;
    public static int refreshIndex = 49;
    public static int nextPageIndex = 53;
    public static int previousPageIndex = 52;
    public static int createTeamIndex = 45;

    public static Inventory createGui(Player player, GuiType guiType) {
        Inventory inventory;
        if(guiType.equals(GuiType.Team))
            inventory = Bukkit.createInventory(null, inventorySize, teamGuiTitle);
        else if(guiType.equals(GuiType.Dungeon))
            inventory = Bukkit.createInventory(null, inventorySize, dungeonGuiTitle);
        else
            inventory = null;

        DungeonPlayer dungeonPlayer = DataManager.dungeonPlayers.get(player.getUniqueId());
        if(dungeonPlayer==null){
            dungeonPlayer = new DungeonPlayer(player);
            DataManager.dungeonPlayers.put(player.getUniqueId(), dungeonPlayer);
        }

        generateItems(inventory, dungeonPlayer, guiType);

        return inventory;
    }

    /**
     *  Used to create icons of groups on this inventory
     * @param inventory Inventory
     * @param dungeonPlayer DungeonPlayer
     */
    public static void generateItems(Inventory inventory, DungeonPlayer dungeonPlayer, GuiType guiType) {

        int currentViewPage = 0;
        int totalLength = 0;
        int restLength = 0;
        int firstIndex = 0;
        if(guiType.equals(GuiType.Team)) {
            currentViewPage = dungeonPlayer.getCurrentJoinTeamViewPage();
            firstIndex = maxDungeonGroupPerPage*currentViewPage;
            totalLength = DataManager.getDungeonGroups().size();
        } else if(guiType.equals(GuiType.Dungeon)) {
            currentViewPage = dungeonPlayer.getCurrentCreateTeamViewPage();
            firstIndex = maxDungeonGroupPerPage*currentViewPage;
            totalLength = DataManager.dungeonGroupSetting.size();
        }
        restLength = Math.min(totalLength - firstIndex, maxDungeonGroupPerPage);

        if(guiType.equals(GuiType.Team)) {
            putTeamInfoIntoInventory(restLength, firstIndex, dungeonPlayer, inventory);
        } else if(guiType.equals(GuiType.Dungeon)) {
            putDungeonInfoIntoInventory(firstIndex, inventory);
        }

        ItemStack refresh = Util.createItem(Material.PAPER, refreshTitle, 19);
        inventory.setItem(refreshIndex, refresh);

        if(totalLength > firstIndex + restLength) {
            ItemStack next = createNextPageIcon();
            inventory.setItem(nextPageIndex, next);
        }

        if(currentViewPage > 0) {
            ItemStack previous = createPreviousPageIcon();
            inventory.setItem(previousPageIndex, previous);
        }
    }

    public static void putTeamInfoIntoInventory(int restNumGroups, int firstIndex, DungeonPlayer dungeonPlayer, Inventory inventory) {
        int count = 0;
        ArrayList<DungeonGroup> dungeonGroups = DataManager.getDungeonGroups();
        for(int i=0; i<restNumGroups; i++){
            DungeonGroup dungeonGroup = dungeonGroups.get(firstIndex + i);
            DungeonSetting dungeonSetting = dungeonGroup.getDungeonSetting();

            ItemStack icon;
            if(dungeonGroup.containsPlayer(dungeonPlayer)){
                icon = createLeaveIcon(dungeonGroup, dungeonSetting.getDisplayName());
            } else {
                icon = createJoinIcon(dungeonGroup, dungeonSetting.getDisplayName());
            }
            inventory.setItem(count, icon);

            count++;
        }

        ItemStack createTeam = Util.createItem(Material.PAPER, createTeamTitle, 29);
        inventory.setItem(createTeamIndex, createTeam);
    }

    public static void putDungeonInfoIntoInventory(int firstIndex, Inventory inventory) {
        int count = 0;
        int i = 0;
        for(DungeonSetting dungeonSetting:DataManager.dungeonGroupSetting.values()){
            i++;
            if(i < firstIndex) {
                continue;
            }
            String displayName = ChatColor.RESET + "创建副本" + dungeonSetting.getDisplayName() + ChatColor.RESET + "队伍";
            ItemStack icon = Util.createItem(Material.FEATHER, displayName, 29);
            Util.setPersistentData(icon,  new NamespacedKey(plugin, "dungeonName"), dungeonSetting.getDungeonName());
            inventory.setItem(count, icon);
            count++;
        }

        ItemStack createTeam = Util.createItem(Material.PAPER, goBackTitle, 12);
        inventory.setItem(createTeamIndex, createTeam);
    }

    public static void openDungeonGui(Player player, GuiType guiType) {
        player.openInventory(createGui(player, guiType));
    }

    public static ItemStack createJoinIcon(DungeonGroup dungeonGroup, String groupDisplayName) {
        ItemStack icon = Util.createItem(Material.DIAMOND_SWORD, joinPrefix + groupDisplayName);

        setDataForJoinLeaveIcon(dungeonGroup, icon);
        return icon;
    }

    public static ItemStack createLeaveIcon(DungeonGroup dungeonGroup, String groupDisplayName) {
        ItemStack icon = Util.createItem(Material.DIAMOND_SWORD, leavePrefix + groupDisplayName, 16);

        setDataForJoinLeaveIcon(dungeonGroup, icon);
        return icon;
    }

    public static void setDataForJoinLeaveIcon(DungeonGroup dungeonGroup, ItemStack icon) {
        ArrayList<String> lore = new ArrayList<String>() {{
            add(ChatColor.RESET + "队伍: " + dungeonGroup.getGroupName());
            add(numPeopleLore + dungeonGroup.getPlayers().size());
            for(DungeonPlayer dungeonPlayer:dungeonGroup.getPlayers()) {
                add(ChatColor.RESET + "成员: " + dungeonPlayer.getPlayer().getDisplayName());
            }
        }};
        Util.setLoreForItem(icon, lore);
        Util.setPersistentData(icon,  new NamespacedKey(plugin, "groupName"), dungeonGroup.getGroupName());
    }

    public static ItemStack createNextPageIcon() {
        return Util.createItem(Material.PAPER, nextPageTitle, 4);
    }

    public static ItemStack createPreviousPageIcon() {
        return Util.createItem(Material.PAPER, previousPageTitle, 2);
    }
}
