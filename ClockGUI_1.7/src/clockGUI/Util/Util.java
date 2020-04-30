package clockGUI.Util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Util {
    public static ItemStack setItem(ItemStack item, String name, ArrayList<String> lore, String itemID)
    {
        ItemMeta meta = item.getItemMeta();
        if(name!=null)
        {
            meta.setDisplayName(name);
        }

        if(lore!=null)
        {
            meta.setLore(lore);
        }
        item.setItemMeta(meta);

        if(itemID!=null)
        {
            item.setType(Material.getMaterial(itemID));
        }
        return item;
    }

    public static ItemStack createItem(String ID, int quantity, int customModelId, String displayName, List<String> lore, boolean hideEnchant, boolean hideAttribute)
    {
        ItemStack item = new ItemStack(Material.getMaterial(ID.toUpperCase()), quantity);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        meta.setCustomModelData(customModelId);
        meta.setLore(lore);
        if(hideAttribute)
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        if(hideEnchant)
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);

        return item;
    }
}
