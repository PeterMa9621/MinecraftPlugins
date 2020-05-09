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
		if(event.getView().getTitle().equalsIgnoreCase("§5宝石合成"))
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
					event.getInventory().getItem(31).getItemMeta().getDisplayName().equalsIgnoreCase("§5点击开始合成"))
			{
				Player p = (Player)event.getWhoClicked();
				event.setCancelled(true);
				if(event.getInventory().getItem(40)!=null)
				{
					event.getWhoClicked().sendMessage("§a[宝石系统]§c 宝石出产区域禁止放置任何物品！");
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
							p.sendMessage("§a[宝石系统] §c缺少已鉴定的宝石");
							return;
						}
						
						if((!event.getInventory().getItem(19).getItemMeta().getLore().contains("§e[已鉴定]")) &&
								(!event.getInventory().getItem(25).getItemMeta().getLore().contains("§e[已鉴定]")))
						{
							p.sendMessage("§a[宝石系统] §c缺少已鉴定的宝石");
							return;
						}
						if(event.getInventory().getItem(19).getItemMeta().getLore().equals(event.getInventory().getItem(25).getItemMeta().getLore()))
						{
							String lastLore = event.getInventory().getItem(19).getItemMeta().getLore().get(1);
							Double value = Double.valueOf(lastLore.substring(lastLore.length()-3));
							if(lastLore.contains("暴击"))
							{
								if(value>=6.0)
								{
									p.sendMessage("§a[宝石系统] §c这个宝石属性已经达到最大，无法继续合成");
									return;
								}
							}
							else
							{
								if(value>=3.0)
								{
									p.sendMessage("§a[宝石系统] §c这个宝石属性已经达到最大，无法继续合成");
									return;
								}
							}
							
							if(plugin.economy.getBalance(p.getName())>=plugin.gemManager.priceForEvaluate)
							{
								plugin.economy.withdrawPlayer(p.getName(), plugin.gemManager.priceForEvaluate);
								p.sendMessage("§a[宝石系统]§e 扣除§c"+String.valueOf(plugin.gemManager.priceForEvaluate)+"§e金币");
								synthesis(event, value, lastLore);
							}
							else
							{
								p.sendMessage("§a[宝石系统]§c 鉴定宝石所需金币不足");
							}
						}
						else
						{
							p.sendMessage("§a[宝石系统]§c 所需宝石属性不同");
						}
					}
					else
					{
						p.sendMessage("§a[宝石系统]§c 无效的宝石或宝石未鉴定");
					}
				}
				else
				{
					p.sendMessage("§a[宝石系统]§c 缺少宝石");
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
		if(lastLore.contains("暴击"))
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
			p.sendMessage("§a[宝石系统]§c 恭喜，合成成功！");
			
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
			p.sendMessage("§a[宝石系统]§5 合成过程中出现意外，合成失败");
			
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
		if(loreList.get(1).contains("暴击"))
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
