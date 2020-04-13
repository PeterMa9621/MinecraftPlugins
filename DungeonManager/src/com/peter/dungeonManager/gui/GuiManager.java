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
    public static final String teamGuiTitle = "§2加入副本队伍";
    public static final String dungeonGuiTitle = "§2创建副本队伍";
    public static final String joinPrefix = "§6点击加入§5";
    public static final String leavePrefix = "§6点击退出§5";
    public static final String refreshTitle = "§3刷新";
    public static final String numPeopleLore = "§7已加入玩家:";
    public static final String nextPageTitle = "§f下一页";
    public static final String previousPageTitle = "§f上一页";
    public static final String createTeamTitle = "§f创建副本队伍";
    public static final String goBackTitle = "§f返回";
    public static final String missGroupNotification = ChatColor.RESET + "该队伍" + ChatColor.RED + "已解散" +
            ChatColor.RESET + "或" + ChatColor.GREEN + "已进入副本";
    public static final String duplicateGroupNotification = ChatColor.RED + "你已经加入一个队伍了";
    public static final String notSatisfyRequirementNotification = "§c人数要求不满足,最少:§2%d§c人,最多:§2%d§c人";
    public static final String groupFullNotification = ChatColor.RED + "人数已满";
    public static DungeonManager plugin;
    public static final int inventorySize = 54;
    public static final int maxDungeonGroupPerPage = 45;
    public static final int refreshIndex = 49;
    public static final int startDungeonIndex = 50;
    public static final int nextPageIndex = 53;
    public static final int previousPageIndex = 52;
    public static final int createTeamIndex = 45;

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

        ItemStack createDungeonIcon = createStartDungeonIcon(dungeonPlayer);
        inventory.setItem(startDungeonIndex, createDungeonIcon);

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
                icon = createLeaveIcon(dungeonGroup);
            } else {
                icon = createJoinIcon(dungeonGroup);
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

    public static ItemStack createJoinIcon(DungeonGroup dungeonGroup) {
        String groupDisplayName = dungeonGroup.getDungeonSetting().getDisplayName();
        ItemStack icon = Util.createItem(Material.DIAMOND_SWORD, joinPrefix + groupDisplayName);

        setDataForJoinLeaveIcon(dungeonGroup, icon);
        return icon;
    }

    public static ItemStack createLeaveIcon(DungeonGroup dungeonGroup) {
        String groupDisplayName = dungeonGroup.getDungeonSetting().getDisplayName();
        ItemStack icon = Util.createItem(Material.DIAMOND_SWORD, leavePrefix + groupDisplayName, 16);

        setDataForJoinLeaveIcon(dungeonGroup, icon);
        return icon;
    }

    public static ItemStack createStartDungeonIcon(DungeonPlayer dungeonPlayer) {
        DungeonGroup dungeonGroup = dungeonPlayer.getDungeonGroup();
        if(dungeonGroup!=null && dungeonGroup.isLeader(dungeonPlayer) && !dungeonPlayer.isWaitingForStart()){
            String groupDisplayName = dungeonGroup.getDungeonSetting().getDisplayName();
            return Util.createItem(Material.PAPER, ChatColor.RESET + "开始副本" + groupDisplayName, 33);
        }
        return null;
    }

    public static void setDataForJoinLeaveIcon(DungeonGroup dungeonGroup, ItemStack icon) {
        ArrayList<String> lore = new ArrayList<String>() {{
            add(ChatColor.RESET + "队伍: " + dungeonGroup.getGroupName());
            add(numPeopleLore + dungeonGroup.getPlayers().size());
            for(DungeonPlayer dungeonPlayer:dungeonGroup.getPlayers()) {
                add(ChatColor.RESET + "成员: " + dungeonPlayer.getPlayer().getDisplayName());
            }
            if(dungeonGroup.isFull())
                add(groupFullNotification);
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
