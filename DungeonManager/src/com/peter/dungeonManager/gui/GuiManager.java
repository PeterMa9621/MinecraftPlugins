package com.peter.dungeonManager.gui;

import com.peter.dungeonManager.DungeonManager;
import com.peter.dungeonManager.manager.DungeonGroupManager;
import com.peter.dungeonManager.model.DungeonGroup;
import com.peter.dungeonManager.model.DungeonPlayer;
import com.peter.dungeonManager.model.DungeonSetting;
import com.peter.dungeonManager.model.GroupResponse;
import com.peter.dungeonManager.util.GuiType;
import com.peter.dungeonManager.util.Util;
import dps.rewardBox.Reward;
import dps.rewardBox.RewardTable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;

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
    public static final String minLevelNotification = "§7最小等级要求:§6%d";
    public static final String minLevelNotSatisfy = "§c你的等级不满足副本要求";
    public static final int inventorySize = 54;
    public static final int maxDungeonGroupPerPage = 45;
    public static final int refreshIndex = 49;
    public static final int startDungeonIndex = 50;
    public static final int nextPageIndex = 53;
    public static final int previousPageIndex = 52;
    public static final int createTeamIndex = 45;

    private DungeonManager plugin;
    public GuiManager(DungeonManager plugin) {
        this.plugin = plugin;
    }

    public Inventory createGui(Player player, GuiType guiType) {
        Inventory inventory;
        if(guiType.equals(GuiType.Group))
            inventory = Bukkit.createInventory(null, inventorySize, teamGuiTitle);
        else if(guiType.equals(GuiType.Dungeon))
            inventory = Bukkit.createInventory(null, inventorySize, dungeonGuiTitle);
        else
            inventory = null;

        DungeonPlayer dungeonPlayer = plugin.dataManager.getDungeonPlayer(player);

        generateItems(inventory, dungeonPlayer, guiType);

        return inventory;
    }

    /**
     *  Used to create icons of groups on this inventory
     * @param inventory Inventory
     * @param dungeonPlayer DungeonPlayer
     */
    public void generateItems(Inventory inventory, DungeonPlayer dungeonPlayer, GuiType guiType) {

        int currentViewPage = 0;
        int totalLength = 0;
        int restLength = 0;
        int firstIndex = 0;
        if(guiType.equals(GuiType.Group)) {
            currentViewPage = dungeonPlayer.getCurrentJoinTeamViewPage();
            firstIndex = maxDungeonGroupPerPage*currentViewPage;
            totalLength = plugin.dataManager.getDungeonGroups().size();
        } else if(guiType.equals(GuiType.Dungeon)) {
            currentViewPage = dungeonPlayer.getCurrentCreateTeamViewPage();
            firstIndex = maxDungeonGroupPerPage*currentViewPage;
            totalLength = plugin.dataManager.dungeonGroupSetting.size();
        }
        restLength = Math.min(totalLength - firstIndex, maxDungeonGroupPerPage);

        if(guiType.equals(GuiType.Group)) {
            putGroupInfoIntoInventory(restLength, firstIndex, dungeonPlayer, inventory);
        } else if(guiType.equals(GuiType.Dungeon)) {
            putDungeonInfoIntoInventory(firstIndex, inventory, dungeonPlayer);
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

    public void putGroupInfoIntoInventory(int restNumGroups, int firstIndex, DungeonPlayer dungeonPlayer, Inventory inventory) {
        int count = 0;
        ArrayList<DungeonGroup> dungeonGroups = plugin.dataManager.getDungeonGroups();
        for(int i=0; i<restNumGroups; i++){
            DungeonGroup dungeonGroup = dungeonGroups.get(firstIndex + i);

            ItemStack icon;
            if(dungeonGroup.containsPlayer(dungeonPlayer)){
                icon = createGroupIcon(dungeonGroup, dungeonPlayer, false);
            } else {
                icon = createGroupIcon(dungeonGroup, dungeonPlayer, true);
            }
            inventory.setItem(count, icon);

            count++;
        }

        ItemStack createTeam = Util.createItem(Material.PAPER, createTeamTitle, 29);
        inventory.setItem(createTeamIndex, createTeam);
    }

    public void putDungeonInfoIntoInventory(int firstIndex, Inventory inventory, DungeonPlayer dungeonPlayer) {
        Player player = dungeonPlayer.getPlayer();
        int count = 0;
        int i = 0;
        ArrayList<DungeonSetting> dungeonSettingList = new ArrayList<>(plugin.dataManager.dungeonGroupSetting.values());
        Collections.sort(dungeonSettingList);
        for(DungeonSetting dungeonSetting:dungeonSettingList){
            i++;
            if(i < firstIndex) {
                continue;
            }
            String dungeonName = dungeonSetting.getDungeonName();
            int minLevel = dungeonSetting.getMinLevel();
            ItemStack icon = dungeonSetting.getIcon();
            GroupResponse groupResponse = DungeonGroupManager.canCreateGroup(dungeonSetting, dungeonPlayer);
            ArrayList<String> lore = new ArrayList<String>() {{
                add(String.format(minLevelNotification, minLevel));
            }};
            if(!groupResponse.canCreateOrJoinGroup()) {
                icon = plugin.configManager.getLockIcon(dungeonSetting.getDisplayName());
                lore.addAll(groupResponse.getReason());
                Util.setPersistentData(icon, new NamespacedKey(plugin, "lock"), "lock");
            } else {
                if(player.isOp()) {
                    RewardTable rewardTable = DungeonManager.dpsAPI.getRewardTable(dungeonName);
                    int numReward = 1;
                    for(Reward reward:rewardTable.getRewards()) {
                        lore.add("§f" + numReward + ". " + reward.getIcon().getItemMeta().getDisplayName() + ", " + reward.getChance());
                        numReward ++;
                    }
                }
            }
            Util.setLoreForItem(icon, lore);
            Util.setPersistentData(icon,  new NamespacedKey(plugin, "dungeonName"), dungeonName);
            inventory.setItem(count, icon);
            count++;
        }

        ItemStack createTeam = Util.createItem(Material.PAPER, goBackTitle, 12);
        inventory.setItem(createTeamIndex, createTeam);
    }

    public void openDungeonGui(Player player, GuiType guiType) {
        player.openInventory(createGui(player, guiType));
    }

    public ItemStack createGroupIcon(DungeonGroup dungeonGroup, DungeonPlayer dungeonPlayer, Boolean isJoinIcon) {
        DungeonSetting dungeonSetting = dungeonGroup.getDungeonSetting();
        GroupResponse groupResponse = DungeonGroupManager.canJoinGroup(dungeonGroup, dungeonPlayer);
        ItemStack icon;
        String groupDisplayName = dungeonSetting.getDisplayName();
        ArrayList<String> lore = new ArrayList<>();
        if(!groupResponse.canCreateOrJoinGroup()) {
            icon = plugin.configManager.getLockIcon(groupDisplayName);
            lore.addAll(groupResponse.getReason());
            Util.setPersistentData(icon, new NamespacedKey(plugin, "lock"), "lock");
        } else {
            icon = dungeonSetting.getIcon();
        }

        ItemMeta itemMeta = icon.getItemMeta();
        if(isJoinIcon) {
            itemMeta.setDisplayName(joinPrefix + groupDisplayName);
            icon.setItemMeta(itemMeta);
        } else {
            icon = Util.createItem(Material.PAPER, leavePrefix + groupDisplayName, 40);
        }
        Util.setLoreForItem(icon, lore);
        setDataForJoinLeaveIcon(dungeonGroup, icon, dungeonPlayer);
        return icon;
    }

    public ItemStack createStartDungeonIcon(DungeonPlayer dungeonPlayer) {
        DungeonGroup dungeonGroup = dungeonPlayer.getDungeonGroup();
        if(dungeonGroup!=null && dungeonGroup.isLeader(dungeonPlayer) && !dungeonPlayer.isWaitingForStart()){
            String groupDisplayName = dungeonGroup.getDungeonSetting().getDisplayName();
            return Util.createItem(Material.PAPER, ChatColor.RESET + "开始副本" + groupDisplayName, 33);
        }
        return null;
    }

    public void setDataForJoinLeaveIcon(DungeonGroup dungeonGroup, ItemStack icon, DungeonPlayer dungeonPlayer) {
        ArrayList<String> lore = new ArrayList<String>() {{
            DungeonSetting dungeonSetting = dungeonGroup.getDungeonSetting();
            add(ChatColor.RESET + "队伍: " + dungeonGroup.getGroupName());
            add(numPeopleLore + dungeonGroup.getPlayers().size() + "/" + dungeonSetting.getMaxPlayers());
            for(DungeonPlayer dungeonPlayer:dungeonGroup.getPlayers()) {
                add(ChatColor.RESET + "成员: " + dungeonPlayer.getPlayer().getDisplayName());
            }
            // Check if this player can join the team
            if(dungeonGroup.isFull())
                add(groupFullNotification);
            if(!dungeonSetting.isSatisfyLevelRequirement(dungeonPlayer.getPlayer())){
                add(minLevelNotSatisfy);
            }
        }};

        Util.addLoreForItem(icon, lore);
        Util.setPersistentData(icon,  new NamespacedKey(plugin, "groupName"), dungeonGroup.getGroupName());
    }

    public ItemStack createNextPageIcon() {
        return Util.createItem(Material.PAPER, nextPageTitle, 4);
    }

    public ItemStack createPreviousPageIcon() {
        return Util.createItem(Material.PAPER, previousPageTitle, 2);
    }
}
