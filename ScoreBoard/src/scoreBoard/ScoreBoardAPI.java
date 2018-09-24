package scoreBoard;

import org.bukkit.entity.Player;

public class ScoreBoardAPI 
{
	private ScoreBoard plugin;
	public ScoreBoardAPI(ScoreBoard plugin)
	{
		this.plugin=plugin;
	}
	public void stopScoreBoard(Player p)
	{
		if(plugin.task.containsKey(p.getName()))
		{
			plugin.getServer().getScheduler().cancelTask(plugin.task.get(p.getName()));
			plugin.task.remove(p.getName());
		}
	}
	
	public void restartSocreBoard(Player p)
	{
		if(!plugin.task.containsKey(p.getName()))
		{
			plugin.runTask(p);
		}
	}
}
