package betterWeapon.Gem;

import java.util.ArrayList;
import java.util.List;

import betterWeapon.util.Util;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import betterWeapon.BetterWeapon;


public class GemSynthesisListener implements Listener
{
	private BetterWeapon plugin;
	
	int[] slot = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,20,21,22,23,24,26,27,28,29,30,32,33,34
			,35,36,37,38,39,41,42,43,44};
	
	public GemSynthesisListener(BetterWeapon plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerClickGUI(InventoryClickEvent event)
	{
		if(event.getView().getTitle().equalsIgnoreCase("��5��ʯ�ϳ�"))
		{
			if(plugin.isExist(event.getRawSlot(), slot))
			{
				event.setCancelled(true);
			}
			
			if(event.getRawSlot()==44)
			{
				event.getWhoClicked().openInventory(plugin.gemGui.initMainGUI((Player)event.getWhoClicked()));
				return;
			}
			
			if(event.getRawSlot()==31 &&
					event.getInventory().getItem(31).getItemMeta().getDisplayName().equalsIgnoreCase("��5�����ʼ�ϳ�"))
			{
				Player p = (Player)event.getWhoClicked();
				event.setCancelled(true);
				if(event.getInventory().getItem(40)!=null)
				{
					event.getWhoClicked().sendMessage("��a[��ʯϵͳ]��c ��ʯ���������ֹ�����κ���Ʒ��");
					return;
				}
				
				if(event.getInventory().getItem(19)!=null && event.getInventory().getItem(25)!=null)
				{
					if(event.getInventory().getItem(19).getType().equals(plugin.gemManager.gemstone.getType())
							&& event.getInventory().getItem(25).getType().equals(plugin.gemManager.gemstone.getType()))
					{
						if((!event.getInventory().getItem(19).getItemMeta().hasLore()) &&
								(!event.getInventory().getItem(25).getItemMeta().hasLore()))
						{
							p.sendMessage("��a[��ʯϵͳ] ��cȱ���Ѽ����ı�ʯ");
							return;
						}
						
						if((!event.getInventory().getItem(19).getItemMeta().getLore().contains("��e[�Ѽ���]")) &&
								(!event.getInventory().getItem(25).getItemMeta().getLore().contains("��e[�Ѽ���]")))
						{
							p.sendMessage("��a[��ʯϵͳ] ��cȱ���Ѽ����ı�ʯ");
							return;
						}
						if(event.getInventory().getItem(19).getItemMeta().getLore().equals(event.getInventory().getItem(25).getItemMeta().getLore()))
						{
							String lastLore = event.getInventory().getItem(19).getItemMeta().getLore().get(1);
							Double value = Double.valueOf(lastLore.substring(lastLore.length()-3));
							if(lastLore.contains("����"))
							{
								if(value>=6.0)
								{
									p.sendMessage("��a[��ʯϵͳ] ��c�����ʯ�����Ѿ��ﵽ����޷������ϳ�");
									return;
								}
							}
							else
							{
								if(value>=3.0)
								{
									p.sendMessage("��a[��ʯϵͳ] ��c�����ʯ�����Ѿ��ﵽ����޷������ϳ�");
									return;
								}
							}
							
							if(plugin.economy.getBalance(p.getName())>=plugin.gemManager.priceForEvaluate)
							{
								plugin.economy.withdrawPlayer(p.getName(), plugin.gemManager.priceForEvaluate);
								p.sendMessage("��a[��ʯϵͳ]��e �۳���c"+String.valueOf(plugin.gemManager.priceForEvaluate)+"��e���");
								synthesis(event, value, lastLore);
							}
							else
							{
								p.sendMessage("��a[��ʯϵͳ]��c ������ʯ�����Ҳ���");
							}
						}
						else
						{
							p.sendMessage("��a[��ʯϵͳ]��c ���豦ʯ���Բ�ͬ");
						}
					}
					else
					{
						p.sendMessage("��a[��ʯϵͳ]��c ��Ч�ı�ʯ��ʯδ����");
					}
				}
				else
				{
					p.sendMessage("��a[��ʯϵͳ]��c ȱ�ٱ�ʯ");
				}
			}
		}

	}
	
	private void synthesis(InventoryClickEvent event, double value, String lastLore) 
	{
		Player p = (Player)event.getWhoClicked();
		//---------------------------------------------
		ItemStack gem = event.getInventory().getItem(19).clone();
		gem.setAmount(1);
		
		int level = 0;
		if(lastLore.contains("����"))
		{
			level = ((int)value)-1;
		}
		else
		{
			level = ((int)(value*2))-1;
		}
		
		if(Util.getRandomInt(100)<plugin.gemManager.synthesisPossibility.get(level))
		{
			_synthesis(event, gem, value);
			p.sendMessage("��a[��ʯϵͳ]��c ��ϲ���ϳɳɹ���");
			
			p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.5F, 0.0F);
			if(event.getInventory().getItem(19).getAmount()>1)
			{
				ItemStack item = event.getInventory().getItem(19);
				item.setAmount(item.getAmount()-1);
				event.getInventory().setItem(19, item);
			}
			else
				event.getInventory().setItem(19, null);
			
			if(event.getInventory().getItem(25).getAmount()>1)
			{
				ItemStack item = event.getInventory().getItem(25);
				item.setAmount(item.getAmount()-1);
				event.getInventory().setItem(25, item);
			}
			else
				event.getInventory().setItem(25, null);
			
			event.getInventory().setItem(40, gem);
		}
		else
		{
			p.sendMessage("��a[��ʯϵͳ]��5 �ϳɹ����г������⣬�ϳ�ʧ��");
			
			if(event.getInventory().getItem(19).getAmount()>1)
			{
				ItemStack item = event.getInventory().getItem(19);
				item.setAmount(item.getAmount()-1);
				event.getInventory().setItem(19, item);
			}
			else
				event.getInventory().setItem(19, null);
			
			if(event.getInventory().getItem(25).getAmount()>1)
			{
				ItemStack item = event.getInventory().getItem(25);
				item.setAmount(item.getAmount()-1);
				event.getInventory().setItem(25, item);
			}
			else
				event.getInventory().setItem(25, null);
			
			event.getInventory().setItem(40, gem);
		}
		
	}

	private void _synthesis(InventoryClickEvent event, ItemStack gem, double value) 
	{
		ItemMeta meta = gem.getItemMeta();
		List<String> loreList = new ArrayList<>();

		if(meta.getLore()!=null)
		{
			for(String lo:meta.getLore())
			{
				loreList.add(lo);
			}
		}
		
		String lore = "";
		if(loreList.get(1).contains("����"))
		{
			lore = loreList.get(1).substring(0, loreList.get(1).length()-3);
			lore += (value+1);
		}
		else
		{
			lore = loreList.get(1).substring(0, loreList.get(1).length()-3);
			lore += (value+0.5);
		}

		loreList.remove(1);
		loreList.add(lore);
		
		meta.setLore(loreList);
		gem.setItemMeta(meta);
	}
}
