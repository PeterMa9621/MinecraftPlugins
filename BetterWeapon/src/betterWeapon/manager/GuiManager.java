package betterWeapon.manager;

import betterWeapon.BetterWeapon;
import betterWeapon.util.ItemStackUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class GuiManager {
    private BetterWeapon plugin;

    public GuiManager(BetterWeapon plugin) {
        this.plugin = plugin;
    }

    public Inventory initMainGUI(Player player)
    {
        ItemStack enchant = ItemStackUtil.createItem("enchanted_book", "§5点击打开附魔强化界面", null, 0);
        //--------------------------------------------
        ItemStack attribute = ItemStackUtil.createItem("anvil", "§e点击打开熔炼强化界面", null, 0);
        //--------------------------------------------
        ItemStack gem = ItemStackUtil.createItem("diamond", "§e点击打开宝石系统界面", null, 0);
        gem.addUnsafeEnchantment(Enchantment.LUCK, 1);

        Inventory inv = Bukkit.createInventory(player, 9, "§5强化系统");
        inv.setItem(2, enchant);
        inv.setItem(4, gem);
        inv.setItem(6, attribute);

        return inv;
    }

    public Inventory initSmeltGUI(Player player)
    {
        ItemStack windows = ItemStackUtil.createItem("light_gray_stained_glass_pane", " ", null, 0);
        //--------------------------------------------
        ItemStack equipment = ItemStackUtil.createItem("orange_stained_glass_pane", "§3上方请放入要熔炼的装备", null, 0);
        //--------------------------------------------
        ItemStack intensify = ItemStackUtil.createItem("lime_stained_glass_pane", "§3上方请放入熔炼石", null, 0);
        //--------------------------------------------
        ItemStack start = ItemStackUtil.createItem(
                "emerald",
                "§5点击开始熔炼",
                new ArrayList<String>() {{
                    add("§e需要花费§c"+plugin.smeltManager.getPrice()+"§e金币");
                }},
                0);
        //--------------------------------------------
        ItemStack back = ItemStackUtil.createItem("paper", "§e点击返回主界面", null, 12);

        Inventory inv = Bukkit.createInventory(player, 45, "§5熔炼系统");

        for(int i=0; i<45; i++)
        {
            inv.setItem(i, windows);
        }


        inv.setItem(10, null);
        inv.setItem(16, null);
        inv.setItem(19, equipment);
        inv.setItem(25, intensify);
        inv.setItem(31, start);
        inv.setItem(40, null);
        inv.setItem(44, back);
        return inv;
    }

    public Inventory initIntensifyGUI(Player player)
    {
        ItemStack windows = ItemStackUtil.createItem("light_gray_stained_glass_pane", " ", null, 0);
        //--------------------------------------------
        ItemStack equipment = ItemStackUtil.createItem("orange_stained_glass_pane", "§3上方请放入要强化的装备", null, 0);
        //--------------------------------------------
        ItemStack intensify = ItemStackUtil.createItem("yellow_stained_glass_pane", "§3上方请放入强化石", null, 0);
        //--------------------------------------------
        ItemStack assistant = ItemStackUtil.createItem("lime_stained_glass_pane", "§3上方请放入辅助物品(可以不放)", null, 0);
        //--------------------------------------------
        ItemStack start = ItemStackUtil.createItem(
                "emerald",
                "§5点击开始强化",
                new ArrayList<String>() {{
                    add("§e需要花费§c"+plugin.smeltManager.getPrice()+"§e金币");
                }},
                0);
        //--------------------------------------------
        ItemStack back = ItemStackUtil.createItem("paper", "§e点击返回主界面", null, 12);

        Inventory inv = Bukkit.createInventory(player, 45, "§5附魔强化系统");

        for(int i=0; i<10; i++)
        {
            inv.setItem(i, windows);
        }
        for(int i=17; i<45; i++)
        {
            inv.setItem(i, windows);
        }
        for(int i=0; i<4; i+=3)
        {
            inv.setItem(11+i, windows);
            inv.setItem(12+i, windows);
        }

        inv.setItem(19, equipment);
        inv.setItem(22, intensify);
        inv.setItem(25, assistant);
        inv.setItem(31, start);
        inv.setItem(40, null);
        inv.setItem(44, back);
        return inv;
    }
}
