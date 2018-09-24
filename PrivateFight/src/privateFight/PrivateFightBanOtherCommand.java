package privateFight;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class PrivateFightBanOtherCommand implements Listener
{
	private PrivateFight plugin;

	public PrivateFightBanOtherCommand(PrivateFight plugin)
	{
		this.plugin=plugin;
	}
	
	@EventHandler
	private void onPlayerCommand(PlayerCommandPreprocessEvent event)
	{
		if(plugin.allFightingPlayers.containsKey(event.getPlayer().getName()))
		{
			if(!plugin.allowedCommand.contains(event.getMessage()))
			{
				event.setCancelled(true);
				Player p = (Player)event.getPlayer();
				p.sendMessage("§a[决斗系统] §c目前你无法使用该指令,决斗过程中只允许使用/pk leave指令");
				return;
			}
		}
		
	}
}
