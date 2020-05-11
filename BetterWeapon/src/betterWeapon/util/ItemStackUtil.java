package betterWeapon.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemStackUtil {
    public static ItemStack createItem(String itemId, String displayName, List<String> lore, int customModelId) {
        ItemStack itemStack = new ItemStack(Material.getMaterial(itemId.toUpperCase()));
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(displayName);
        itemMeta.setLore(lore);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.setCustomModelData(customModelId);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static boolean isSimilar(ItemStack itemStack1, ItemStack itemStack2) {
        Material material1 = itemStack1.getType();
        Material material2 = itemStack2.getType();

        if(!material1.equals(material2))
            return false;
        ItemMeta itemMeta1 = itemStack1.getItemMeta();
        ItemMeta itemMeta2 = itemStack2.getItemMeta();
        if(itemMeta1==null || itemMeta2==null)
            return false;
        int customId1 = itemMeta1.getCustomModelData();
        int customId2 = itemMeta2.getCustomModelData();
        if(customId1!=customId2)
            return false;
        String displayName1 = itemMeta1.getDisplayName();
        String displayName2 = itemMeta2.getDisplayName();
        if(!displayName1.equalsIgnoreCase(displayName2))
            return false;
        List<String> lore1 = itemMeta1.getLore();
        List<String> lore2 = itemMeta2.getLore();
        if((lore1!=null && lore2==null) || (lore1==null && lore2!=null))
            return false;
        if(lore1==null && lore2==null)
            return true;
        else
            return lore1.equals(lore2);
    }
}
