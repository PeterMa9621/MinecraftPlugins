package vipSystem.util;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Util {
    static public FileConfiguration load(File file)
    {
        if (!(file.exists()))
        { //假如文件不存在
            try   //捕捉异常，因为有可能创建不成功
            {
                file.createNewFile();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    static public FileConfiguration load(String path)
    {
        File file=new File(path);
        if(!file.exists())
        {
            try
            {
                file.createNewFile();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
        return YamlConfiguration.loadConfiguration(new File(path));
    }

    public static LocalDateTime getNewDate(LocalDateTime recentDate, int days)
    {
        return recentDate.plusDays(days);
    }

    public static ItemStack createItem(String ID, int quantity, int durability, String displayName, String lore)
    {
        ItemStack item = new ItemStack(Material.getMaterial(ID), quantity, (short)durability);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        ArrayList<String> loreList = new ArrayList<String>();
        for(String l:lore.split("%"))
        {
            loreList.add(l);
        }
        meta.setLore(loreList);
        item.setItemMeta(meta);

        return item;
    }

    public static ItemStack createItem(String ID, int quantity, int durability, String displayName)
    {
        ItemStack item = new ItemStack(Material.getMaterial(ID), quantity, (short)durability);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        item.setItemMeta(meta);

        return item;
    }
}
