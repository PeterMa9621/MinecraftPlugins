package eazyRepair;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import net.citizensnpcs.api.event.NPCRightClickEvent;

public class EazyRepairListener implements Listener
{
	private EazyRepair plugin;

	public EazyRepairListener(EazyRepair plugin)
	{
		this.plugin=plugin;
	}
	
	@EventHandler
	public void onPlayerRightClickNPC(NPCRightClickEvent event)
    {
		if(plugin.npcID.contains(event.getNPC().getId()))
		{
			Player p = event.getClicker();
			int id = event.getNPC().getId();
			p.openInventory(plugin.createGUI(p, event.getNPC().getName(), id));
			p.playSound(p.getLocation(), Sound.BLOCK_SNOW_PLACE, 1F, 0.0F);
		}
		return;
    }
	
	@EventHandler
	public void onPlayerCloseGUI(InventoryCloseEvent event)
	{
		if(event.getInventory().getTitle().equalsIgnoreCase(plugin.title))
		{
			Inventory inv = event.getInventory();
			Player p = (Player)event.getPlayer();
			for(int i=0; i<27; i++)
			{
				if(inv.getItem(i)!=null)
					if(inv.getItem(i).getTypeId()!=349)
						p.getInventory().addItem(inv.getItem(i));
			}
		}
	}

}
