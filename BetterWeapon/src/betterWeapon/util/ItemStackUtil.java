package betterWeapon.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemStackUtil {
    public static ItemStack createItem(String itemId, String displayName, List<String> lore, int customModelId) {
        ItemStack itemStack = new ItemStack(Material.getMaterial(itemId.toUpperCase()));
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(displayName);
        itemMeta.setLore(lore);
        itemMeta.setCustomModelData(customModelId);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
