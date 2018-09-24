package queueSystem;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class QueueSystemListener implements Listener
{
	private QueueSystem plugin;

	public QueueSystemListener(QueueSystem plugin)
	{
		this.plugin=plugin;
	}

	@EventHandler
	public void onPlayerClickInventory(InventoryClickEvent event)
	{
		if(event.getInventory().getTitle().contains("§e§l寻找队伍"))
		{
			event.setCancelled(true);
			Player p = (Player)event.getWhoClicked();
			if(event.getRawSlot()<0 || event.getRawSlot()>53)
				return;
			if(event.getRawSlot()==45 && event.getInventory().getItem(45)!=null)
			{
				if(plugin.queuePlayer.containsKey(p))
				{
					p.closeInventory();
					p.sendTitle("§c你已经拥有队伍了！","");
					return;
				}
				else
				{
					Queue queue = new Queue(plugin, p);
					plugin.queue.put(p, queue);
					plugin.queuePlayer.put(p, p);
					p.openInventory(plugin.manageQueueGui(p));
					
					plugin.createTeamTag(p, p);
					
					p.sendMessage("§6创建队伍成功！");
				}
			}
			else if(event.getInventory().getItem(event.getRawSlot())!=null && 
					event.getInventory().getItem(event.getRawSlot()).getItemMeta().hasDisplayName() && 
					event.getInventory().getItem(event.getRawSlot()).getItemMeta().getDisplayName().contains("§6的队伍"))
			{
				ItemStack head = event.getInventory().getItem(event.getRawSlot());
				String name = head.getItemMeta().getDisplayName().split("§3")[1].split("§6")[0];
				Player leader = Bukkit.getPlayer(name);
				if(leader==p)
				{
					p.sendMessage("§c你不能加入你自己的队伍！");
					return;
				}
				Queue queue = plugin.queue.get(leader);
				if(plugin.applyQueue(p, queue))
					leader.sendMessage("§6玩家§3"+p.getName()+"§6请求加入队伍");
				/*
				queue.add(p);
				JoinQueueEvent joinQueue = new JoinQueueEvent(plugin, p);
				Bukkit.getServer().getPluginManager().callEvent(joinQueue);
				for(Player player:queue.getPlayers())
				{
					player.sendMessage("§6玩家§3"+p.getName()+"§6已加入队伍！");
				}
				plugin.queue.put(leader, queue);
				plugin.queuePlayer.put(p, leader);
				p.closeInventory();
				p.sendMessage("§6你已经加入§3"+leader.getName()+"§6的队伍！");
				plugin.createTeamTag(p, leader);
				if(leader.getOpenInventory().getTopInventory().getTitle().equalsIgnoreCase("§e§l我的队伍"))
				{
					leader.openInventory(plugin.manageQueueGui(leader));
				}
				*/
			}
			else if(event.getRawSlot()==51)
			{
				int page = Integer.valueOf(event.getInventory().getTitle().split(":")[1]);
				if(page<=1)
					return;
				else
				{
					p.openInventory(plugin.findQueueGui(p).get(page-2));
				}
			}
			else if(event.getRawSlot()==53)
			{
				int page = Integer.valueOf(event.getInventory().getTitle().split(":")[1]);
				ArrayList<Inventory> list = plugin.findQueueGui(p);
				if(page>=list.size())
					return;
				else
				{
					p.openInventory(plugin.findQueueGui(p).get(page));
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event)
    {
		if(plugin.queuePlayer.containsKey(event.getPlayer()))
		{
			Player p = event.getPlayer();
			Player leader = plugin.queuePlayer.get(p);
			if(p!=leader)
			{
				plugin.leaveQueue(p, plugin.queue.get(leader));
				/*
				for(Queue queue:plugin.queue.values())
				{
					if(queue.getApplicant().contains(p))
						queue.removeApplicant(p);
				}
				*/
			}
			else
			{
				plugin.disbandQueue(plugin.queue.get(leader));
			}
			plugin.queuePlayer.remove(p);
		}
    }
	
	/*
	@EventHandler
	public void onPlayerLeaveQueue(QueueEvent event)
    {
		if(!event.isLeader())
		{
			Player p = event.getQueue().getLeader();
			if(p!=null)
				p.sendMessage("Test!!");
		}
		
    }
    */
}
