package voucher;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.citizensnpcs.api.event.NPCRightClickEvent;

public class NPCListener implements Listener
{
	private Voucher plugin;
	
	public NPCListener(Voucher plugin)
	{
		this.plugin=plugin;
	}
	
	@EventHandler
	public void onPlayerRightClickNPC(NPCRightClickEvent event)
    {
		if(event.getNPC().getId()==plugin.npcId)
		{
			Player p = event.getClicker();
			p.openInventory(plugin.getEquipGUI(p));
			p.playSound(p.getLocation(), Sound.BLOCK_SNOW_PLACE, 1F, 0.0F);
			return;
		}

    }
}
