package vipSystem;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class VipSystemListener implements Listener
{
	private VipSystem plugin;

	public VipSystemListener(VipSystem plugin)
	{
		this.plugin=plugin;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
    {
		if(plugin.vipData.containsKey(event.getPlayer().getName()))
		{
			if(plugin.vipData.get(event.getPlayer().getName()).isDeadline())
			{
				plugin.vipData.remove(event.getPlayer());
				File file=new File(plugin.getDataFolder(),"/Data/"+event.getPlayer().getName()+".yml");
				if(file.exists())
					file.delete();
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "manuadd "+event.getPlayer().getName()+" "+plugin.defaultGroup);
				event.getPlayer().sendMessage("§6[会员系统] §e你的会员已到期");
			}
		}
		
		return;
    }

}
