package betterWeapon.Gem;

import java.util.ArrayList;
import java.util.Arrays;

import betterWeapon.util.GemType;
import betterWeapon.util.ItemStackUtil;
import betterWeapon.util.Util;
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
		if(event.getView().getTitle().equalsIgnoreCase("§5宝石鉴定"))
		{
			if(plugin.isExist(event.getRawSlot(), slot))
			{
				event.setCancelled(true);
			}
			
			if(event.getRawSlot()==26)
			{
				event.getWhoClicked().openInventory(plugin.gemGui.initMainGUI((Player)event.getWhoClicked()));
				return;
			}
			
			if(event.getRawSlot()==13 && 
					event.getInventory().getItem(13).getItemMeta().getDisplayName().equalsIgnoreCase("§5点击开始鉴定"))
			{
				Player p = (Player)event.getWhoClicked();
				event.setCancelled(true);
				if(event.getInventory().getItem(16)!=null)
				{
					event.getWhoClicked().sendMessage("§a[宝石系统]§c 宝石出产区域禁止放置任何物品！");
					return;
				}
				ItemStack gem = event.getInventory().getItem(10);
				if(gem!=null && ItemStackUtil.isSimilar(gem, plugin.gemManager.gemstone))
				{
					int priceForEvaluate = plugin.gemManager.priceForEvaluate;
					if(plugin.economy.getBalance(p.getName()) >= priceForEvaluate)
					{
						plugin.economy.withdrawPlayer(p.getName(), priceForEvaluate);
						p.sendMessage("§a[宝石系统]§e扣除§c" + priceForEvaluate + "§e金币");
						evaluate(event);
					}
					else
					{
						p.sendMessage("§a[宝石系统]§c 鉴定宝石所需金币不足");
					}
				}
				else
				{
					p.sendMessage("§a[宝石系统]§c 缺少未鉴定宝石或宝石已鉴定");
				}
			}
		}

	}
	
	private void evaluate(InventoryClickEvent event) 
	{
		Player p = (Player)event.getWhoClicked();
		//---------------------------------------------
		ItemStack clonedGem = event.getInventory().getItem(10).clone();
		clonedGem.setAmount(1);
		int quantity = Util.getRandomInt(plugin.gemManager.evaPossibility.size());

		for(int i=quantity; i>=0; i--)
		{
			if(Util.getRandomInt(100)<plugin.gemManager.evaPossibility.get(i))
			{
				_evaluate(event, clonedGem, i);
				p.sendMessage("§a[宝石系统]§c 恭喜，鉴定成功！");
				
				p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.5F, 0.0F);

				ItemStack gem = event.getInventory().getItem(10);
				gem.setAmount(gem.getAmount()-1);

				event.getInventory().setItem(16, clonedGem);
				return;
			}
		}
		p.sendMessage("§a[宝石系统]§5 鉴定过程中出现意外，鉴定失败，宝石碎掉了");
		
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

	private void _evaluate(InventoryClickEvent event, ItemStack gem, int level)
	{
		ItemMeta meta = gem.getItemMeta();
		ArrayList<String> loreList = new ArrayList<String>();
		
		int quantity = Util.getRandomInt(plugin.gemManager.itemPossibility.size());
		int typeIndex = 0;
		for(int i=quantity; i>=0; i--)
		{
			if(Util.getRandomInt(100)<plugin.gemManager.itemPossibility.get(i))
			{
				typeIndex = i;
				break;
			}
		}
		
		GemType gemType = plugin.gemManager.gemType.get(typeIndex);
		String type = "";
		double value = 0.0;

		if(gemType.equals(GemType.attack))
		{
			type = "攻击";
			value = (level+1)/2.0;
		}
		else if(gemType.equals(GemType.defend))
		{
			type = "防御";
			value = (level+1)/2.0;
		}
		else if(gemType.equals(GemType.block))
		{
			type = "格挡(百分比)";
			value = (level+1)/2.0;
		}
		else if(gemType.equals(GemType.crit))
		{
			type = "暴击(百分比)";
			value = (level+1);
		}
		else if(gemType.equals(GemType.penetrate))
		{
			type = "穿透";
			value = (level+1)/2.0;
		}
		
		String lore = "§e[已鉴定]%§a"+"属性:§c"+type+"+"+value;

		loreList.addAll(Arrays.asList(lore.split("%")));
		
		meta.setDisplayName("§6已鉴定的宝石");
		meta.setLore(loreList);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		gem.setItemMeta(meta);
		gem.addUnsafeEnchantment(Enchantment.LUCK, 1);
	}
}
