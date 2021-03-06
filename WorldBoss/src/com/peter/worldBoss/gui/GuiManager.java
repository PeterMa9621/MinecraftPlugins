package com.peter.worldBoss.gui;

import com.peter.worldBoss.WorldBoss;
import com.peter.worldBoss.config.ConfigManager;
import com.peter.worldBoss.manager.BossGroupManager;
import com.peter.worldBoss.model.BossGroup;
import com.peter.worldBoss.model.BossGroupSetting;
import com.peter.worldBoss.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class GuiManager {
    public static String GuiTitle = "§2加入世界BOSS活动";
    public static String joinPrefix = "§6点击加入§5";
    public static String leavePrefix = "§6点击退出§5";
    public static String refreshTitle = "§3刷新";
    public static String numPeopleLore = "§7已加入玩家:";
    public static WorldBoss plugin;
    public static int inventorySize = 27;
    public static int refreshIndex = 22;

    public static Inventory createGui(Player player) {
        Inventory inventory = Bukkit.createInventory(null, inventorySize, GuiTitle);
        int count = 0;
        for(BossGroupSetting setting: BossGroupManager.bossGroupSetting.values()){
            if(setting.isComingSoon()){
                String groupName = setting.getGroupName();
                BossGroup bossGroup = BossGroupManager.bossGroups.get(groupName);

                if(bossGroup == null){
                    bossGroup = new BossGroup(groupName);
                    BossGroupManager.bossGroups.put(groupName, bossGroup);
                }

                ItemStack icon;
                if(bossGroup.containsPlayer(player)){
                    icon = createLeaveIcon(bossGroup, setting.getDisplayName());
                } else {
                    icon = createJoinIcon(bossGroup, setting);
                }
                inventory.setItem(count, icon);
                count++;
            }
        }

        ItemStack refresh = Util.createItem(Material.PAPER, refreshTitle, 19);
        inventory.setItem(refreshIndex, refresh);

        return inventory;
    }

    public static void openWorldBossGui(Player player) {
        player.openInventory(createGui(player));
    }

    public static ItemStack createJoinIcon(BossGroup bossGroup, BossGroupSetting setting) {
        ItemStack icon = setting.getIcon();
        Util.setDisplayName(icon, joinPrefix + setting.getDisplayName());

        ArrayList<String> lore = new ArrayList<String>() {{
            add(numPeopleLore + bossGroup.getPlayers().size());
        }};
        Util.setLoreForItem(icon, lore);
        Util.setPersistentData(icon,  new NamespacedKey(plugin, "groupName"), bossGroup.getGroupName());
        return icon;
    }

    public static ItemStack createLeaveIcon(BossGroup bossGroup, String groupDisplayName) {
        ItemStack icon = ConfigManager.leaveGroupIcon;
        Util.setDisplayName(icon, leavePrefix + groupDisplayName);

        ArrayList<String> lore = new ArrayList<String>() {{
            add(numPeopleLore + bossGroup.getPlayers().size());
        }};
        Util.setLoreForItem(icon, lore);
        Util.setPersistentData(icon,  new NamespacedKey(plugin, "groupName"), bossGroup.getGroupName());
        return icon;
    }
}
