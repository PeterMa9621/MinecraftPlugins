package levelSystem.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemUtil {
    public static ItemStack createItem(String ID, String displayName, List<String> lore, int customModelId) {
        ArrayList<String> loreList = new ArrayList<>();
        for(String eachLore:lore) {
            eachLore = eachLore.replace("&", "¡ì");
            loreList.add(eachLore);
        }
        ItemStack item = new ItemStack(Material.getMaterial(ID.toUpperCase()));
        ItemMeta meta = item.getItemMeta();
        if(meta != null){
            meta.setLore(loreList);
            if(displayName!=null) {
                displayName = displayName.replace("&", "¡ì");
                meta.setDisplayName(displayName);
            }
            if(customModelId>0) {
                meta.setCustomModelData(customModelId);
            }

            item.setItemMeta(meta);
        }
        return item;
    }
}
