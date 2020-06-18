package clockGUI.Util;

import com.mysql.fabric.xmlrpc.base.Array;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
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

    public static ItemStack createItem(String ID, int quantity, int damage, String displayName, List<String> lore, boolean hideEnchant, boolean hideAttribute)
    {
        ItemStack item;
        try{
            item = new ItemStack(Material.getMaterial(ID.toUpperCase()), quantity, (short)damage);
        } catch (Exception e) {
            item = new ItemStack(Material.valueOf(ID), quantity, (short)damage);
        }

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName.replace('&', ChatColor.COLOR_CHAR));

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

    public static void replacePlaceholder(ItemStack itemStack, Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        String displayName = itemMeta.getDisplayName();
        displayName = PlaceholderAPI.setPlaceholders(player, displayName);
        itemMeta.setDisplayName(displayName);
        List<String> lores = new ArrayList<>();
        if(itemMeta.getLore()!=null) {
            for (String lore : itemMeta.getLore()) {
                lore = PlaceholderAPI.setPlaceholders(player, lore);
                lores.add(lore);
            }
        }

        itemMeta.setLore(lores);
        itemStack.setItemMeta(itemMeta);
    }
}
