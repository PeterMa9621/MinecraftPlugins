package com.peter.dungeonManager.util;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class Util {
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

    public static void addLoreForItem(ItemStack item, ArrayList<String> lore) {
        ItemMeta itemMeta = item.getItemMeta();
        List<String> originLore = itemMeta.getLore();
        if (originLore==null) {
            originLore = new ArrayList<>();
        }
        originLore.addAll(lore);
        itemMeta.setLore(originLore);
        item.setItemMeta(itemMeta);
    }

    public static void setPersistentData(ItemStack item, NamespacedKey key, String value) {
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, value);
        item.setItemMeta(itemMeta);
    }

    public static String getPersistentData(ItemStack item, NamespacedKey key) {
        ItemMeta itemMeta = item.getItemMeta();
        if(itemMeta == null)
            return null;
        return itemMeta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
    }

    /**
     * Get a diff between two dates
     * @param date1 the oldest date
     * @param date2 the newest date
     * @param timeUnit the unit in which you want the diff
     * @return the diff value, in the provided unit
     */
    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
    }

    public static Map<TimeUnit,Long> computeDiff(Date date1, Date date2) {

        long diffInMillies = date2.getTime() - date1.getTime();

        //create the list
        List<TimeUnit> units = new ArrayList<TimeUnit>(EnumSet.allOf(TimeUnit.class));
        Collections.reverse(units);

        //create the result map of TimeUnit and difference
        Map<TimeUnit,Long> result = new LinkedHashMap<TimeUnit,Long>();
        long milliesRest = diffInMillies;

        for ( TimeUnit unit : units ) {

            //calculate difference in millisecond
            long diff = unit.convert(milliesRest,TimeUnit.MILLISECONDS);
            long diffInMilliesForUnit = unit.toMillis(diff);
            milliesRest = milliesRest - diffInMilliesForUnit;

            //put the result in the map
            result.put(unit,diff);
        }

        return result;
    }
}
