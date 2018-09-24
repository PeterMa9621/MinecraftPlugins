package queueSystem;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ManageQueueListener implements Listener
{
	private QueueSystem plugin;

	public ManageQueueListener(QueueSystem plugin)
	{
		this.plugin=plugin;
	}

	@EventHandler
	public void onPlayerClickInventory(InventoryClickEvent event)
	{
		if(event.getInventory().getTitle().equalsIgnoreCase("§e§l我的队伍"))
		{
			event.setCancelled(true);
			Player p = (Player)event.getWhoClicked();
			if(event.getRawSlot()<0 || event.getRawSlot()>17)
				return;
			Player leader = plugin.queuePlayer.get(p);
			Queue queue = plugin.queue.get(leader);
			if(event.getRawSlot()==9 && event.getInventory().getItem(9)!=null && p==leader)
			{
				for(Player player:queue.getPlayers())
				{
					LeaveQueueEvent leaveQueue = new LeaveQueueEvent(plugin, player);
					Bukkit.getServer().getPluginManager().callEvent(leaveQueue);
					plugin.queuePlayer.remove(player);
					if(player.getOpenInventory().getTopInventory().getTitle().equalsIgnoreCase("§e§l我的队伍"))
						player.closeInventory();
					player.sendMessage("§3你的队伍已§c解散！");
					plugin.removeTeamTag(player);
				}
				plugin.queue.remove(leader);
			}
			
			else if(event.getRawSlot()==17 && event.getInventory().getItem(17)!=null)
			{
				if(p==leader)
				{
					p.closeInventory();
					p.sendMessage("§c由于你是队长，你只能解散队伍！");
					return;
				}
				LeaveQueueEvent leaveQueue = new LeaveQueueEvent(plugin, p);
				Bukkit.getServer().getPluginManager().callEvent(leaveQueue);
				plugin.queuePlayer.remove(p);
				p.closeInventory();
				
				plugin.removeTeamTag(p);
				
				p.sendMessage("§3你已退出"+leader.getName()+"的队伍");
				
				queue.remove(p);
			}
			else if(event.getInventory().getItem(event.getRawSlot())!=null && 
					event.getInventory().getItem(event.getRawSlot()).getItemMeta().hasDisplayName() &&
					event.getInventory().getItem(event.getRawSlot()).getItemMeta().getDisplayName().contains("§6成员:"))
			{
				if(event.getClick().isRightClick() && p==leader)
				{
					String name = event.getInventory().getItem(event.getRawSlot()).getItemMeta().getDisplayName().split(":")[1];
					Player whoIsKicked;
					if(Bukkit.getPlayer(name)!=null)
					{
						whoIsKicked = Bukkit.getPlayer(name);
						LeaveQueueEvent leaveQueue = new LeaveQueueEvent(plugin, whoIsKicked);
						Bukkit.getServer().getPluginManager().callEvent(leaveQueue);
						plugin.queuePlayer.remove(whoIsKicked);
						plugin.queue.get(leader).remove(whoIsKicked);
						p.openInventory(plugin.manageQueueGui(p));
						for(Player player:plugin.queue.get(leader).getPlayers())
						{
							if(player!=whoIsKicked)
								player.sendMessage("§3"+name+"§6已被踢出队伍");
						}
						if(whoIsKicked.getOpenInventory().getTopInventory().getTitle().equalsIgnoreCase("§e§l我的队伍"))
						{
							whoIsKicked.closeInventory();
						}
						plugin.removeTeamTag(whoIsKicked);
						whoIsKicked.sendMessage("§3你已被§c踢出§3队伍！");
					}

				}
			}
		}
	}
}
