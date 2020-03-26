package vipSystem;

import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import vipSystem.util.Util;

public class VipSystemListener implements Listener
{
	private VipSystem plugin;

	public VipSystemListener(VipSystem plugin)
	{
		this.plugin=plugin;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) throws SQLException {
		VipPlayer vipPlayer = plugin.configLoader.loadPlayerConfig(event.getPlayer().getUniqueId());

		if(vipPlayer!=null)
		{
			if(vipPlayer.isDeadline())
			{
				try {
					Util.removeVip(vipPlayer, plugin.configLoader, plugin.players);
					event.getPlayer().sendMessage("§6[会员系统] §e你的会员已到期");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
    }

}
