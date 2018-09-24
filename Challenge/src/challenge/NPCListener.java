package challenge;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.citizensnpcs.api.event.NPCRightClickEvent;

public class NPCListener implements Listener
{
	private Challenge plugin;
	
	public NPCListener(Challenge plugin)
	{
		this.plugin=plugin;
	}
	
	@EventHandler
	public void onPlayerRightClickNPC(NPCRightClickEvent event)
    {
		if(event.getNPC().getId()==plugin.NPCID)
		{
			Player p = event.getClicker();
			if(plugin.mobChallengeAppear==true || plugin.timeChallengeAppear==true)
			{
				p.openInventory(plugin.getChallengeGUI(p));
				p.playSound(p.getLocation(), Sound.BLOCK_SNOW_PLACE, 1F, 0.0F);
				return;
			}
			else
			{
				p.sendMessage("§6[挑战系统] §e现在还没到挑战时间哦");
				return;
			}
		}
    }
}
