package queueSystem;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ApplyListener implements Listener
{
	private QueueSystem plugin;

	public ApplyListener(QueueSystem plugin)
	{
		this.plugin=plugin;
	}

	@EventHandler
	public void onPlayerClickInventory(InventoryClickEvent event)
	{
		if(event.getInventory().getTitle().equalsIgnoreCase("��e��l�������"))
		{
			event.setCancelled(true);
			Player p = (Player)event.getWhoClicked();
			if(event.getRawSlot()<0 || event.getRawSlot()>35)
				return;
			Player leader = plugin.queuePlayer.get(p);
			Queue queue = plugin.queue.get(leader);
			if(event.getRawSlot()!= 35 && event.getInventory().getItem(event.getRawSlot())!=null && 
					event.getInventory().getItem(event.getRawSlot()).getItemMeta().hasDisplayName() &&
					event.getInventory().getItem(event.getRawSlot()).getItemMeta().getDisplayName().contains("��6�������:"))
			{
				String name = event.getInventory().getItem(event.getRawSlot()).getItemMeta().getDisplayName().split("��6�������:")[1];
				Player applicant = Bukkit.getPlayer(name);
				if(event.getClick().equals(ClickType.LEFT))
				{
					if(applicant!=null)
					{
						if(!plugin.joinQueue(applicant, queue))
						{
							leader.closeInventory();
							leader.sendMessage("��c��������ж������������Ѵ����ޣ�");
							return;
						}
						else
						{
							leader.openInventory(plugin.applyGui(p, queue));
						}
					}
					else
					{
						leader.openInventory(plugin.applyGui(p, queue));
						leader.sendMessage("��c�����Ŀǰ�����ߣ�");
						return;
					}
				}
				else if(event.getClick().equals(ClickType.RIGHT))
				{
					if(applicant!=null)
					{
						queue.removeApplicant(applicant);
						p.openInventory(plugin.applyGui(p, queue));
						leader.sendMessage("��6�Ѿܾ�����ң�");
						applicant.sendMessage("��3"+p.getName()+"��6�Ķ���ܾ������������룡");
					}
					else
					{
						leader.openInventory(plugin.applyGui(p, queue));
						leader.sendMessage("��c�����Ŀǰ�����ߣ�");
						return;
					}
				}
			}
			else if(event.getRawSlot()==35)
			{
				leader.openInventory(plugin.manageQueueGui(leader));
			}

		}
	}
}
