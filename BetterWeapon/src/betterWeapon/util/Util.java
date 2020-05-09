package betterWeapon.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.SplittableRandom;

public class Util {
    private static SplittableRandom random = new SplittableRandom();
    public static int getRandomInt(int max) {
        return random.nextInt(max);
    }

    public static int getLoreIndex(ItemStack itemStack) {
        int index = 0;
        if(!itemStack.hasItemMeta())
            return index;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(!itemMeta.hasLore())
            return index;
        List<String> lore = itemMeta.getLore();

        Boolean isWeapon = isWeapon(itemStack);
        Boolean isArmor = isArmor(itemStack);

        for(int i=0; i<lore.size(); i++) {
            String eachLore = lore.get(i);
            if(isWeapon) {
                if(eachLore.contains("攻击速度")) {
                    index = i;
                    break;
                }
            } else if(isArmor) {
                if(eachLore.contains("等级要求")) {
                    index = i;
                    break;
                }
            }
        }
        return index;
    }

    public static Boolean isSword(ItemStack itemStack) {
        return itemStack.getType().equals(Material.STONE_SWORD) || itemStack.getType().equals(Material.DIAMOND_SWORD) ||
                itemStack.getType().equals(Material.GOLDEN_SWORD) || itemStack.getType().equals(Material.IRON_SWORD) ||
                itemStack.getType().equals(Material.WOODEN_SWORD);
    }

    public static Boolean isAxe(ItemStack itemStack) {
        return itemStack.getType().equals(Material.STONE_AXE) || itemStack.getType().equals(Material.DIAMOND_AXE) ||
                itemStack.getType().equals(Material.GOLDEN_AXE) || itemStack.getType().equals(Material.IRON_AXE) ||
                itemStack.getType().equals(Material.WOODEN_AXE);
    }

    public static Boolean isWeapon(ItemStack itemStack) {
        return isSword(itemStack) || isAxe(itemStack);
    }

    public static Boolean isBoots(ItemStack itemStack) {
        return itemStack.getType().equals(Material.LEATHER_BOOTS) || itemStack.getType().equals(Material.CHAINMAIL_BOOTS) ||
                itemStack.getType().equals(Material.DIAMOND_BOOTS) || itemStack.getType().equals(Material.GOLDEN_BOOTS) ||
                itemStack.getType().equals(Material.IRON_BOOTS);
    }

    public static Boolean isLeggings(ItemStack itemStack) {
        return itemStack.getType().equals(Material.LEATHER_LEGGINGS) || itemStack.getType().equals(Material.CHAINMAIL_LEGGINGS) ||
                itemStack.getType().equals(Material.DIAMOND_LEGGINGS) || itemStack.getType().equals(Material.GOLDEN_LEGGINGS) ||
                itemStack.getType().equals(Material.IRON_LEGGINGS);
    }

    public static Boolean isChest(ItemStack itemStack) {
        return itemStack.getType().equals(Material.LEATHER_CHESTPLATE) || itemStack.getType().equals(Material.CHAINMAIL_CHESTPLATE) ||
                itemStack.getType().equals(Material.DIAMOND_CHESTPLATE) || itemStack.getType().equals(Material.GOLDEN_CHESTPLATE) ||
                itemStack.getType().equals(Material.IRON_CHESTPLATE);
    }

    public static Boolean isHelmet(ItemStack itemStack) {
        return itemStack.getType().equals(Material.LEATHER_HELMET) || itemStack.getType().equals(Material.CHAINMAIL_HELMET) ||
                itemStack.getType().equals(Material.DIAMOND_HELMET) || itemStack.getType().equals(Material.GOLDEN_HELMET) ||
                itemStack.getType().equals(Material.IRON_HELMET);
    }

    public static Boolean isArmor(ItemStack itemStack) {
        return isBoots(itemStack) || isLeggings(itemStack) || isChest(itemStack) || isHelmet(itemStack);
    }

    public static void setPersistentData(ItemStack item, NamespacedKey key, String value) {
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, value);
        item.setItemMeta(itemMeta);
    }

    public static String getPersistentData(ItemStack item, NamespacedKey key) {
        if(item==null)
            return null;
        ItemMeta itemMeta = item.getItemMeta();
        if(itemMeta==null)
            return null;
        if(itemMeta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
            return itemMeta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
        }
        return null;
    }
}
