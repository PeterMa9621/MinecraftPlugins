package pvpSwitch;

import org.bukkit.entity.Player;

public class PVPSwitchAPI
{
	private PVPSwitch plugin;

	public PVPSwitchAPI(PVPSwitch plugin)
	{
		this.plugin=plugin;
	}

	public void banPlayer(Player player)
	{
		if(player==null)
			return;
		plugin.bannedPlayer.put(player.getName(), true);
	}
	
	public void unbanPlayer(Player player)
	{
		if(player==null)
			return;
		plugin.bannedPlayer.put(player.getName(), false);
	}
	
	public void switchOnPVP(Player player)
	{
		if(player==null)
			return;
		plugin.playerData.put(player.getName(), true);
	}
	
	public void switchOffPVP(Player player)
	{
		if(player==null)
			return;
		plugin.playerData.put(player.getName(), false);
	}
	
	public boolean getPVPState(Player player)
	{
		return plugin.playerData.get(player.getName());
	}
}

