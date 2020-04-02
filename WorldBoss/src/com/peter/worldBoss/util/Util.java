package com.peter.worldBoss.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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

}
