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
		 *  ���������Щ������Щ������Щ������Щ������Щ������Щ������Щ������Щ�������
		 *  �� 0 �� 1 �� 2 �� 3 �� 4 �� 5 �� 6 �� 7 �� 8 ��
		 *  ���������ة������ة������ة������ة������ة������ة������ة������ة�������
		 */
		ItemStack hole = plugin.createItem(276, 1, 0, "��5�����װ�����׽���");
		//--------------------------------------------
		ItemStack attribute = plugin.createItem(145, 1, 0, "��e�����װ����Ƕ����");
		//--------------------------------------------
		ItemStack gem = plugin.createItem(388, 1, 0, "��e����򿪱�ʯ��������");
		//--------------------------------------------
		ItemStack gemSmelt = plugin.createItem(264, 1, 0, "��e����򿪱�ʯ�ϳɽ���");
		//--------------------------------------------
		ItemStack back = plugin.createItem(331, 1, 0, "��e�������������");
		
		Inventory inv = Bukkit.createInventory(player, 9, "��5��ʯϵͳ");
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
		ItemStack equipment = plugin.createItem(160, 1, 2, "��3�Ϸ������Ҫ���׵�װ��");
		//--------------------------------------------
		ItemStack finish = plugin.createItem(160, 1, 5, "��3����׵�װ�������������");
		//--------------------------------------------
		ItemStack start = plugin.createItem(145, 1, 0, "��5�����ʼ����", "��e��Ҫ���ѡ�c"+plugin.priceForHole+"��e���");
		//--------------------------------------------
		ItemStack back = plugin.createItem(331, 1, 0, "��e�������������");
		
		Inventory inv = Bukkit.createInventory(player, 27, "��5װ������");
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
		ItemStack equipment = plugin.createItem(160, 1, 1, "��3�Ϸ������Ҫ��Ƕ��װ��");
		//--------------------------------------------
		ItemStack gem = plugin.createItem(160, 1, 5, "��3�Ϸ�����뱦ʯ");
		//--------------------------------------------
		ItemStack start = plugin.createItem(145, 1, 0, "��5�����ʼ��Ƕ", "��e��Ҫ���ѡ�c"+plugin.priceForInlay+"��e���");
		//--------------------------------------------
		ItemStack back = plugin.createItem(331, 1, 0, "��e�������������");
		
		Inventory inv = Bukkit.createInventory(player, 45, "��5װ����Ƕ");
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
		ItemStack gemOriginal = plugin.createItem(160, 1, 1, "��3�˴������Ҫ�����ı�ʯ");
		//--------------------------------------------
		ItemStack gemFinish = plugin.createItem(160, 1, 5, "��3�˴�Ϊ������ı�ʯ");
		//--------------------------------------------
		ItemStack start = plugin.createItem(145, 1, 0, "��5�����ʼ����", "��e��Ҫ���ѡ�c"+plugin.priceForEvaluate+"��e���");
		//--------------------------------------------
		ItemStack back = plugin.createItem(331, 1, 0, "��e�������������");
		
		Inventory inv = Bukkit.createInventory(player, 27, "��5��ʯ����");
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
		ItemStack gem1 = plugin.createItem(160, 1, 1, "��3�˴�����뱦ʯ");
		//--------------------------------------------
		ItemStack gem2 = plugin.createItem(160, 1, 1, "��3�˴��������ͬ�ı�ʯ");
		//--------------------------------------------
		ItemStack start = plugin.createItem(145, 1, 0, "��5�����ʼ�ϳ�", "��e��Ҫ���ѡ�c"+plugin.priceForSynthesis+"��e���");
		//--------------------------------------------
		ItemStack back = plugin.createItem(331, 1, 0, "��e�������������");
		
		Inventory inv = Bukkit.createInventory(player, 45, "��5��ʯ�ϳ�");
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
