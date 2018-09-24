package scoreBoard;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ScoreBoardListener implements Listener
{
	private ScoreBoard plugin;
	

	public ScoreBoardListener(ScoreBoard plugin)
	{
		this.plugin=plugin;
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event)
    {
		if(plugin.task.containsKey(event.getPlayer().getName()))
		{
			plugin.getServer().getScheduler().cancelTask(plugin.task.get(event.getPlayer().getName()));
			plugin.task.remove(event.getPlayer().getName());
		}
    }
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
    {
		plugin.runTask(event.getPlayer());
		return;
    }
	

}
