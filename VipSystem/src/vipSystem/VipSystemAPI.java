package vipSystem;

import java.io.File;

import org.bukkit.entity.Player;

public class VipSystemAPI 
{
	private VipSystem plugin;
	
	public VipSystemAPI(VipSystem plugin)
	{
		this.plugin=plugin;
	}
	
	public String getLeftTime(Player p)
	{
		if(plugin.vipData.containsKey(p.getName()))
		{
			return plugin.vipData.get(p.getName()).getLeftTime();
		}
		else
		{
			return null;
		}
	}
	
	public boolean removeVip(Player p)
	{
		if(plugin.vipData.containsKey(p.getName()))
		{
			plugin.vipData.remove(p.getName());
			File file=new File(plugin.getDataFolder(),"/Data/"+p.getName()+".yml");
			if(file.exists())
				file.delete();
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public String getVipGroupName(Player p)
	{
		if(plugin.vipData.containsKey(p.getName()))
		{
			VipPlayer vipPlayer;
			vipPlayer = plugin.vipData.get(p.getName());
			return plugin.vipGroups.get(vipPlayer.getVipGroup());
		}
		else
		{
			return null;
		}
	}
	
	public int getLeftHour(Player p)
	{
		if(plugin.vipData.containsKey(p.getName()))
		{
			VipPlayer vipPlayer;
			vipPlayer = plugin.vipData.get(p.getName());
			return vipPlayer.getLeftHours();
		}
		else
		{
			return 0;
		}
	}
}
