package betterWeapon;

import java.util.ArrayList;
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
	
	BetterWeaponGemGui gemGui;
	
	public BetterWeaponGemListener(BetterWeapon plugin)
	{
		this.plugin = plugin;
		gemGui = new BetterWeaponGemGui(plugin);
	}
	
	@EventHandler
	public void onPlayerClickGUI(InventoryClickEvent event)
	{
		if(event.getInventory().getTitle().equalsIgnoreCase("§5宝石系统"))
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
				event.getWhoClicked().openInventory(plugin.initMainGUI((Player)event.getWhoClicked()));
				return;
			}
		}
		
		if(event.getInventory().getTitle().equalsIgnoreCase("§5装备开孔"))
		{
			if(plugin.isExist(event.getRawSlot(), slotHole))
			{
				event.setCancelled(true);
			}
			
			if(event.getRawSlot()==26)
			{
				event.getWhoClicked().openInventory(plugin.initMainGUI((Player)event.getWhoClicked()));
				return;
			}

			if(event.getRawSlot()==22 && 
					event.getInventory().getItem(22).getItemMeta().getDisplayName().equalsIgnoreCase("§5点击开始开孔"))
			{
				Player p = (Player)event.getWhoClicked();
				event.setCancelled(true);
				if(event.getInventory().getItem(16)!=null)
				{
					event.getWhoClicked().sendMessage("§a[宝石系统]§c 装备出产区域禁止放置任何物品！");
					return;
				}

				if(event.getInventory().getItem(10)!=null && 
						plugin.gem.get("Equipment").contains(event.getInventory().getItem(10).getTypeId()))
				{
					if(event.getInventory().getItem(10).hasItemMeta() && 
							event.getInventory().getItem(10).getItemMeta().hasLore())
					{
						if(event.getInventory().getItem(10).getItemMeta().getLore().contains("§e[已开孔]"))
						{
							p.sendMessage("§a[宝石系统]§c 这个装备已经开过孔了");
							return;
						}
					}
					
					if(plugin.economy.getBalance(p.getName())>=plugin.priceForHole)
					{
						plugin.economy.withdrawPlayer(p.getName(), plugin.priceForHole);
						p.sendMessage("§a[宝石系统]§e 扣除§c"+String.valueOf(plugin.priceForHole)+"§e金币");
						hole(event);
						return;
					}
					else
					{
						p.sendMessage("§a[宝石系统]§c 装备开孔所需金币不足");
					}
				}
				else
				{
					p.sendMessage("§a[宝石系统]§c 缺少装备或无效的装备");
				}
			}
		
		}

	}

	
	private void hole(InventoryClickEvent event) 
	{
		Player p = (Player)event.getWhoClicked();
		//---------------------------------------------
		ItemStack equip = event.getInventory().getItem(10);
		
		int quantity = plugin.random(plugin.gem.get("HolePossibility").size());

		for(int i=quantity; i>=0; i--)
		{
			if(plugin.random(100)<plugin.gem.get("HolePossibility").get(i))
			{
				_hole(event, equip, i);
				p.sendMessage("§a[宝石系统]§c 恭喜，开孔成功！");
				
				p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.5F, 0.0F);
				event.getInventory().setItem(10, null);
				event.getInventory().setItem(16, equip);
				return;
			}
		}
		p.sendMessage("§a[宝石系统]§5 开孔过程中出现意外，开孔失败");
		
		event.getInventory().setItem(10, null);
		event.getInventory().setItem(16, equip);
	}

	private void _hole(InventoryClickEvent event, ItemStack equip, int quantity) 
	{
		ItemMeta meta = equip.getItemMeta();
		ArrayList<String> loreList = new ArrayList<String>();

		if(meta.getLore()!=null)
		{
			for(String lo:meta.getLore())
			{
				loreList.add(lo);
			}
		}
		
		if(!loreList.isEmpty())
		{
			String lore = "§a开孔数量:§c"+(quantity+1)+"%§e[已开孔]";
			if(loreList.contains("§e[已开孔]"))
			{
				int index = loreList.indexOf("§e[已开孔]");
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
			String lore = "§e[已开孔]%§a开孔数量:§c"+(quantity+1);
			for(String l:lore.split("%"))
			{
				loreList.add(l);
			}
		}

		/*
		String lore = "§e[已开孔]%§a开孔数量:§c"+(quantity+1);
				
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
		
		if(event.getInventory().getTitle().equalsIgnoreCase("§5装备开孔"))
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
		
		if(event.getInventory().getTitle().equalsIgnoreCase("§5装备镶嵌"))
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
		
		if(event.getInventory().getTitle().equalsIgnoreCase("§5宝石鉴定"))
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
		
		if(event.getInventory().getTitle().equalsIgnoreCase("§5宝石合成"))
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
			return;
		}
	}
	

}
