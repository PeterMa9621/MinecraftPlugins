package com.peter.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import com.peter.FestivalReward;

import java.util.ArrayList;

public class Util {
    public final static int qqIndex = 11;
    public final static int wechatIndex = 13;
    public final static int alipayIndex = 15;
    public final static int webIndex = 22;
    public final static String guiTitle = "§5§l请选择支付方式购买§1";
    public final static ArrayList<String> describe = new ArrayList<String>() {{
        add("§3点击后会稍等片刻会打开二维码地图");
        add("§3扫码完成支付后稍等1-2分钟");
        add("§3之后购买的物品会在自动发放");
    }};
    public static void openGui(FestivalReward plugin, Player player, String kitName) {
        Inventory inventory = Bukkit.createInventory(player, 27, guiTitle + kitName);

        ItemStack qq = createItemStack(Material.STICK, "§6通过QQ支付", 72);
        setPersistentData(qq, new NamespacedKey(plugin, "kitName"), kitName);
        ItemStack wechat = createItemStack(Material.STICK, "§6通过微信支付", 73);
        setPersistentData(wechat, new NamespacedKey(plugin, "kitName"), kitName);
        ItemStack alipay = createItemStack(Material.STICK, "§6通过支付宝支付", 74);
        setPersistentData(alipay, new NamespacedKey(plugin, "kitName"), kitName);

        ItemStack web = createItemStack(Material.PAPER, "§6打开网站购买", 17);
        ArrayList<String> webDdescribe = new ArrayList<String>() {{
            add("§3点击后请点击聊天框的文字打开网站");
        }};
        ItemMeta itemMeta = web.getItemMeta();
        itemMeta.setLore(webDdescribe);
        web.setItemMeta(itemMeta);

        inventory.setItem(qqIndex, qq);
        inventory.setItem(wechatIndex, wechat);
        inventory.setItem(alipayIndex, alipay);
        inventory.setItem(webIndex, web);

        player.openInventory(inventory);
    }

    private static ItemStack createItemStack(Material material, String displayName, int customModelId) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(displayName);
        itemMeta.setCustomModelData(customModelId);
        itemMeta.setLore(describe);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static void setPersistentData(ItemStack item, NamespacedKey key, String value) {
        if(item==null)
            return;
        ItemMeta itemMeta = item.getItemMeta();
        if(itemMeta==null)
            return;
        itemMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, value);
        item.setItemMeta(itemMeta);
    }

    public static String getPersistentData(ItemStack item, NamespacedKey key) {
        if(item==null)
            return null;
        ItemMeta itemMeta = item.getItemMeta();
        if(itemMeta == null)
            return null;
        return itemMeta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
    }
}
