package peterHelper.manager;

import dev.lone.itemsadder.api.ItemsAdder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import peterHelper.PeterHelper;
import peterHelper.model.CustomItemInfo;
import peterHelper.util.ArmorUtil;
import peterHelper.util.AttributeModifierUtil;
import peterHelper.util.LevelUtil;
import peterHelper.util.Util;

public class CustomItemManager {
    PeterHelper plugin;
    public CustomItemManager(PeterHelper plugin) {
        this.plugin = plugin;
    }

    public ItemStack getCustomItem(String id) {
        ItemStack itemStack = ItemsAdder.getCustomItem(id);
        if(itemStack==null){
            return null;
        }
        CustomItemInfo customItemInfo = plugin.configManager.customItemHashMap.get(id);
        int level = customItemInfo.getLevelRequired();
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(Util.isWeapon(itemStack)) {
            AttributeModifierUtil.randomWeaponAttribute(itemMeta);
            itemStack.setItemMeta(itemMeta);
            LevelUtil.bindLevelUtil(itemStack, level, plugin);
        } else if(Util.isArmor(itemStack)) {
            AttributeModifierUtil.randomArmorAttribute(itemMeta);
            itemStack.setItemMeta(itemMeta);
            ArmorUtil.bindSuiteInfo(plugin, itemStack, customItemInfo.getSuiteInfo());
            LevelUtil.bindLevelUtil(itemStack, level, plugin);
        }
        return itemStack;
    }
}
