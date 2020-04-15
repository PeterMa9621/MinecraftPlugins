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
		if(plugin.players.containsKey(p.getUniqueId())) {
			LevelPlayer levelPlayer = plugin.players.get(p.getUniqueId());
			return levelPlayer.getLevel();
		}
		return 1;
	}
}
