package com.peter.util;

import com.peter.model.Festival;
import com.peter.model.FestivalPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import com.peter.FestivalReward;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Util {
    public final static int festivalIndex = 13;
    public final static String guiTitle = "§5§l节日礼包";

    public static void openGui(FestivalReward plugin, Player player) throws SQLException {
        if(!plugin.festivalManager.isFestival()) {
            player.sendMessage("§6今天不是节日，没有礼包哦！");
            return;
        }


        Inventory inventory = Bukkit.createInventory(player, 27, guiTitle);
        Festival festival = plugin.festivalManager.getFestival();
        FestivalPlayer festivalPlayer = plugin.festivalPlayerManager.getFestivalPlayer(player.getUniqueId());
        ItemStack festivalReward;
        if(!festivalPlayer.hasReceivedReward() && !plugin.configManager.hasThisIpReceivedReward(festivalPlayer.getIp())) {
            festivalReward = createItemStack(Material.PAPER, "§6点击领取" + festival.getFestivalName() + "§6节日礼物",
                    42, festival.getDescribe());
            setPersistentData(festivalReward, new NamespacedKey(plugin, "date"), festival.getDate());
        } else {
            festivalReward = createItemStack(Material.PAPER, "§c你已经领取过" + festival.getFestivalName() + "§c节日礼物",
                    43, null);
        }

        inventory.setItem(festivalIndex, festivalReward);
        player.openInventory(inventory);
    }

    public static ItemStack createItemStack(Material material, String displayName, int customModelId, List<String> lore) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(displayName);
        itemMeta.setLore(lore);
        itemMeta.setCustomModelData(customModelId);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static void setPersistentData(ItemStack item, NamespacedKey key, String value) {
        if(item==null)
            return;
        ItemMeta itemMeta = item.getItemMeta();
        if(itemMeta==null)
            return;
        itemMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, value);
        item.setItemMeta(itemMeta);
    }

    public static String getPersistentData(ItemStack item, NamespacedKey key) {
        if(item==null)
            return null;
        ItemMeta itemMeta = item.getItemMeta();
        if(itemMeta == null)
            return null;
        return itemMeta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
    }
}
