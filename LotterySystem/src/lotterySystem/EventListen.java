package lotterySystem;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import lotterySystem.EventListen;

public class EventListen implements Listener
{
	private LotterySystem plugin;
	private int time=1;
	public EventListen (LotterySystem plugin)
	{
		this.plugin=plugin;
	}
	
	@EventHandler
	public void onPlayerInteraction(PlayerInteractEvent event)
	{
		Action action = event.getAction();
		if(action == Action.RIGHT_CLICK_AIR)
		{
			Player p = event.getPlayer();
			ItemStack item = p.getInventory().getItemInMainHand();
			if(item==null || item.getType()==Material.AIR)
				return;
			if(!item.hasItemMeta())
				return;
			if(!item.getItemMeta().hasLore())
				return;
			if(!item.getItemMeta().hasDisplayName())
				return;
			if(!item.getItemMeta().getDisplayName().equals(plugin.egg.getItemMeta().getDisplayName()))
				return;
			if(p.getInventory().firstEmpty()<0)
			{
				p.sendMessage("§e你身上§c满了§e，请先清理背包");
				return;
			}
			ArrayList<String> lore=(ArrayList<String>)item.getItemMeta().getLore();
			time=1;
			for(String l:lore)
			{
				for(String ml:plugin.mainLore)
				{
					if(!l.equalsIgnoreCase(ml))
						continue;
				}
				int result = plugin.lottery(plugin.items);
				p.getInventory().addItem(plugin.items.get(result));
				String name = "";
				if(plugin.items.get(result).getItemMeta().getDisplayName()==null)
					name = plugin.items.get(result).getData().toString();
				else
					name = plugin.items.get(result).getItemMeta().getDisplayName();
				p.sendMessage("§a你得到了"+plugin.items.get(result).getAmount()+"个"+name);
				p.playSound(p.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0F, 0.0F);
				ItemStack itemInHand = p.getInventory().getItemInMainHand();
				if(itemInHand.getAmount()>1)
				{
					itemInHand.setAmount(itemInHand.getAmount()-1);
				}
				else
				{
					p.getInventory().removeItem(itemInHand);
				}
				return;
			}
		}
		if(action == Action.RIGHT_CLICK_BLOCK)
		{
			if (time==1)
			{
				Player p = event.getPlayer();
				ItemStack item = p.getInventory().getItemInMainHand();
				if(item==null || item.getType()==Material.AIR)
					return;
				if(!item.hasItemMeta())
					return;
				if(!item.getItemMeta().hasLore())
					return;
				if(!item.getItemMeta().hasDisplayName())
					return;
				if(!item.getItemMeta().getDisplayName().equals(plugin.egg.getItemMeta().getDisplayName()))
					return;
				if(p.getInventory().firstEmpty()<0)
				{
					p.sendMessage("§e你身上§c满了§e，请先清理背包");
					return;
				}
				ArrayList<String> lore=(ArrayList<String>)item.getItemMeta().getLore();
				time=2;
				for(String l:lore)
				{
					for(String ml:plugin.mainLore)
					{
						if(!l.equalsIgnoreCase(ml))
							continue;
					}
					int result = plugin.lottery(plugin.items);
					p.getInventory().addItem(plugin.items.get(result));
					String name = "";
					if(plugin.items.get(result).getItemMeta().getDisplayName()==null)
						name = plugin.items.get(result).getData().toString();
					else
						name = plugin.items.get(result).getItemMeta().getDisplayName();
					p.sendMessage("§a你得到了"+plugin.items.get(result).getAmount()+"个"+name);
					p.playSound(p.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0F, 0.0F);
					ItemStack itemInHand = p.getInventory().getItemInMainHand();
					if(itemInHand.getAmount()>1)
					{
						itemInHand.setAmount(itemInHand.getAmount()-1);
					}
					else
					{
						p.getInventory().removeItem(itemInHand);
					}
					return;
				}
			}
			else
			{
				time=1;
			}
			
		}
	}
	/*
	private void guiTask(InventoryClickEvent event) 
	{
		Bukkit.getScheduler().runTaskTimer((Plugin)this, new Runnable()
		{
			public void run()
			{
				int i=0;
				event.getInventory().setItem(9+i, item);
				i+=1;
				if(i==17)
				{
					i=0;
				}
			}
		}, 4*20, 4*20);

	}
	*/
	
}

