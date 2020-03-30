package peterHelper.util;

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
}
