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
			plugin.task.get(p.getName()).cancel();
			plugin.task.remove(p.getName());
		}
	}
	
	public void restartScoreBoard(Player p)
	{
		if(!plugin.task.containsKey(p.getName()))
		{
			plugin.runTask(p);
		}
	}
}
