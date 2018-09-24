package commandDeny;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandDenyListener implements Listener
{
	private CommandDeny plugin;

	public CommandDenyListener(CommandDeny plugin)
	{
		this.plugin=plugin;
	}
	
	@EventHandler(priority=EventPriority.HIGH)
	private void onPlayerCommand(PlayerCommandPreprocessEvent event)
	{
		if(!event.getPlayer().isOp())
		{
			for(String c:plugin.command)
			{
				if(event.getMessage().contains(c))
				{
					event.setCancelled(true);
					event.getPlayer().sendMessage(plugin.message);
					return;
				}
			}
		}
    }

}
