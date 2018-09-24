package levelSystem;

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
		if(plugin.player.containsKey(p.getName()))
		{
			PlayerData pd = plugin.player.get(p.getName());
			return pd.getLevel();
		}
		return 1;
	}
}
