package betterWeapon.listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import betterWeapon.BetterWeapon;
import betterWeapon.manager.GemGuiManager;
import betterWeapon.util.SmeltType;
import com.mysql.fabric.xmlrpc.base.Array;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BetterWeaponListener implements Listener
{
	private BetterWeapon plugin;
	boolean first = false;
	boolean second = false;
	boolean third = false;
	int[] slot = {0,1,2,3,4,5,6,7,8,9,11,12,14,15,17,18,19,20,21,22,23,24,25,26,27,28
			,29,30,32,33,34,35,36,37,38,39,41,42,43,44};
	int[] slot2 = {0,1,2,3,4,5,6,7,8,9,11,12,13,14,15,17,18,19,20,21,22,23,24,25,26,27,28
			,29,30,32,33,34,35,36,37,38,39,41,42,43,44};
	
	GemGuiManager gemGui;
	
	public BetterWeaponListener(BetterWeapon plugin)
	{
		this.plugin=plugin;
		gemGui= new GemGuiManager(plugin);
	}
	
	
	/*
	public void _intensifyForOwner(InventoryClickEvent event, ItemStack equip, int level)
	{
		ItemMeta meta = equip.getItemMeta();
		List<String> loreList = meta.getLore();
		String lore = "��aǿ���ȼ�:"+(level+1)+"%%��a������Ϊ:��3"+event.getWhoClicked().getName();
		if(loreList.size()==4)
		{
			String owner = loreList.get(3);
		}
		loreList.clear();
		for(String l:lore.split("%"))
		{
			loreList.add(l);
		}
		if(loreList.size()==4)
		{
			loreList.add("owner");
		}
		//String lore = "��aǿ���ȼ�:"+(level+1)+"%%��a������Ϊ:��3"+event.getWhoClicked().getName();
		//ArrayList<String> loreList = new ArrayList<String>();
		
		meta.setLore(loreList);
		equip.setItemMeta(meta);
		equip.addUnsafeEnchantment(Enchantment.getById(plugin.rule.get(equip.getTypeId())), level+1);
	}
	*/
	
	public void _intensify(InventoryClickEvent event, ItemStack equip, int level)
	{
		ItemMeta meta = equip.getItemMeta();
		//meta.getLore().toArray()
		List<String> loreList = new ArrayList<>();

		if(meta.getLore()!=null)
		{
			for(String lo:meta.getLore())
			{
				loreList.add(lo);
			}
		}

		ArrayList<String> lore = new ArrayList<String>() {{
			add("��aǿ���ȼ�:��c"+(level+1));
			add("��e[��ħǿ��]");
		}};
		if(!loreList.isEmpty()) {
			if(loreList.contains("��e[��ħǿ��]")) {
				int index = loreList.indexOf("��e[��ħǿ��]");
				for(int i=0; i<lore.size(); i++) {
					loreList.remove(index);
				}

				for(String l:lore) {
					loreList.add(index, l);
				}
			} else {
				for(String l:lore) {
					loreList.add(0, l);
				}
			}
		} else {
			for(String l:lore) {
				loreList.add(0, l);
			}
		}

		//String lore = "��aǿ���ȼ�:"+(level+1)+"%%��a������Ϊ:��3"+event.getWhoClicked().getName();
		//ArrayList<String> loreList = new ArrayList<String>();
		
		meta.setLore(loreList);
		equip.setItemMeta(meta);
		Enchantment enchantment = plugin.intensifyManager.getEnchant(equip.getType().toString());
		equip.addUnsafeEnchantment(enchantment, level+1);
	}
	
	public void intensify(InventoryClickEvent event, boolean assistant1, boolean assistant2)
	{
		Player p = (Player)event.getWhoClicked();
		//---------------------------------------------
		ItemStack equip = event.getInventory().getItem(10);
		if(equip==null)
			return;
		Enchantment enchantment = plugin.intensifyManager.getEnchant(equip.getType().toString());
		int level = equip.getEnchantmentLevel(enchantment);

		ArrayList<Integer> possibilityList = plugin.intensifyManager.getPossibilityList();
		if(level<possibilityList.size()) {
			if(assistant2) {
				_intensify(event, equip, level);
				
				p.sendMessage("��a[ǿ��ϵͳ]��c��ϲ��ǿ���ɹ���");
				if(level+1>5) {
					p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 0.0F);
					Bukkit.broadcastMessage("��3��ϲ��ҡ�a"+p.getName()+"��3ǿ��װ����a"+"��3����a"+(level+1)+"��3��");
				}
				else {
					p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.5F, 0.0F);
				}
				
				event.getInventory().setItem(10, null);
				if(event.getInventory().getItem(13).getAmount()>1) {
					ItemStack item13 = event.getInventory().getItem(13);
					item13.setAmount(item13.getAmount()-1);
					event.getInventory().setItem(13, item13);
				} else {
					event.getInventory().setItem(13, null);
				}
				
				if(event.getInventory().getItem(16).getAmount()>1) {
					ItemStack item16 = event.getInventory().getItem(16);
					item16.setAmount(item16.getAmount()-1);
					event.getInventory().setItem(16, item16);
				} else {
					event.getInventory().setItem(16, null);
				}
				
				event.getInventory().setItem(40, equip);
			} else if(plugin.random(100)<possibilityList.get(level)) {
				_intensify(event, equip, level);
				p.sendMessage("��a[ǿ��ϵͳ]��c��ϲ��ǿ���ɹ���");
				if(level+1>5) {
					p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 0.0F);
					Bukkit.broadcastMessage("��3��ϲ��ҡ�a"+p.getName()+"��3ǿ��װ����a"+"��3����a"+(level+1)+"��3��");
				}
				else {
					p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.5F, 0.0F);
				}

				event.getInventory().setItem(10, null);
				if(event.getInventory().getItem(13).getAmount()>1) {
					ItemStack item13 = event.getInventory().getItem(13);
					item13.setAmount(item13.getAmount()-1);
					event.getInventory().setItem(13, item13);
				} else {
					event.getInventory().setItem(13, null);
				}
				
				event.getInventory().setItem(40, equip);
			} else {
				if(level>=5) {
					if(assistant1) {
						event.getInventory().setItem(10, null);
						
						if(event.getInventory().getItem(13).getAmount()>1) {
							ItemStack item13 = event.getInventory().getItem(13);
							item13.setAmount(item13.getAmount()-1);
							event.getInventory().setItem(13, item13);
						} else {
							event.getInventory().setItem(13, null);
						}
						
						if(event.getInventory().getItem(16).getAmount()>1) {
							ItemStack item16 = event.getInventory().getItem(16);
							item16.setAmount(item16.getAmount()-1);
							event.getInventory().setItem(16, item16);
						} else {
							event.getInventory().setItem(16, null);
						}
						
						event.getInventory().setItem(40, equip);
						p.sendMessage("��a[ǿ��ϵͳ]��5���ź���ǿ��ʧ�ܣ�������Ʒ����Ч��������ס��������");
					} else {
						event.getInventory().setItem(10, null);
						if(event.getInventory().getItem(13).getAmount()>1) {
							ItemStack item13 = event.getInventory().getItem(13);
							item13.setAmount(item13.getAmount()-1);
							event.getInventory().setItem(13, item13);
						} else {
							event.getInventory().setItem(13, null);
						}
						p.sendMessage("��a[ǿ��ϵͳ]��5���ź���ǿ��ʧ�ܣ�װ������ˣ�");
					}
				} else {
					event.getInventory().setItem(10, null);
					if(event.getInventory().getItem(13).getAmount()>1) {
						ItemStack item13 = event.getInventory().getItem(13);
						item13.setAmount(item13.getAmount()-1);
						event.getInventory().setItem(13, item13);
					} else {
						event.getInventory().setItem(13, null);
					}
					
					event.getInventory().setItem(40, equip);
					p.sendMessage("��a[ǿ��ϵͳ]��5���ź���ǿ��ʧ�ܣ�");
				}
			}
		} else {
			p.sendMessage("��a[ǿ��ϵͳ]��5��װ����ǿ�������ȼ����������ǿ����");
		}
		
	}
	
	public void _smelt(InventoryClickEvent event, ItemStack equip, int level, SmeltType smeltType)
	{
		ItemMeta meta = equip.getItemMeta();
		ArrayList<String> loreList = new ArrayList<String>();
		String type = null;
		if(meta.getLore()!=null)
		{
			for(String lo:meta.getLore())
			{
				loreList.add(lo);
			}
		}

		if(smeltType.equals(SmeltType.attack))
			type = "����";
		if(smeltType.equals(SmeltType.defend))
			type = "����";

		if(!loreList.isEmpty())
		{
			String lore = null;
			if(type=="����")
				lore = "��a��������:��c"+ type + "+" +(level+1)+"%��e[����]";
			else
			{
				double defend = (level+1)/2.0;
				lore = "��a��������:��c"+ type + "+" +defend+"%��e[����]";
			}
			if(loreList.contains("��e[����]"))
			{
				int index = loreList.indexOf("��e[����]");
				for(int i=0; i<lore.split("%").length; i++)
				{
					loreList.remove(index);
				}
				
				for(String l:lore.split("%"))
				{
					loreList.add(index, l);
				}
			}
			else
			{
				for(String l:lore.split("%"))
				{
					loreList.add(0, l);
				}
			}
		}
		else
		{
			String lore = null;
			if(type=="����")
				lore = "��e[����]%��a��������:��c"+ type + "+" +(level+1);
			else
			{
				double defend = (level+1)/2.0;
				lore = "��e[����]%��a��������:��c"+ type + "+" +defend;
			}
				
			for(String l:lore.split("%"))
			{
				loreList.add(l);
			}
		}
		//String lore = "��aǿ���ȼ�:"+(level+1)+"%%��a������Ϊ:��3"+event.getWhoClicked().getName();
		//ArrayList<String> loreList = new ArrayList<String>();
		
		meta.setLore(loreList);
		equip.setItemMeta(meta);
	}
	
	public void smelt(InventoryClickEvent event)
	{
		Player p = (Player)event.getWhoClicked();
		//---------------------------------------------
		ItemStack equip = event.getInventory().getItem(10);
		
		SmeltType smeltType = plugin.smeltManager.getRule(equip.getType());
		
		if(event.getInventory().getItem(16).getAmount()>1) {
			ItemStack item16 = event.getInventory().getItem(16);
			item16.setAmount(item16.getAmount()-1);
			event.getInventory().setItem(16, item16);
		} else {
			event.getInventory().setItem(16, null);
		}

		ArrayList<Integer> possibilitySmeltList = plugin.smeltManager.getPossibilitySmeltList();
		int level = plugin.random(possibilitySmeltList.size());

		for(int i=level; i>=0; i--)
		{
			if(plugin.random(100)<possibilitySmeltList.get(i))
			{
				_smelt(event, equip, i, smeltType);
				p.sendMessage("��a[����ϵͳ] ��c��ϲ�������ɹ���");

				if(i+1>5)
				{
					if(smeltType.equals(SmeltType.attack))
					{
						Bukkit.broadcastMessage("��3��ϲ��ҡ�a"+p.getName()+"��3�����������֡�a"+(i+1)+"��3�����Լӳ�");
					}
					else
					{
						double highLevel = (i+1)/2.0;
						Bukkit.broadcastMessage("��3��ϲ��ҡ�a"+p.getName()+"��3����װ�����֡�a"+highLevel+"��3�����Լӳ�");
					}
					
					p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 0.0F);
				}
				else
					p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.5F, 0.0F);
				event.getInventory().setItem(10, null);
				event.getInventory().setItem(40, equip);
				return;
			}
		}
		p.sendMessage("��a[����ϵͳ]��5���������г������⣬����ʧ��");
		
		event.getInventory().setItem(10, null);
		event.getInventory().setItem(40, equip);

	}
	
	@EventHandler
	public void onPlayerClickGUI(InventoryClickEvent event)
	{
		if(event.getView().getTitle().equalsIgnoreCase("��5ǿ��ϵͳ"))
		{
			event.setCancelled(true);
			if(event.getRawSlot()==2) {
				event.getWhoClicked().openInventory(plugin.guiManager.initIntensifyGUI((Player)event.getWhoClicked()));
				return;
			}
			if(event.getRawSlot()==6) {
				event.getWhoClicked().openInventory(plugin.guiManager.initSmeltGUI((Player)event.getWhoClicked()));
				return;
			}
			if(event.getRawSlot()==4) {
				event.getWhoClicked().openInventory(gemGui.initMainGUI((Player)event.getWhoClicked()));
				return;
			}
		}
		
		//-----------------------------------------------------------------------
		
		if(event.getView().getTitle().equalsIgnoreCase("��5��ħǿ��ϵͳ"))
		{
			if(plugin.isExist(event.getRawSlot(), slot))
			{
				event.setCancelled(true);
			}
			
			if(event.getRawSlot()==44)
			{
				event.getWhoClicked().openInventory(plugin.guiManager.initMainGUI((Player)event.getWhoClicked()));
				return;
			}
			
			if(event.getRawSlot()==31 && 
					event.getInventory().getItem(31).getItemMeta().getDisplayName().equalsIgnoreCase("��5�����ʼǿ��"))
			{
				Player p = (Player)event.getWhoClicked();
				event.setCancelled(true);
				if(event.getInventory().getItem(40)!=null)
				{
					event.getWhoClicked().sendMessage("��a[ǿ��ϵͳ] ��cǿ����ť�·�����������κ���Ʒ��");
					return;
				}
				HashMap<String, String> rule = plugin.intensifyManager.getRule();
				if(event.getInventory().getItem(10)!=null &&
						rule.containsKey(event.getInventory().getItem(10).getType().toString()))
				{
					if(event.getInventory().getItem(13)!=null)
					{
						if(!event.getInventory().getItem(13).getItemMeta().hasLore())
						{
							p.sendMessage("��a[ǿ��ϵͳ] ��cȱ��ǿ��ʯ����Ч��ǿ��ʯ");
							return;
						}
						if(!event.getInventory().getItem(13).
								equals(plugin.intensifyManager.getItem()))
						{
							p.sendMessage("��a[ǿ��ϵͳ] ��cȱ��ǿ��ʯ����Ч��ǿ��ʯ");
							return;
						}

						if(event.getInventory().getItem(16)==null)
						{
							intensify(event, false, false);
							return;
						}
						if(!event.getInventory().getItem(16).getItemMeta().hasLore())
						{
							p.sendMessage("��a[ǿ��ϵͳ] ��c��Ч�ĸ�����Ʒ");
							return;
						}
						if(event.getInventory().getItem(16).
								equals(plugin.assistantManager.getAssistant(0)))//���ʯ
						{
							intensify(event, true, false);
							return;
						}
						if(event.getInventory().getItem(16).
								equals(plugin.assistantManager.getAssistant(1)))//���ʯ
						{
							intensify(event, false, true);
							return;
						}
						p.sendMessage("��a[ǿ��ϵͳ] ��c��Ч�ĸ�����Ʒ");
					}
					else
					{
						p.sendMessage("��a[ǿ��ϵͳ] ��cȱ��ǿ��ʯ����Ч��ǿ��ʯ");
					}
				}
				else
				{
					p.sendMessage("��a[ǿ��ϵͳ] ��cȱ��װ������Ч��װ��");
				}
			}
		}
		
		//-----------------------------------------------------------------------
		
		if(event.getView().getTitle().equalsIgnoreCase("��5����ϵͳ"))
		{
			if(plugin.isExist(event.getRawSlot(), slot2))
			{
				event.setCancelled(true);
			}
			
			if(event.getRawSlot()==44)
			{
				event.getWhoClicked().openInventory(plugin.guiManager.initMainGUI((Player)event.getWhoClicked()));
				return;
			}
			
			if(event.getRawSlot()==31 && 
					event.getInventory().getItem(31).getItemMeta().getDisplayName()=="��5�����ʼ����")
			{
				Player p = (Player)event.getWhoClicked();
				event.setCancelled(true);
				if(event.getInventory().getItem(40)!=null)
				{
					event.getWhoClicked().sendMessage("��a[����ϵͳ] ��c������ť�·�����������κ���Ʒ��");
					return;
				}
				HashMap<Material, SmeltType> ruleSmelt = plugin.smeltManager.getRuleSmelt();
				if(event.getInventory().getItem(10)!=null && 
						ruleSmelt.containsKey(event.getInventory().getItem(10).getType()))
				{
					if(event.getInventory().getItem(16)!=null)
					{
						if(event.getInventory().getItem(16).getItemMeta().hasLore())
						{
							if(event.getInventory().getItem(16)
									.equals(plugin.smeltManager.getItem()))
							{
								int priceForSmelt = plugin.smeltManager.getPrice();
								if(plugin.economy.getBalance(p.getName())>=priceForSmelt)
								{
									p.sendMessage("��a[����ϵͳ] ��e�۳���c" + priceForSmelt + "��e���");
									plugin.economy.withdrawPlayer(p.getName(), priceForSmelt);
									smelt(event);
								}
								else
								{
									p.sendMessage("��a[����ϵͳ] ��c���������Ҳ���");
								}
							}
							else
							{
								p.sendMessage("��a[����ϵͳ] ��cȱ������ʯ����Ч������ʯ");
							}
						}
						else
						{
							p.sendMessage("��a[����ϵͳ] ��cȱ������ʯ����Ч������ʯ");
						}
					}
					else
					{
						p.sendMessage("��a[����ϵͳ] ��cȱ������ʯ����Ч������ʯ");
					}
				}
				else
				{
					p.sendMessage("��a[����ϵͳ] ��cȱ��װ������Ч��װ��");
				}
			}
		}
		
	}
	
	@EventHandler
	public void onPlayerCloseGUI(InventoryCloseEvent event)
	{
		if(event.getView().getTitle().equalsIgnoreCase("��5��ħǿ��ϵͳ"))
		{
			if(event.getInventory().getItem(10)!=null)
			{
				event.getPlayer().getInventory().addItem(event.getInventory().getItem(10));
			}
			if(event.getInventory().getItem(13)!=null)
			{
				event.getPlayer().getInventory().addItem(event.getInventory().getItem(13));
			}
			if(event.getInventory().getItem(16)!=null)
			{
				event.getPlayer().getInventory().addItem(event.getInventory().getItem(16));
			}
			if(event.getInventory().getItem(40)!=null)
			{
				event.getPlayer().getInventory().addItem(event.getInventory().getItem(40));
			}
			return;
		}
		
		if(event.getView().getTitle().equalsIgnoreCase("��5����ϵͳ"))
		{
			if(event.getInventory().getItem(10)!=null)
			{
				event.getPlayer().getInventory().addItem(event.getInventory().getItem(10));
			}
			if(event.getInventory().getItem(16)!=null)
			{
				event.getPlayer().getInventory().addItem(event.getInventory().getItem(16));
			}
			if(event.getInventory().getItem(40)!=null)
			{
				event.getPlayer().getInventory().addItem(event.getInventory().getItem(40));
			}
			return;
		}
		
	}
	

}
