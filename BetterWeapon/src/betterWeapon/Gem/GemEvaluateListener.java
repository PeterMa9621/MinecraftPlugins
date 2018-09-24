package betterWeapon.Gem;

import java.util.ArrayList;

import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import betterWeapon.BetterWeapon;


public class GemEvaluateListener implements Listener
{
	private BetterWeapon plugin;
	
	int[] slot = {0,1,2,3,4,5,6,7,8,9,11,12,13,14,15,17,18,19,20,21,22,23,24,25,26};
	
	public GemEvaluateListener(BetterWeapon plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerClickGUI(InventoryClickEvent event)
	{
		if(event.getInventory().getTitle().equalsIgnoreCase("��5��ʯ����"))
		{
			if(plugin.isExist(event.getRawSlot(), slot))
			{
				event.setCancelled(true);
			}
			
			if(event.getRawSlot()==26)
			{
				event.getWhoClicked().openInventory(plugin.initMainGUI((Player)event.getWhoClicked()));
				return;
			}
			
			if(event.getRawSlot()==13 && 
					event.getInventory().getItem(13).getItemMeta().getDisplayName().equalsIgnoreCase("��5�����ʼ����"))
			{
				Player p = (Player)event.getWhoClicked();
				event.setCancelled(true);
				if(event.getInventory().getItem(16)!=null)
				{
					event.getWhoClicked().sendMessage("��a[��ʯϵͳ]��c ��ʯ���������ֹ�����κ���Ʒ��");
					return;
				}
				if(event.getInventory().getItem(10)!=null && 
						event.getInventory().getItem(10).getItemMeta().equals(plugin.gemstone.getItemMeta()))
				{
					if(plugin.economy.getBalance(p.getName())>=plugin.priceForEvaluate)
					{
						plugin.economy.withdrawPlayer(p.getName(), plugin.priceForEvaluate);
						p.sendMessage("��a[��ʯϵͳ]��e�۳���c"+String.valueOf(plugin.priceForEvaluate)+"��e���");
						evaluate(event);
						return;
					}
					else
					{
						p.sendMessage("��a[��ʯϵͳ]��c ������ʯ�����Ҳ���");
					}
				}
				else
				{
					p.sendMessage("��a[��ʯϵͳ]��c ȱ��δ������ʯ��ʯ�Ѽ���");
				}
			}
		}

	}
	
	private void evaluate(InventoryClickEvent event) 
	{
		Player p = (Player)event.getWhoClicked();
		//---------------------------------------------
		ItemStack equip = event.getInventory().getItem(10).clone();
		equip.setAmount(1);
		int quantity = plugin.random(plugin.gem.get("EvaPossibility").size());

		for(int i=quantity; i>=0; i--)
		{
			if(plugin.random(100)<plugin.gem.get("EvaPossibility").get(i))
			{
				_evaluate(event, equip, i);
				p.sendMessage("��a[��ʯϵͳ]��c ��ϲ�������ɹ���");
				
				p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.5F, 0.0F);
				if(event.getInventory().getItem(10).getAmount()>1)
				{
					ItemStack item = event.getInventory().getItem(10);
					item.setAmount(item.getAmount()-1);
					event.getInventory().setItem(10, item);
				}
				else
					event.getInventory().setItem(10, null);
				event.getInventory().setItem(16, equip);
				return;
			}
		}
		p.sendMessage("��a[��ʯϵͳ]��5 ���������г������⣬����ʧ�ܣ���ʯ�����");
		
		if(event.getInventory().getItem(10).getAmount()>1)
		{
			ItemStack item = event.getInventory().getItem(10);
			item.setAmount(item.getAmount()-1);
			event.getInventory().setItem(10, item);
		}
		else
			event.getInventory().setItem(10, null);
		event.getInventory().setItem(16, null);
	}

	private void _evaluate(InventoryClickEvent event, ItemStack equip, int level) 
	{
		ItemMeta meta = equip.getItemMeta();
		ArrayList<String> loreList = new ArrayList<String>();
		
		int quantity = plugin.random(plugin.gem.get("ItemPossibility").size());
		int typeIndex = 0;
		for(int i=quantity; i>=0; i--)
		{
			if(plugin.random(100)<plugin.gem.get("ItemPossibility").get(i))
			{
				typeIndex = i;
				break;
			}
		}
		
		String type = plugin.gemType.get(typeIndex);
		double value = 0.0;

		if(type.equalsIgnoreCase("attack"))
		{
			type = "����";
			value = (level+1)/2.0;
		}
		else if(type.equalsIgnoreCase("defend"))
		{
			type = "����";
			value = (level+1)/2.0;
		}
		else if(type.equalsIgnoreCase("block"))
		{
			type = "��(�ٷֱ�)";
			value = (level+1)/2.0;
		}
		else if(type.equalsIgnoreCase("crit"))
		{
			type = "����(�ٷֱ�)";
			value = (level+1);
		}
		else if(type.equalsIgnoreCase("penetrate"))
		{
			type = "��͸";
			value = (level+1)/2.0;
		}
		
		String lore = "��e[�Ѽ���]%��a"+"����:��c"+type+"+"+value;
		
		for(String l:lore.split("%"))
		{
			loreList.add(l);
		}
		
		meta.setDisplayName("��6�Ѽ����ı�ʯ");
		meta.setLore(loreList);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		equip.setItemMeta(meta);
		equip.addUnsafeEnchantment(Enchantment.LUCK, 1);
	}
}
