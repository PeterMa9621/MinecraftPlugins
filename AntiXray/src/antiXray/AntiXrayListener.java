package antiXray;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

public class AntiXrayListener implements Listener
{
	private AntiXray plugin;
	
	public AntiXrayListener(AntiXray plugin)
	{
		this.plugin=plugin;
	}
	
	public boolean isExist(int number, ArrayList<Integer> numberList)
	{
		for(int i=0; i<numberList.size(); i++)
		{
			if(number==numberList.get(i))
			{
				return true;
			}
		}
		return false;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
    {
		plugin.loadPlayerConfig(event.getPlayer().getUniqueId());
    }
	
	@EventHandler
	public void onPlayerBreakBlock(BlockBreakEvent event)
	{
		if(event.getPlayer().hasPermission("antiXray.bypass"))
		{
			return;
		}
		
		if(event.getPlayer().hasPermission("antiXray.op"))
		{
			return;
		}
		
		if(!plugin.worlds.containsKey(event.getPlayer().getWorld().getName()))
		{
			return;
		}
		
		String worldName = event.getPlayer().getWorld().getName();
		Worlds world = plugin.worlds.get(worldName);
		
		if(event.getBlock().getLocation().getBlockY()>world.getHeightLimit())
		{
			return;
		}

		if(!plugin.tools.contains(event.getPlayer().getInventory().getItemInMainHand().getType()))
		{
			return;
		}
		
		Player p = event.getPlayer();
		Material blockID = event.getBlock().getType();

		int currentPoint = plugin.playerData.get(p.getUniqueId());

		if(world.getRecoverInfo().containsKey(blockID))
		{
			if(world.getRecoverOnUse())
			{
				if(currentPoint+world.getRecoverInfo().get(blockID)<=plugin.totalPoints)
				{
					currentPoint += world.getRecoverInfo().get(blockID);
				}
			}
		}
		else if(world.getBlockInfo().containsKey(blockID))
		{
			if((currentPoint-world.getBlockInfo().get(blockID))>=0)
			{
				currentPoint -= world.getBlockInfo().get(blockID);
			}
			else
			{
				event.setCancelled(true);
				String msg = plugin.message.get("DontHaveEnoughPoints");
				if(msg.contains("{points}"))
					msg = msg.replace("{points}", String.valueOf(currentPoint));
				p.sendMessage(msg);
				if(plugin.notify)
				{
					for(String name:plugin.op)
					{
						if(Bukkit.getServer().getPlayer(name)!=null)
						{
							Bukkit.getServer().getPlayer(name).sendMessage("§a[反透视系统] §6玩家§c"+p.getName()+"§6已用完挖矿点数");
						}
					}
				}
				return;
			}
		}
		else
		{
			return;
		}

		plugin.playerData.put(p.getUniqueId(), currentPoint);

	}
	
	@EventHandler
	public void onPlayerInteraction(PlayerInteractEvent event)
	{
		Action action = event.getAction();
		if(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)
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
			if(!item.getItemMeta().getDisplayName().equals(plugin.recoverItem.getItemMeta().getDisplayName()))
				return;
			if(!item.getItemMeta().getLore().equals(plugin.recoverItem.getItemMeta().getLore()))
				return;

			int currentPoint = plugin.playerData.get(p.getName());
			if(currentPoint>=plugin.totalPoints)
			{
				String msg = plugin.message.get("AlreadyHadEnoughPoints");
				p.sendMessage(msg);
				return;
			}
			
			if(currentPoint+plugin.recoverPoint>plugin.totalPoints)
			{
				currentPoint = plugin.totalPoints;
			}
			else
			{
				currentPoint += plugin.recoverPoint;
			}
			plugin.playerData.put(p.getUniqueId(), currentPoint);
			
			String msg = plugin.message.get("RecoverPoints");
			if(msg.contains("{recoverpoints}"))
				msg = msg.replace("{recoverpoints}", String.valueOf(plugin.recoverPoint));
			p.sendMessage(msg);
			int amount = p.getInventory().getItemInMainHand().getAmount();
			if(amount>1)
			{
				ItemStack recoverItem = p.getInventory().getItemInMainHand();
				recoverItem.setAmount(amount-1);
				p.getInventory().setItemInMainHand(recoverItem);
			}
			else
			{
				p.getInventory().setItemInMainHand(null);
			}
		}

	}
}
