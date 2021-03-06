package vipSystem;

import java.io.File;
import java.sql.SQLException;

import org.bukkit.entity.Player;

public class VipSystemAPI 
{
	private VipSystem plugin;
	
	public VipSystemAPI(VipSystem plugin)
	{
		this.plugin=plugin;
	}
	
	public String getLeftTime(Player p) throws SQLException {
		VipPlayer vipPlayer = plugin.configLoader.loadPlayerConfig(p.getUniqueId());
		if(vipPlayer!=null)
		{
			return vipPlayer.getLeftTime();
		}
		else
		{
			return null;
		}
	}
	
	public boolean removeVip(Player p) throws SQLException {
		VipPlayer vipPlayer = plugin.configLoader.loadPlayerConfig(p.getUniqueId());
		if(vipPlayer!=null)
		{
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
	
	public String getVipGroupName(Player p) throws SQLException {
		VipPlayer vipPlayer = plugin.configLoader.loadPlayerConfig(p.getUniqueId());
		if(vipPlayer!=null)
		{
			return vipPlayer.getVipGroup();
		}
		else
		{
			return null;
		}
	}
	
	public int getLeftHour(Player p) throws SQLException {
		VipPlayer vipPlayer = plugin.configLoader.loadPlayerConfig(p.getUniqueId());
		if(vipPlayer!=null)
		{
			return vipPlayer.getLeftHours();
		}
		else
		{
			return 0;
		}
	}
}
