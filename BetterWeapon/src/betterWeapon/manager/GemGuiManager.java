package betterWeapon.manager;

import betterWeapon.BetterWeapon;
import betterWeapon.util.ItemStackUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class GemGuiManager
{
	private BetterWeapon plugin;
	public GemGuiManager(BetterWeapon plugin)
	{
		this.plugin=plugin;
	}
	
	public Inventory initMainGUI(Player player)
	{
		/**
		 *  ┌───┬───┬───┬───┬───┬───┬───┬───┬───┐
		 *  │ 0 │ 1 │ 2 │ 3 │ 4 │ 5 │ 6 │ 7 │ 8 │
		 *  └───┴───┴───┴───┴───┴───┴───┴───┴───┘
		 */
		ItemStack hole = ItemStackUtil.createItem("diamond_sword", "§5点击打开装备开孔界面", null, 0);
		//--------------------------------------------
		ItemStack attribute = ItemStackUtil.createItem("anvil", "§e点击打开装备镶嵌界面", null, 0);
		//--------------------------------------------
		ItemStack gem = ItemStackUtil.createItem("emerald", "§e点击打开宝石鉴定界面", null, 0);
		//--------------------------------------------
		ItemStack gemSmelt = ItemStackUtil.createItem("diamond", "§e点击打开宝石合成界面", null, 0);
		//--------------------------------------------
		ItemStack back = ItemStackUtil.createItem("paper", "§e点击返回主界面", null, 12);
		
		Inventory inv = Bukkit.createInventory(player, 9, "§5宝石系统");
		inv.setItem(0, hole);
		inv.setItem(2, attribute);
		inv.setItem(4, gem);
		inv.setItem(6, gemSmelt);
		inv.setItem(8, back);
		
		return inv;
	}
	
	public Inventory initHoleGUI(Player player)
	{
		ItemStack windows = ItemStackUtil.createItem("light_gray_stained_glass_pane", " ", null, 0);
		//--------------------------------------------
		ItemStack equipment = ItemStackUtil.createItem("orange_stained_glass_pane", "§3上方请放入要开孔的装备", null, 0);
		//--------------------------------------------
		ItemStack finish = ItemStackUtil.createItem("lime_stained_glass_pane", "§3开完孔的装备会出现在这里", null, 0);
		//--------------------------------------------
		ItemStack start = ItemStackUtil.createItem("anvil", "§5点击开始开孔", new ArrayList<String>() {{
			add("§e需要花费§c"+plugin.gemManager.priceForHole+"§e金币");
		}}, 0);
		//--------------------------------------------
		ItemStack back = ItemStackUtil.createItem("paper", "§e点击返回主界面", null, 12);
		
		Inventory inv = Bukkit.createInventory(player, 27, "§5装备开孔");
		for(int i=0; i<27; i++)
		{
			inv.setItem(i, windows);
		}
		inv.setItem(10, null);
		inv.setItem(16, null);
		inv.setItem(19, equipment);
		inv.setItem(25, finish);
		inv.setItem(22, start);
		inv.setItem(26, back);
		return inv;
	}
	
	public Inventory initInlayGUI(Player player)
	{
		ItemStack windows = ItemStackUtil.createItem("light_gray_stained_glass_pane", " ", null, 0);
		//--------------------------------------------
		ItemStack equipment = ItemStackUtil.createItem("orange_stained_glass_pane", "§3上方请放入要镶嵌的装备", null, 0);
		//--------------------------------------------
		ItemStack gem = ItemStackUtil.createItem("lime_stained_glass_pane", "§3上方请放入宝石", null, 0);
		//--------------------------------------------
		ItemStack start = ItemStackUtil.createItem("anvil", "§5点击开始镶嵌", new ArrayList<String>() {{
			add("§e需要花费§c"+plugin.gemManager.priceForInlay+"§e金币");
		}}, 0);
		//--------------------------------------------
		ItemStack back = ItemStackUtil.createItem("paper", "§e点击返回主界面", null, 12);
		
		Inventory inv = Bukkit.createInventory(player, 45, "§5装备镶嵌");
		for(int i=0; i<45; i++)
		{
			inv.setItem(i, windows);
		}
		inv.setItem(10, null);
		inv.setItem(16, null);
		inv.setItem(19, equipment);
		inv.setItem(25, gem);
		inv.setItem(31, start);
		inv.setItem(40, null);
		inv.setItem(44, back);
		
		return inv;
	}
	
	public Inventory initEvaluateGUI(Player player)
	{
		ItemStack windows = ItemStackUtil.createItem("light_gray_stained_glass_pane", " ", null, 0);
		//--------------------------------------------
		ItemStack gemOriginal = ItemStackUtil.createItem("orange_stained_glass_pane", "§3此处请放入要鉴定的宝石", null, 0);
		//--------------------------------------------
		ItemStack gemFinish = ItemStackUtil.createItem("lime_stained_glass_pane", "§3此处为鉴定完的宝石", null, 0);
		//--------------------------------------------
		ItemStack start = ItemStackUtil.createItem("anvil", "§5点击开始鉴定", new ArrayList<String>() {{
			add("§e需要花费§c"+plugin.gemManager.priceForEvaluate+"§e金币");
		}}, 0);
		//--------------------------------------------
		ItemStack back = ItemStackUtil.createItem("paper", "§e点击返回主界面", null, 12);
		
		Inventory inv = Bukkit.createInventory(player, 27, "§5宝石鉴定");
		for(int i=0; i<27; i++)
		{
			inv.setItem(i, windows);
		}
		
		inv.setItem(1, gemOriginal);
		inv.setItem(9, gemOriginal);
		inv.setItem(11, gemOriginal);
		inv.setItem(19, gemOriginal);
		//==================================
		inv.setItem(7, gemFinish);
		inv.setItem(15, gemFinish);
		inv.setItem(17, gemFinish);
		inv.setItem(25, gemFinish);
		//==================================
		
		inv.setItem(10, null);
		inv.setItem(16, null);
		inv.setItem(13, start);
		inv.setItem(26, back);
		
		return inv;
	}
	
	public Inventory initSynthesisGUI(Player player)
	{
		ItemStack windows = ItemStackUtil.createItem("light_gray_stained_glass_pane", " ", null, 0);
		//--------------------------------------------
		ItemStack gem1 = ItemStackUtil.createItem("orange_stained_glass_pane", "§3此处请放入宝石", null, 0);
		//--------------------------------------------
		ItemStack gem2 = ItemStackUtil.createItem("orange_stained_glass_pane", "§3此处请放入相同的宝石", null, 0);
		//--------------------------------------------
		ItemStack start = ItemStackUtil.createItem("anvil", "§5点击开始合成", new ArrayList<String>() {{
			add("§e需要花费§c"+plugin.gemManager.priceForSynthesis+"§e金币");
		}}, 0);
		//--------------------------------------------
		ItemStack back = ItemStackUtil.createItem("paper", "§e点击返回主界面", null, 12);
		
		Inventory inv = Bukkit.createInventory(player, 45, "§5宝石合成");
		for(int i=0; i<45; i++)
		{
			inv.setItem(i, windows);
		}
		inv.setItem(19, null);
		inv.setItem(25, null);
		inv.setItem(31, start);
		inv.setItem(40, null);
		inv.setItem(44, back);
		//==========================
		inv.setItem(9, gem1);
		inv.setItem(10, gem1);
		inv.setItem(11, gem1);
		inv.setItem(18, gem1);
		inv.setItem(20, gem1);
		inv.setItem(27, gem1);
		inv.setItem(28, gem1);
		inv.setItem(29, gem1);
		//==========================
		inv.setItem(15, gem2);
		inv.setItem(16, gem2);
		inv.setItem(17, gem2);
		inv.setItem(24, gem2);
		inv.setItem(26, gem2);
		inv.setItem(33, gem2);
		inv.setItem(34, gem2);
		inv.setItem(35, gem2);
		return inv;
	}
}
