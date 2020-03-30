package pvpSwitch;

import org.bukkit.entity.Player;
import pvpSwitch.model.PvpPlayer;

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

		PvpPlayer pvpPlayer = plugin.playerData.get(player.getUniqueId());
		pvpPlayer.setBanned(true);
		plugin.playerData.put(player.getUniqueId(), pvpPlayer);
	}
	
	public void unbanPlayer(Player player)
	{
		if(player==null)
			return;

		PvpPlayer pvpPlayer = plugin.playerData.get(player.getUniqueId());
		pvpPlayer.setBanned(false);
		plugin.playerData.put(player.getUniqueId(), pvpPlayer);
	}
	
	public void switchOnPVP(Player player)
	{
		if(player==null)
			return;

		PvpPlayer pvpPlayer = plugin.playerData.get(player.getUniqueId());
		pvpPlayer.setPvp(true);
		plugin.playerData.put(player.getUniqueId(), pvpPlayer);
	}
	
	public void switchOffPVP(Player player)
	{
		if(player==null)
			return;

		PvpPlayer pvpPlayer = plugin.playerData.get(player.getUniqueId());
		pvpPlayer.setPvp(false);
		plugin.playerData.put(player.getUniqueId(), pvpPlayer);
	}
	
	public boolean getPVPState(Player player)
	{
		return plugin.playerData.get(player.getUniqueId()).canPvp();
	}
}

