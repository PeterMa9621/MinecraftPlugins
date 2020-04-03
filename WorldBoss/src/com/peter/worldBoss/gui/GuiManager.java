package com.peter.worldBoss.gui;

import com.peter.worldBoss.WorldBoss;
import com.peter.worldBoss.model.BossGroupSetting;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GuiManager {
    public static String GuiTitle = "§2加入世界BOSS活动";

    public static void openWorldBossGui(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 27, GuiTitle);
        int count = 0;
        for(BossGroupSetting setting: WorldBoss.bossGroupSetting.values()){
            if(setting.isComingSoon()){
                String groupName = setting.getGroupName();

                ItemStack icon = new ItemStack(Material.DIAMOND_SWORD);
                ItemMeta itemMeta = icon.getItemMeta();
                itemMeta.setDisplayName("§6加入§5" + setting.getDisplayName());
                icon.setItemMeta(itemMeta);
                inventory.setItem(count, icon);
            }
        }
    }
}
