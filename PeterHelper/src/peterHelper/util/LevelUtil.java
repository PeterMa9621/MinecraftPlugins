package peterHelper.util;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import peterHelper.PeterHelper;

import java.util.List;

public class LevelUtil {
    public static void bindLevelUtil(ItemStack itemStack, int level, PeterHelper plugin) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> lore = itemMeta.getLore();
        int size = lore.size();
        lore.add(size-1, "§9等级要求: §6" + level);
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        Util.setPersistentData(itemStack, new NamespacedKey(plugin, "level"), "" + level);
    }

    public static boolean canUseItem(ItemStack itemStack, Player player, PeterHelper plugin) {
        String levelInfo = Util.getPersistentData(itemStack, new NamespacedKey(plugin, "level"));
        if(levelInfo==null) {
            return true;
        }
        int level = Integer.parseInt(levelInfo);
        int currentPlayerLevel = plugin.levelSystemApi.getLevel(player);
        return currentPlayerLevel >= level;
    }
}
