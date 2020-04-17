package dailyQuest;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import net.citizensnpcs.api.event.NPCRightClickEvent;

public class DailyQuestListener implements Listener
{
	private DailyQuest plugin;

	public DailyQuestListener(DailyQuest plugin)
	{
		this.plugin=plugin;
	}
	
	@EventHandler
	public void onPlayerRightClickNPC(NPCRightClickEvent event)
    {
		if(plugin.npcID.contains(event.getNPC().getId()) && 
				plugin.questPlayers.get(event.getClicker().getName()).getCurrentNumber()!=0 &&
				plugin.quests.get(plugin.questPlayers.get(event.getClicker().getName()).getWhatTheQuestIs()).getNPCId()==event.getNPC().getId())
		{
			Player p = event.getClicker();
			int id = event.getNPC().getId();
			p.openInventory(plugin.createGUI(p, event.getNPC().getName(), id));
			p.playSound(p.getLocation(), Sound.BLOCK_SNOW_PLACE, 1F, 0.0F);
			return;
		}
		
		if(event.getNPC().getId()==plugin.getQuestNPCId)
		{
			Player p = event.getClicker();
			int id = event.getNPC().getId();
			p.openInventory(plugin.getQuestGUI(p, event.getNPC().getName(), id));
			p.playSound(p.getLocation(), Sound.BLOCK_SNOW_PLACE, 1F, 0.0F);
			return;
		}
    }
}
