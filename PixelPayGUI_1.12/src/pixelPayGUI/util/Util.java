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
    public final static String guiTitle = "��5��l��ѡ��֧����ʽ�����1";
    public final static ArrayList<String> describe = new ArrayList<String>() {{
        add("��3�������Ե�Ƭ�̻�򿪶�ά���ͼ");
        add("��3ɨ�����֧�����Ե�1-2����");
        add("��3֮�������Ʒ�����Զ�����");
    }};
    public static void openGui(PixelPayGUI plugin, Player player, String kitName) {
        Inventory inventory = Bukkit.createInventory(player, 27, guiTitle + kitName);

        ItemStack qq = createItemStack(Material.WOOD_HOE, "��6ͨ��QQ֧��", kitName, (short) 1);
        ItemStack wechat = createItemStack(Material.WOOD_HOE, "��6ͨ��΢��֧��", kitName, (short) 2);
        ItemStack alipay = createItemStack(Material.WOOD_HOE, "��6ͨ��֧����֧��", kitName, (short) 3);

        ItemStack web = createItemStack(Material.WOOD_HOE, "��6����վ����", null, (short) 4);
        ArrayList<String> webDdescribe = new ArrayList<String>() {{
            add("��3������������������ִ���վ");
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
