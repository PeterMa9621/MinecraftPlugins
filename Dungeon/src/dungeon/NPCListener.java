package dungeon;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.citizensnpcs.api.event.NPCRightClickEvent;

public class NPCListener implements Listener
{
	private Dungeon plugin;
	
	public NPCListener(Dungeon plugin)
	{
		this.plugin=plugin;
	}
	
	@EventHandler
	public void onPlayerRightClickNPC(NPCRightClickEvent event)
    {
		if(event.getNPC().getId()==plugin.NPCID)
		{
			Player p = event.getClicker();

			p.openInventory(plugin.getDungeonGUI(p));
			p.playSound(p.getLocation(), Sound.BLOCK_SNOW_PLACE, 1F, 0.0F);
			return;
		}
    }
}
