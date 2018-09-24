package voucher;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import net.citizensnpcs.api.CitizensAPI;

public class VoucherListener implements Listener
{
	private Voucher plugin;
	
	int[] slot = {0,1,2,3,4,5,6,7,8,9,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,
			31,32,33,34,35,36,37,38,39,41,42,43,44};
	
	public VoucherListener(Voucher plugin)
	{
		this.plugin=plugin;
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event)
    {
		if(plugin.addLocation.containsKey(event.getPlayer().getName()))
		{
			if(event.getClickedBlock().getType()==Material.CHEST ||
					event.getClickedBlock().getType()==Material.TRAPPED_CHEST)
			{
				String locationName = plugin.addLocation.get(event.getPlayer().getName());
				plugin.addLocation.remove(event.getPlayer().getName());
				plugin.location.put(locationName, event.getClickedBlock().getLocation());
				
				Block chestBlock = event.getClickedBlock();
				
				HashMap<String, ItemStack> item = plugin.getItem(chestBlock);
				
				for(ItemStack i:item.values())
				{
					plugin.totalItem.put(i.getItemMeta().getDisplayName(), i);
				}
				
				plugin.items.put(locationName, item);
				event.getPlayer().sendMessage("§6[兑换券] §a设置完成");
			}
			else
			{
				event.getPlayer().sendMessage("§6[兑换券] §c这不是一个箱子!");
			}
		}
    }
	
	@EventHandler
	private void onPlayerClickQuestItemGUI(InventoryClickEvent event)
	{
		if(event.getInventory().getTitle().contains("§5兑换券地点:"))
		{
			event.setCancelled(true);
			Player p = (Player)event.getWhoClicked();
			if(event.getRawSlot()<0)
				return;
			if(event.getRawSlot()>=0 && event.getRawSlot()<=44 &&
					event.getInventory().getItem(event.getRawSlot())!=null)
			{
				String displayName = event.getInventory().getItem(event.getRawSlot()).getItemMeta().getDisplayName();
				
				ItemStack newVoucher = plugin.voucher.clone();
				
				ItemMeta meta = newVoucher.getItemMeta();
				List<String> lore = meta.getLore();
				
				lore.add(0, "§6兑换物品:"+displayName);
				meta.setLore(lore);
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				newVoucher.setItemMeta(meta);
				
				p.getInventory().addItem(newVoucher);
			}
		}
		if(event.getInventory().getTitle().contains("§5兑换装备"))
		{
			Player p = (Player)event.getWhoClicked();
			if(plugin.isExist(event.getRawSlot(), slot)==true)
			{
				event.setCancelled(true);
			}
			
			/*
			if(event.getRawSlot()==10)
			{
				p.sendMessage(""+event.getCurrentItem().getTypeId());
				//p.sendMessage(""+event.getCursor().getItemMeta().getDisplayName());
				if(event.getCurrentItem().getTypeId()==0 &&
						event.getCursor().getItemMeta().hasDisplayName() &&
						event.getCursor().getItemMeta().getDisplayName().equalsIgnoreCase(plugin.voucher.getItemMeta().getDisplayName()))
				{
					String name = "";
					for(String l:event.getCursor().getItemMeta().getLore())
					{
						if(l.contains("§6兑换物品:"))
						{
							name = l.split(":")[1];
							break;
						}
					}
					p.sendMessage(""+name);
					if(name!="")
					{
						p.getOpenInventory().setItem(16, plugin.totalItem.get(name));
					}
				}
				else if(event.getCursor().getTypeId()==0)
				{
					ItemStack window = plugin.createItem(160, 1, 0, " ");
					p.getOpenInventory().setItem(16, window);
				}
					
			}
			*/
			
			if(event.getRawSlot()==31)
			{
				if(event.getInventory().getItem(10)==null)
				{
					p.sendMessage("§6[兑换券] §c请放入兑换券!");
					return;
				}
				
				if(!event.getInventory().getItem(10).getItemMeta().hasDisplayName())
				{
					p.sendMessage("§6[兑换券] §c请放入正确的兑换券!");
					return;
				}
				
				if(!event.getInventory().getItem(10).getItemMeta().hasLore())
				{
					p.sendMessage("§6[兑换券] §c请放入正确的兑换券!");
					return;
				}
				
				if(!event.getInventory().getItem(10).getItemMeta().getDisplayName().equalsIgnoreCase(plugin.voucher.getItemMeta().getDisplayName()))
				{
					p.sendMessage("§6[兑换券] §c请放入正确的兑换券!");
					return;
				}
				
				if(event.getInventory().getItem(40)!=null)
				{
					p.sendMessage("§6[兑换券] §c按钮下方的位置不能放置任何物品!");
					return;
				}
				
				String name = "";
				for(String l:event.getInventory().getItem(10).getItemMeta().getLore())
				{
					if(l.contains("§6兑换物品:"))
					{
						name = l.split(":")[1];
						break;
					}
				}
				if(name!="")
				{
					if(plugin.totalItem.containsKey(name))
					{
						if(event.getClick()==ClickType.RIGHT)
						{
							if(event.getInventory().getItem(10).getAmount()==1)
								event.getInventory().setItem(10, null);
							else
							{
								ItemStack item = event.getInventory().getItem(10);
								item.setAmount(item.getAmount()-1);
								event.getInventory().setItem(10, item);
							}
							event.getInventory().setItem(40, plugin.totalItem.get(name));
							event.getInventory().setItem(16, null);
						}
						else if(event.getClick()==ClickType.LEFT)
						{
							event.getInventory().setItem(16, plugin.totalItem.get(name));
						}
					}
					else
					{
						p.sendMessage("§6[兑换券] §c这个兑换券已过期!");
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerCloseGUI(InventoryCloseEvent event)
	{
		if(event.getInventory().getTitle().equalsIgnoreCase("§5兑换装备"))
		{
			if(event.getInventory().getItem(10)!=null)
			{
				event.getPlayer().getInventory().addItem(event.getInventory().getItem(10));
			}
			if(event.getInventory().getItem(40)!=null)
			{
				event.getPlayer().getInventory().addItem(event.getInventory().getItem(40));
			}
			return;
		}
	}
	
	@EventHandler
	private void onPlayerMove(PlayerMoveEvent event)
	{

		if(plugin.waitForTp.contains(event.getPlayer().getName()))
		{
			plugin.waitForTp.remove(event.getPlayer().getName());
			event.getPlayer().sendMessage("§6[兑换券] §c传送已取消");
		}
	}
	
	@EventHandler
	private void onPlayerQuit(PlayerQuitEvent event)
	{
		if(plugin.waitForTp.contains(event.getPlayer().getName()))
			plugin.waitForTp.remove(event.getPlayer().getName());
	}
	
	@EventHandler
	public void onPlayerInteraction(PlayerInteractEvent event)
	{
		if(((event.getAction()==Action.RIGHT_CLICK_BLOCK) ||
				(event.getAction()==Action.RIGHT_CLICK_AIR)) && (event.getPlayer().isSneaking()))
		{
			if(event.getPlayer().getInventory().getItemInMainHand().getType()==null || 
					event.getPlayer().getInventory().getItemInMainHand().getType()==Material.AIR)
				return;
			if(!event.getPlayer().getInventory().getItemInMainHand().getItemMeta().hasLore())
				return;
			if(!event.getPlayer().getInventory().getItemInMainHand().getItemMeta().hasDisplayName())
				return;
			if(event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName().
					equals(plugin.voucher.getItemMeta().getDisplayName()))
			{
				for(String l:event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getLore())
				{
					if(l.contains("§6兑换物品:"))
					{
						if(plugin.waitForTp.contains(event.getPlayer().getName()))
							return;
						if(CitizensAPI.getNPCRegistry().getById(plugin.npcId)!=null)
						{
							Player player = event.getPlayer();
							player.sendMessage("§6[兑换券] §a将在3秒后将你传送到兑换商人，请站在原地不要动");
							plugin.waitForTp.add(player.getName());
							new BukkitRunnable()
							{
					    		int time;
								public void run()
								{
									if(!plugin.waitForTp.contains(player.getName()))
									{
										cancel();
									}
									if(time>3)
									{
										Location location = CitizensAPI.getNPCRegistry().getById(plugin.npcId).getStoredLocation();
										player.teleport(location);
										plugin.waitForTp.remove(player.getName());
										player.sendMessage("§6[兑换券] §2已传送到兑换商人");
										cancel();
									}
									time++;
								}
							}.runTaskTimer(plugin, 0L, 20L);
						}
					}
				}
			}
		}
	}
}
