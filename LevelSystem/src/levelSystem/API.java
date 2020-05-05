package levelSystem;

import levelSystem.model.LevelPlayer;
import org.bukkit.entity.Player;

public class API
{
	LevelSystem plugin;
	public API(LevelSystem plugin)
	{
		this.plugin = plugin;
	}
	
	public int getLevel(Player p)
	{
		if(plugin.levelPlayerManager.containsLevelPlayer(p)) {
			LevelPlayer levelPlayer = plugin.levelPlayerManager.getLevelPlayer(p);
			return levelPlayer.getLevel();
		}
		return 1;
	}
}
