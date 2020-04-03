package com.peter.worldBoss.util;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Objects;

public class Util {
    public static void getItemInfoInMainHand(Player p){
        ItemStack itemStack = p.getInventory().getItemInMainHand();
        p.sendMessage("Type: " + itemStack.getType().toString());
        p.sendMessage("Durability: " + itemStack.getDurability());
        if(itemStack.hasItemMeta()){
            ItemMeta itemMeta = itemStack.getItemMeta();
            assert itemMeta != null;
            if(itemMeta.hasCustomModelData()){
                int customModelData = itemMeta.getCustomModelData();
                p.sendMessage("Custom Model Data: " + customModelData);
            } else {
                p.sendMessage("Np Custom Model Data");
            }

            p.sendMessage("Name: " + itemMeta.getDisplayName());
            for(String lore: Objects.requireNonNull(itemMeta.getLore())){
                p.sendMessage("Lore: " + lore);
            }
        }
    }

    public static void createA() {

    }

    public static ItemStack createItem(Material material, String displayName, int customModelData) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(displayName);
        itemMeta.setCustomModelData(customModelData);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack createItem(Material material, String displayName) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(displayName);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static void setLoreForItem(ItemStack item, ArrayList<String> lore) {
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
    }

    public static void setPersistentData(ItemStack item, NamespacedKey key, String value) {
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, value);
        item.setItemMeta(itemMeta);
    }

    public static String getPersistentData(ItemStack item, NamespacedKey key) {
        ItemMeta itemMeta = item.getItemMeta();
        return itemMeta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
    }
}
