package peterHelper.util;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import peterHelper.PeterHelper;
import peterHelper.model.SuiteInfo;

import java.util.HashMap;
import java.util.List;

public class ArmorUtil {
    public static boolean isArmorSuite(PeterHelper plugin, ItemStack[] armors, ItemStack newEquip) {
        if(armors.length < 4)
            return false;

        for(int i=0; i<4; i++) {
            if(armors[i]==null) {
                armors[i] = newEquip;
                break;
            }
        }

        HashMap<String, Integer> suite = new HashMap<>();
        for(ItemStack armor : armors) {
            if(armor==null)
                return false;
            String suiteInfo = Util.getPersistentData(armor, new NamespacedKey(plugin, "suite"));
            Bukkit.getLogger().info(suiteInfo);
            if(suiteInfo==null)
                return false;
            suite.put(suiteInfo, 1);
        }
        return suite.size() == 1;
    }

    public static void bindSuiteInfo(PeterHelper plugin, ItemStack itemStack, SuiteInfo suiteInfo) {
        if(suiteInfo==null)
            return;
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> lore = itemMeta.getLore();
        int size = lore.size();
        lore.add(size-1, "¡ì5Ì××°Ð§¹û");
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        Util.setPersistentData(itemStack, new NamespacedKey(plugin, "suite"), suiteInfo.getSuiteName());
        Bukkit.getLogger().info(suiteInfo.getSuiteName());
    }
}
