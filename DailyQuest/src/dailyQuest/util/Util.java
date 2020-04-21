package dailyQuest.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.SplittableRandom;

public class Util {
    public static ItemStack createItem(String ID, int quantity, String displayName, List<String> lore, int customModelId)
    {
        ItemStack item = new ItemStack(Material.getMaterial(ID.toUpperCase()), quantity);
        ItemMeta meta = item.getItemMeta();
        if(meta != null){
            if(displayName!=null) {
                meta.setDisplayName(displayName);
            }
            if(customModelId>0) {
                meta.setCustomModelData(customModelId);
            }

            item.setItemMeta(meta);
        }
        return item;
    }

    public static void replacePlaceholder(ItemStack itemStack, String name) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        String displayName = itemMeta.getDisplayName();
        displayName = displayName.replace("%name%", name);
        itemMeta.setDisplayName(displayName);
        List<String> lore = itemMeta.getLore();
        ArrayList<String> newLore = new ArrayList<>();
        if(lore!=null) {
            for(String eachLore:lore) {
                String newEachLore = eachLore.replace("%name%", name);
                newLore.add(newEachLore);
            }
            itemMeta.setLore(newLore);
        }
        itemStack.setItemMeta(itemMeta);
    }

    public static int random(int range) {
        SplittableRandom random = new SplittableRandom();
        return random.nextInt(range);
    }

    public static void changeLore(ItemStack itemStack, ArrayList<String> lore) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
    }

    public static boolean canItemStack(Material material) {
        ItemStack itemStack = new ItemStack(material);
        //Bukkit.getConsoleSender().sendMessage("MaxStack " + (itemStack.getMaxStackSize()>1));
        return itemStack.getMaxStackSize()>1;
    }
}
