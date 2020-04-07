package peterHelper.util;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
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

    public static String getShortItemInfoInMainHand(Player p){
        ItemStack itemStack = p.getInventory().getItemInMainHand();
        String info = "Type: " + itemStack.getType().toString() + ",";

        if(itemStack.hasItemMeta()){
            ItemMeta itemMeta = itemStack.getItemMeta();
            assert itemMeta != null;
            if(itemMeta.hasCustomModelData()){
                info += "Model: " + itemMeta.getCustomModelData();
            } else {
                info += "No Model";
            }
        }
        return info;
    }

    public static String getArmorInfo(Player p){
        AttributeInstance armorAttribute = p.getAttribute(Attribute.GENERIC_ARMOR);
        if(armorAttribute != null){
            return String.valueOf(armorAttribute.getValue());
        }

        return "No Armor";
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
}
