package dps;

import dps.api.DpsAPI;
import dps.listener.DpsListener;
import dps.rewardBox.RewardBoxListener;
import dps.util.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Dps extends JavaPlugin
{
	DpsAPI api = new DpsAPI(this);
	
	public void onEnable() 
	{
		if(!getDataFolder().exists()) 
		{
			getDataFolder().mkdir();
		}

		ConfigUtil.loadConfig(this);
		getServer().getPluginManager().registerEvents(new DpsListener(this), this);
		getServer().getPluginManager().registerEvents(new RewardBoxListener(this), this);
		Bukkit.getConsoleSender().sendMessage("§a[Dps] §eDps已加载");
	}

	public void onDisable() 
	{
		Bukkit.getConsoleSender().sendMessage("§a[Dps] §eDps已卸载");
	}

	public DpsAPI getAPI() {
		return api;
	}
	
	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args)  
	{
		if (cmd.getName().equalsIgnoreCase("dps"))
		{
			if (args.length==0)
			{
				if(sender.isOp())
				{
					sender.sendMessage("§a=========[DPS系统]=========");
					sender.sendMessage("§a/dps reload §3重载配置");
				}
				return true;
			}

			if(args[0].equalsIgnoreCase("reload")){
				ConfigUtil.loadConfig(this);
				sender.sendMessage("§a[Dps] §e配置重载成功");
			}

			return true;
		}
		return false;
	}
}
