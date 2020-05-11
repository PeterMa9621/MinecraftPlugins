package betterWeapon.Gem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import betterWeapon.BetterWeapon;

public class EquipmentInlayListener implements Listener
{
	private BetterWeapon plugin;
	
	int[] slotInlay = {0,1,2,3,4,5,6,7,8,9,11,12,13,14,15,17,18,19,20,21,22,23,24,25,26,27,28
			,29,30,32,33,34,35,36,37,38,39,41,42,43,44};
	
	public EquipmentInlayListener(BetterWeapon plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerClickGUI(InventoryClickEvent event)
	{
		if(event.getView().getTitle().equalsIgnoreCase("§5装备镶嵌"))
		{
			if(plugin.isExist(event.getRawSlot(), slotInlay))
			{
				event.setCancelled(true);
			}
			
			if(event.getRawSlot()==44)
			{
				event.getWhoClicked().openInventory(plugin.gemGui.initMainGUI((Player)event.getWhoClicked()));
				return;
			}
			
			if(event.getRawSlot()==31 && 
					event.getInventory().getItem(31).getItemMeta().getDisplayName().equalsIgnoreCase("§5点击开始镶嵌"))
			{
				Player p = (Player)event.getWhoClicked();
				event.setCancelled(true);
				if(event.getInventory().getItem(40)!=null) {
					event.getWhoClicked().sendMessage("§a[宝石系统]§c 镶嵌按钮下方不允许放置任何物品！");
					return;
				}

				ItemStack equip = event.getInventory().getItem(10);
				if(equip!=null && equip.hasItemMeta() &&
						equip.getItemMeta().hasLore() && equip.getItemMeta().getLore().contains("§e[已开孔]"))
				{
					int quantity = 0;
					for(String l:equip.getItemMeta().getLore())
					{
						if(l.contains("开孔数量") || l.contains("剩余开孔"))
						{
							quantity = Integer.parseInt(l.split(":")[1].substring(2));
							break;
						}
					}
					if(quantity==0)
					{
						p.sendMessage("§a[宝石系统]§c 这个装备已经无法再镶嵌任何宝石了!");
						return;
					}
					ItemStack gem = event.getInventory().getItem(16);
					if(gem!=null) {
						if(gem.hasItemMeta() && gem.getItemMeta().hasLore()) {
							if(gem.getItemMeta().getDisplayName().equalsIgnoreCase("§6已鉴定的宝石") &&
									gem.getItemMeta().getLore().get(0).equalsIgnoreCase("§e[已鉴定]"))
							{
								ArrayList<String> weaponAllowed = new ArrayList<>(plugin.gemManager.inlayEquipment);
								if(gem.getItemMeta().getLore().get(1).contains("攻击") ||
										gem.getItemMeta().getLore().get(1).contains("暴击") ||
										gem.getItemMeta().getLore().get(1).contains("穿透"))
								{
									if(!weaponAllowed.contains(equip.getType().toString())) {
										p.sendMessage("§a[宝石系统]§c 这个宝石不能镶嵌在这个装备上");
										return;
									}
								} else {
									if(weaponAllowed.contains(equip.getType().toString())) {
										p.sendMessage("§a[宝石系统]§c 这个宝石不能镶嵌在这个装备上");
										return;
									}
								}
								int priceForInlay = plugin.gemManager.priceForInlay;
								if(plugin.economy.getBalance(p.getName())>=priceForInlay) {
									p.sendMessage("§a[宝石系统]§e 扣除§c" + priceForInlay + "§e金币");
									plugin.economy.withdrawPlayer(p.getName(), priceForInlay);
									inlay(event);
								} else {
									p.sendMessage("§a[宝石系统]§c 镶嵌所需金币不足");
								}
							} else {
								p.sendMessage("§a[宝石系统]§c 缺少宝石或无效的宝石");
							}
						} else {
							p.sendMessage("§a[宝石系统]§c 缺少宝石或无效的宝石");
						}
					} else {
						p.sendMessage("§a[宝石系统]§c 缺少宝石或无效的宝石");
					}
				} else {
					p.sendMessage("§a[宝石系统]§c 缺少装备或无效的装备");
				}
			}
		}

	}
	
	private void inlay(InventoryClickEvent event) 
	{
		Player p = (Player)event.getWhoClicked();
		//---------------------------------------------
		ItemStack equip = event.getInventory().getItem(10);
		
		String attribute = event.getInventory().getItem(16).getItemMeta().getLore().get(1).split(":")[1];
		
		_inlay(event, equip, attribute);
		p.sendMessage("§a[宝石系统]§c 恭喜，镶嵌成功！");
		
		p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.5F, 0.0F);
		event.getInventory().setItem(10, null);
		if(event.getInventory().getItem(16).getAmount()>1)
		{
			ItemStack item = event.getInventory().getItem(16);
			item.setAmount(item.getAmount()-1);
			event.getInventory().setItem(16, item);
		}
		else
			event.getInventory().setItem(16, null);
		event.getInventory().setItem(40, equip);
	}

	private void _inlay(InventoryClickEvent event, ItemStack equip, String attribute) 
	{
		ItemMeta meta = equip.getItemMeta();
		List<String> loreList = new ArrayList<>();

		if(meta.getLore()!=null)
		{
			for(String lo:meta.getLore())
			{
				loreList.add(lo);
			}
		}

		if(!loreList.isEmpty())
		{
			if(loreList.contains("§e[已开孔]"))
			{
				boolean found=false;
				for(String l:loreList)
				{
					if(l.contains("剩余开孔"))
					{
						found=true;
						break;
					}
				}

				int indexStart = loreList.indexOf("§e[已开孔]") + 1;
				if(found) {
					loreList.add(indexStart, "§2镶嵌:"+attribute);
					int leftNumberHole;
					int indexEnd = indexStart;
					
					for(int i=indexStart; !loreList.get(i).contains("剩余开孔"); i++) {
						indexEnd++;
					}
					
					leftNumberHole = Integer.parseInt(loreList.get(indexEnd).split(":")[1].substring(2));
					
					loreList.remove(indexEnd);
					loreList.add(indexEnd, "§a剩余开孔:§c"+(leftNumberHole-1));
				} else {
					int numberHole = Integer.parseInt(loreList.get(indexStart).split(":")[1].substring(2));
					loreList.remove(indexStart);

					loreList.add(indexStart, "§a剩余开孔:§c"+(numberHole-1));
					loreList.add(indexStart, "§2镶嵌:"+attribute);
				}
			}

		}
		meta.setLore(loreList);
		equip.setItemMeta(meta);
	}
}
