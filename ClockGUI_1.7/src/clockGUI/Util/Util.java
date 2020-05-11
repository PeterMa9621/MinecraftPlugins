package clockGUI.Util;

import com.mysql.fabric.xmlrpc.base.Array;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Util {
    public static ItemStack setItem(ItemStack item, String name, List<String> lore, String itemID)
    {
        ItemMeta meta = item.getItemMeta();
        if(name!=null) {
            meta.setDisplayName(name.replace('&', ChatColor.COLOR_CHAR));
        }

        if(lore!=null) {
            ArrayList<String> tmp = new ArrayList<>();
            for(String s:lore) {
                tmp.add(s.replace('&', ChatColor.COLOR_CHAR));
            }
            meta.setLore(tmp);
        }
        item.setItemMeta(meta);

        if(itemID!=null) {
            item.setType(Material.getMaterial(itemID));
        }
        return item;
    }

    public static ItemStack createItem(String ID, int quantity, int customModelId, String displayName, List<String> lore, boolean hideEnchant, boolean hideAttribute)
    {
        ItemStack item = new ItemStack(Material.getMaterial(ID.toUpperCase()), quantity);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName.replace('&', ChatColor.COLOR_CHAR));
        meta.setCustomModelData(customModelId);

        ArrayList<String> tmp = new ArrayList<>();
        for(String s:lore) {
            tmp.add(s.replace('&', ChatColor.COLOR_CHAR));
        }
        meta.setLore(tmp);

        if(hideAttribute)
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        if(hideEnchant)
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);

        return item;
    }
}
