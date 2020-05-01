package betterWeapon.listener;

import java.util.ArrayList;
import java.util.List;

import betterWeapon.BetterWeapon;
import betterWeapon.manager.GemGuiManager;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BetterWeaponGemListener implements Listener
{
	private BetterWeapon plugin;

	int[] slot = {0,1,2,3,4,5,6,7,8,9,11,12,14,15,17,18,19,20,21,22,23,24,25,26,27,28
			,29,30,32,33,34,35,36,37,38,39,41,42,43,44};
	int[] slotHole = {0,1,2,3,4,5,6,7,8,9,11,12,13,14,15,17,18,19,20,21,23,24,25,26};
	int[] slotInlay = {0,1,2,3,4,5,6,7,8,9,11,12,13,14,15,17,18,19,20,21,22,23,24,25,26,27,28
			,29,30,32,33,34,35,36,37,38,39,41,42,43,44};
	
	GemGuiManager gemGui;
	
	public BetterWeaponGemListener(BetterWeapon plugin)
	{
		this.plugin = plugin;
		gemGui = new GemGuiManager(plugin);
	}
	
	@EventHandler
	public void onPlayerClickGUI(InventoryClickEvent event)
	{
		if(event.getView().getTitle().equalsIgnoreCase("��5��ʯϵͳ"))
		{
			event.setCancelled(true);
			if(event.getRawSlot()==0)
			{
				event.getWhoClicked().openInventory(gemGui.initHoleGUI((Player)event.getWhoClicked()));
				return;
			}
			
			if(event.getRawSlot()==2)
			{
				event.getWhoClicked().openInventory(gemGui.initInlayGUI((Player)event.getWhoClicked()));
				return;
			}
			
			if(event.getRawSlot()==4)
			{
				event.getWhoClicked().openInventory(gemGui.initEvaluateGUI((Player)event.getWhoClicked()));
				return;
			}
			
			if(event.getRawSlot()==6)
			{
				event.getWhoClicked().openInventory(gemGui.initSynthesisGUI((Player)event.getWhoClicked()));
				return;
			}
			
			if(event.getRawSlot()==8)
			{
				event.getWhoClicked().openInventory(plugin.guiManager.initMainGUI((Player)event.getWhoClicked()));
				return;
			}
		}
		
		if(event.getView().getTitle().equalsIgnoreCase("��5װ������"))
		{
			if(plugin.isExist(event.getRawSlot(), slotHole))
			{
				event.setCancelled(true);
			}
			
			if(event.getRawSlot()==26)
			{
				event.getWhoClicked().openInventory(plugin.gemGui.initMainGUI((Player)event.getWhoClicked()));
				return;
			}

			if(event.getRawSlot()==22 && 
					event.getInventory().getItem(22).getItemMeta().getDisplayName().equalsIgnoreCase("��5�����ʼ����"))
			{
				Player p = (Player)event.getWhoClicked();
				event.setCancelled(true);
				if(event.getInventory().getItem(16)!=null)
				{
					event.getWhoClicked().sendMessage("��a[��ʯϵͳ]��c װ�����������ֹ�����κ���Ʒ��");
					return;
				}

				if(event.getInventory().getItem(10)!=null && 
						plugin.gemManager.equipment.contains(event.getInventory().getItem(10).getType().toString()))
				{
					if(event.getInventory().getItem(10).hasItemMeta() && 
							event.getInventory().getItem(10).getItemMeta().hasLore())
					{
						if(event.getInventory().getItem(10).getItemMeta().getLore().contains("��e[�ѿ���]"))
						{
							p.sendMessage("��a[��ʯϵͳ]��c ���װ���Ѿ���������");
							return;
						}
					}

					int priceForHole = plugin.gemManager.priceForHole;
					if(plugin.economy.getBalance(p.getName())>=priceForHole)
					{
						plugin.economy.withdrawPlayer(p.getName(), priceForHole);
						p.sendMessage("��a[��ʯϵͳ]��e �۳���c"+String.valueOf(priceForHole)+"��e���");
						hole(event);
					}
					else
					{
						p.sendMessage("��a[��ʯϵͳ]��c װ�����������Ҳ���");
					}
				}
				else
				{
					p.sendMessage("��a[��ʯϵͳ]��c ȱ��װ������Ч��װ��");
				}
			}
		
		}

	}

	
	private void hole(InventoryClickEvent event) 
	{
		Player p = (Player)event.getWhoClicked();
		//---------------------------------------------
		ItemStack equip = event.getInventory().getItem(10);
		
		int quantity = plugin.random(plugin.gemManager.holePossibility.size());

		for(int i=quantity; i>=0; i--)
		{
			if(plugin.random(100)<plugin.gemManager.holePossibility.get(i))
			{
				_hole(event, equip, i);
				p.sendMessage("��a[��ʯϵͳ]��c ��ϲ�����׳ɹ���");
				
				p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.5F, 0.0F);
				event.getInventory().setItem(10, null);
				event.getInventory().setItem(16, equip);
				return;
			}
		}
		p.sendMessage("��a[��ʯϵͳ]��5 ���׹����г������⣬����ʧ��");
		
		event.getInventory().setItem(10, null);
		event.getInventory().setItem(16, equip);
	}

	private void _hole(InventoryClickEvent event, ItemStack equip, int quantity) 
	{
		ItemMeta meta = equip.getItemMeta();
		List<String> loreList = meta.getLore();
		
		if(!loreList.isEmpty())
		{
			String lore = "��a��������:��c"+(quantity+1)+"%��e[�ѿ���]";
			if(loreList.contains("��e[�ѿ���]"))
			{
				int index = loreList.indexOf("��e[�ѿ���]");
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
			String lore = "��e[�ѿ���]%��a��������:��c"+(quantity+1);
			for(String l:lore.split("%"))
			{
				loreList.add(l);
			}
		}

		/*
		String lore = "��e[�ѿ���]%��a��������:��c"+(quantity+1);
				
		for(String l:lore.split("%"))
		{
			loreList.add(l);
		}
		*/
		
		meta.setLore(loreList);
		equip.setItemMeta(meta);
	}

	@EventHandler
	public void onPlayerCloseGUI(InventoryCloseEvent event)
	{
		
		if(event.getView().getTitle().equalsIgnoreCase("��5װ������"))
		{
			if(event.getInventory().getItem(10)!=null)
			{
				event.getPlayer().getInventory().addItem(event.getInventory().getItem(10));
			}
			if(event.getInventory().getItem(16)!=null)
			{
				event.getPlayer().getInventory().addItem(event.getInventory().getItem(16));
			}
			return;
		}
		
		if(event.getView().getTitle().equalsIgnoreCase("��5װ����Ƕ"))
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
		
		if(event.getView().getTitle().equalsIgnoreCase("��5��ʯ����"))
		{
			if(event.getInventory().getItem(10)!=null)
			{
				event.getPlayer().getInventory().addItem(event.getInventory().getItem(10));
			}
			if(event.getInventory().getItem(16)!=null)
			{
				event.getPlayer().getInventory().addItem(event.getInventory().getItem(16));
			}
			return;
		}
		
		if(event.getView().getTitle().equalsIgnoreCase("��5��ʯ�ϳ�"))
		{
			if(event.getInventory().getItem(19)!=null)
			{
				event.getPlayer().getInventory().addItem(event.getInventory().getItem(10));
			}
			if(event.getInventory().getItem(25)!=null)
			{
				event.getPlayer().getInventory().addItem(event.getInventory().getItem(16));
			}
			if(event.getInventory().getItem(40)!=null)
			{
				event.getPlayer().getInventory().addItem(event.getInventory().getItem(40));
			}
		}
	}
	

}
