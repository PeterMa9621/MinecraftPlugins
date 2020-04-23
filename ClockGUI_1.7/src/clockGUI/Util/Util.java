package clockGUI.Util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

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

    public static ItemStack createItem(String ID, int quantity, int customModelId, String displayName, String lore)
    {
        ItemStack item = new ItemStack(Material.getMaterial(ID.toUpperCase()), quantity);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        meta.setCustomModelData(customModelId);
        ArrayList<String> loreList = new ArrayList<String>();
        for(String l:lore.split("%"))
        {
            loreList.add(l);
        }
        meta.setLore(loreList);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);

        return item;
    }
}
