package pixelPayGUI.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pixelPayGUI.PixelPayGUI;

import java.util.ArrayList;
import java.util.List;

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
    public static void openGui(PixelPayGUI plugin, Player player, String kitName) {
        Inventory inventory = Bukkit.createInventory(player, 27, guiTitle + kitName);

        ItemStack qq = createItemStack(Material.WOOD_HOE, "§6通过QQ支付", kitName, (short) 1);
        ItemStack wechat = createItemStack(Material.WOOD_HOE, "§6通过微信支付", kitName, (short) 2);
        ItemStack alipay = createItemStack(Material.WOOD_HOE, "§6通过支付宝支付", kitName, (short) 3);

        ItemStack web = createItemStack(Material.WOOD_HOE, "§6打开网站购买", null, (short) 4);
        ArrayList<String> webDdescribe = new ArrayList<String>() {{
            add("§3点击后请点击聊天框的文字打开网站");
        }};
        ItemMeta itemMeta = web.getItemMeta();
        itemMeta.setLore(webDdescribe);
        itemMeta.setUnbreakable(true);
        web.setItemMeta(itemMeta);

        inventory.setItem(qqIndex, qq);
        inventory.setItem(wechatIndex, wechat);
        inventory.setItem(alipayIndex, alipay);
        inventory.setItem(webIndex, web);

        player.openInventory(inventory);
    }

    private static ItemStack createItemStack(Material material, String displayName, String kitName, short damage) {
        ItemStack itemStack = new ItemStack(material, 1, damage);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(displayName);
        itemMeta.setUnbreakable(true);
        //itemMeta.setCustomModelData(customModelId);
        ArrayList<String> tmp = new ArrayList<>(describe);
        if(kitName!=null)
            tmp.add(0, kitName);
        itemMeta.setLore(tmp);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
