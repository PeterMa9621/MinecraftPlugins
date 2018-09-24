package betterWeapon;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class BetterWeaponGemGui 
{
	private BetterWeapon plugin;
	public BetterWeaponGemGui(BetterWeapon plugin)
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
		ItemStack hole = plugin.createItem(276, 1, 0, "§5点击打开装备开孔界面");
		//--------------------------------------------
		ItemStack attribute = plugin.createItem(145, 1, 0, "§e点击打开装备镶嵌界面");
		//--------------------------------------------
		ItemStack gem = plugin.createItem(388, 1, 0, "§e点击打开宝石鉴定界面");
		//--------------------------------------------
		ItemStack gemSmelt = plugin.createItem(264, 1, 0, "§e点击打开宝石合成界面");
		//--------------------------------------------
		ItemStack back = plugin.createItem(331, 1, 0, "§e点击返回主界面");
		
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
		ItemStack windows = plugin.createItem(160, 1, 1, " ");
		//--------------------------------------------
		ItemStack equipment = plugin.createItem(160, 1, 2, "§3上方请放入要开孔的装备");
		//--------------------------------------------
		ItemStack finish = plugin.createItem(160, 1, 5, "§3开完孔的装备会出现在这里");
		//--------------------------------------------
		ItemStack start = plugin.createItem(145, 1, 0, "§5点击开始开孔", "§e需要花费§c"+plugin.priceForHole+"§e金币");
		//--------------------------------------------
		ItemStack back = plugin.createItem(331, 1, 0, "§e点击返回主界面");
		
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
		ItemStack windows = plugin.createItem(160, 1, 2, " ");
		//--------------------------------------------
		ItemStack equipment = plugin.createItem(160, 1, 1, "§3上方请放入要镶嵌的装备");
		//--------------------------------------------
		ItemStack gem = plugin.createItem(160, 1, 5, "§3上方请放入宝石");
		//--------------------------------------------
		ItemStack start = plugin.createItem(145, 1, 0, "§5点击开始镶嵌", "§e需要花费§c"+plugin.priceForInlay+"§e金币");
		//--------------------------------------------
		ItemStack back = plugin.createItem(331, 1, 0, "§e点击返回主界面");
		
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
		ItemStack windows = plugin.createItem(160, 1, 0, " ");
		//--------------------------------------------
		ItemStack gemOriginal = plugin.createItem(160, 1, 1, "§3此处请放入要鉴定的宝石");
		//--------------------------------------------
		ItemStack gemFinish = plugin.createItem(160, 1, 5, "§3此处为鉴定完的宝石");
		//--------------------------------------------
		ItemStack start = plugin.createItem(145, 1, 0, "§5点击开始鉴定", "§e需要花费§c"+plugin.priceForEvaluate+"§e金币");
		//--------------------------------------------
		ItemStack back = plugin.createItem(331, 1, 0, "§e点击返回主界面");
		
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
		ItemStack windows = plugin.createItem(160, 1, 15, " ");
		//--------------------------------------------
		ItemStack gem1 = plugin.createItem(160, 1, 1, "§3此处请放入宝石");
		//--------------------------------------------
		ItemStack gem2 = plugin.createItem(160, 1, 1, "§3此处请放入相同的宝石");
		//--------------------------------------------
		ItemStack start = plugin.createItem(145, 1, 0, "§5点击开始合成", "§e需要花费§c"+plugin.priceForSynthesis+"§e金币");
		//--------------------------------------------
		ItemStack back = plugin.createItem(331, 1, 0, "§e点击返回主界面");
		
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
