package com.peter.dungeonManager.gui;

import com.peter.dungeonManager.DungeonManager;
import com.peter.dungeonManager.model.DungeonGroup;
import com.peter.dungeonManager.model.DungeonSetting;
import com.peter.dungeonManager.model.DungeonPlayer;
import com.peter.dungeonManager.util.DataManager;
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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class GuiManager {
    public static final String teamGuiTitle = "��2���븱������";
    public static final String dungeonGuiTitle = "��2������������";
    public static final String joinPrefix = "��6��������5";
    public static final String leavePrefix = "��6����˳���5";
    public static final String refreshTitle = "��3ˢ��";
    public static final String numPeopleLore = "��7�Ѽ������:";
    public static final String nextPageTitle = "��f��һҳ";
    public static final String previousPageTitle = "��f��һҳ";
    public static final String createTeamTitle = "��f������������";
    public static final String goBackTitle = "��f����";
    public static final String missGroupNotification = ChatColor.RESET + "�ö���" + ChatColor.RED + "�ѽ�ɢ" +
            ChatColor.RESET + "��" + ChatColor.GREEN + "�ѽ��븱��";
    public static final String duplicateGroupNotification = ChatColor.RED + "���Ѿ�����һ��������";
    public static final String notSatisfyRequirementNotification = "��c����Ҫ������,����:��2%d��c��,���:��2%d��c��";
    public static final String groupFullNotification = ChatColor.RED + "��������";
    public static final String minLevelNotification = "��7��С�ȼ�Ҫ��:��6%d";
    public static final String minLevelNotSatisfy = "��c��ĵȼ������㸱��Ҫ��";
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

    public static void putTeamInfoIntoInventory(int restNumGroups, int firstIndex, DungeonPlayer dungeonPlayer, Inventory inventory) {
        int count = 0;
        ArrayList<DungeonGroup> dungeonGroups = DataManager.getDungeonGroups();
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

    public static void putDungeonInfoIntoInventory(int firstIndex, Inventory inventory, DungeonPlayer dungeonPlayer) {
        int count = 0;
        int i = 0;
        ArrayList<DungeonSetting> dungeonSettingList = new ArrayList<>(DataManager.dungeonGroupSetting.values());
        Collections.sort(dungeonSettingList);
        for(DungeonSetting dungeonSetting:dungeonSettingList){
            i++;
            if(i < firstIndex) {
                continue;
            }
            ItemStack icon = dungeonSetting.getIcon();
            Util.setLoreForItem(icon, new ArrayList<String>() {{
                int minLevel = dungeonSetting.getMinLevel();
                add(String.format(minLevelNotification, minLevel));
                Player player = dungeonPlayer.getPlayer();
                if(!dungeonSetting.isSatisfyLevelRequirement(player)) {
                    add(minLevelNotSatisfy);
                }
                // Useful info for ops
                if(player.isOp()) {
                    RewardTable rewardTable = DungeonManager.dpsAPI.getRewardTable(dungeonSetting.getDungeonName());
                    int i = 1;
                    for(Reward reward:rewardTable.getRewards()) {
                        add(i + ". " + reward.getIcon().getItemMeta().getDisplayName() + ", " + reward.getChance());
                        i ++;
                    }
                }
            }});
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

    public static ItemStack createGroupIcon(DungeonGroup dungeonGroup, DungeonPlayer dungeonPlayer, Boolean isJoinIcon) {
        DungeonSetting dungeonSetting = dungeonGroup.getDungeonSetting();
        String groupDisplayName = dungeonSetting.getDisplayName();
        ItemStack icon = dungeonSetting.getIcon();
        ItemMeta itemMeta = icon.getItemMeta();
        if(isJoinIcon) {
            itemMeta.setDisplayName(joinPrefix + groupDisplayName);
            icon.setItemMeta(itemMeta);
        } else {
            icon = Util.createItem(Material.PAPER, leavePrefix + groupDisplayName, 40);
        }

        setDataForJoinLeaveIcon(dungeonGroup, icon, dungeonPlayer);
        return icon;
    }

    public static ItemStack createStartDungeonIcon(DungeonPlayer dungeonPlayer) {
        DungeonGroup dungeonGroup = dungeonPlayer.getDungeonGroup();
        if(dungeonGroup!=null && dungeonGroup.isLeader(dungeonPlayer) && !dungeonPlayer.isWaitingForStart()){
            String groupDisplayName = dungeonGroup.getDungeonSetting().getDisplayName();
            return Util.createItem(Material.PAPER, ChatColor.RESET + "��ʼ����" + groupDisplayName, 33);
        }
        return null;
    }

    public static void setDataForJoinLeaveIcon(DungeonGroup dungeonGroup, ItemStack icon, DungeonPlayer dungeonPlayer) {
        ArrayList<String> lore = new ArrayList<String>() {{
            DungeonSetting dungeonSetting = dungeonGroup.getDungeonSetting();
            add(ChatColor.RESET + "����: " + dungeonGroup.getGroupName());
            add(numPeopleLore + dungeonGroup.getPlayers().size() + "/" + dungeonSetting.getMaxPlayers());
            for(DungeonPlayer dungeonPlayer:dungeonGroup.getPlayers()) {
                add(ChatColor.RESET + "��Ա: " + dungeonPlayer.getPlayer().getDisplayName());
            }
            if(dungeonGroup.isFull())
                add(groupFullNotification);
            if(!dungeonSetting.isSatisfyLevelRequirement(dungeonPlayer.getPlayer())){
                add(minLevelNotSatisfy);
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
