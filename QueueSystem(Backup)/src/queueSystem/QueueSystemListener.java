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
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

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
		if(event.getInventory().getTitle().contains("��e��lѰ�Ҷ���"))
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
					p.sendTitle("��c���Ѿ�ӵ�ж����ˣ�","");
					return;
				}
				else
				{
					Queue queue = new Queue(plugin, p);
					plugin.queue.put(p, queue);
					plugin.queuePlayer.put(p, p);
					p.openInventory(plugin.manageQueueGui(p));
					
					plugin.createTeamTag(p, p);
					
					p.sendMessage("��6��������ɹ���");
				}
			}
			else if(event.getInventory().getItem(event.getRawSlot())!=null && 
					event.getInventory().getItem(event.getRawSlot()).getItemMeta().hasDisplayName() && 
					event.getInventory().getItem(event.getRawSlot()).getItemMeta().getDisplayName().contains("��6�Ķ���"))
			{
				ItemStack head = event.getInventory().getItem(event.getRawSlot());
				String name = head.getItemMeta().getDisplayName().split("��3")[1].split("��6")[0];
				Player leader = Bukkit.getPlayer(name);
				if(leader==p)
				{
					p.sendMessage("��c�㲻�ܼ������Լ��Ķ��飡");
					return;
				}
				Queue queue = plugin.queue.get(leader);
				queue.add(p);
				JoinQueueEvent joinQueue = new JoinQueueEvent(plugin, p);
				Bukkit.getServer().getPluginManager().callEvent(joinQueue);
				for(Player player:queue.getPlayers())
				{
					player.sendMessage("��6��ҡ�3"+p.getName()+"��6�Ѽ�����飡");
				}
				plugin.queue.put(leader, queue);
				plugin.queuePlayer.put(p, leader);
				p.closeInventory();
				p.sendMessage("��6���Ѿ������3"+leader.getName()+"��6�Ķ��飡");
				plugin.createTeamTag(p, leader);
				if(leader.getOpenInventory().getTopInventory().getTitle().equalsIgnoreCase("��e��l�ҵĶ���"))
				{
					leader.openInventory(plugin.manageQueueGui(leader));
				}
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
				LeaveQueueEvent leaveQueue = new LeaveQueueEvent(plugin, p);
				Bukkit.getServer().getPluginManager().callEvent(leaveQueue);
				plugin.queue.get(leader).remove(p);
				if(leader.getOpenInventory().getTopInventory().getTitle().equalsIgnoreCase("��e��l�ҵĶ���"))
					leader.openInventory(plugin.manageQueueGui(leader));
				
				plugin.removeTeamTag(p);
			}
			else
			{
				for(Player player:plugin.queue.get(leader).getPlayers())
				{
					LeaveQueueEvent leaveQueue = new LeaveQueueEvent(plugin, player);
					Bukkit.getServer().getPluginManager().callEvent(leaveQueue);
					plugin.queuePlayer.remove(player);
					if(player.getOpenInventory().getTopInventory().getTitle().equalsIgnoreCase("��e��l�ҵĶ���"))
						player.closeInventory();
					player.sendMessage("��3��Ķ����ѡ�c��ɢ��");

					plugin.removeTeamTag(player);
				}
				plugin.queue.remove(leader);
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
