package vipSystem;

import java.io.IOException;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import vipSystem.util.Util;

public class VipSystemListener implements Listener
{
	private VipSystem plugin;

	public VipSystemListener(VipSystem plugin)
	{
		this.plugin=plugin;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		VipPlayer vipPlayer = plugin.configLoader.loadPlayerConfig(event.getPlayer().getUniqueId());

		if(vipPlayer!=null)
		{
			if(!vipPlayer.isExpired()) {
				if(vipPlayer.checkDeadline())
				{
					try {
						Util.removeVip(vipPlayer, plugin.configLoader);
						event.getPlayer().sendMessage("§6[会员系统] §e你的会员已到期");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
    }

}
