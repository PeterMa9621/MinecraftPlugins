package 模版;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.Permission;

public class 模版 extends JavaPlugin
{
	public void onEnable() 
	{
		if(!getDataFolder().exists()) 
		{
			getDataFolder().mkdir();
		}
		getServer().getPluginManager().registerEvents(new 模版Listener(this), this);
        getLogger().info("Finish loading");
	}

	public void onDisable() 
	{
		getLogger().info("Unload");
	}

	public FileConfiguration load(File file)
	{
        if (!(file.exists())) 
        { //假如文件不存在
        	try   //捕捉异常，因为有可能创建不成功
        	{
        		file.createNewFile();
        	}
        	catch(IOException e)
        	{
        		e.printStackTrace();
        	}
        }
        return YamlConfiguration.loadConfiguration(file);
	}
	public FileConfiguration load(String path)
	{
		File file=new File(path);
		if(!file.exists())
		{
			try
		{
				file.createNewFile();
		}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		return YamlConfiguration.loadConfiguration(new File(path));
	}
	
	//p.getWorld().spawnParticle(Particle.FLAME, l, 0);
	/*
	for (double i = 0.3D; i < 0.6D; i += 0.3D) 
	{
		for (double o = 0D; o <= 360.0D; o += 10.0D)
	    {
	        double x = i * (Math.cos(o / 180.0D * 3.141592653589793D) * (-Math.cos((90.0F - l.clone().getPitch() + 90.0F) / 180.0F * 3.141592653589793D) * Math.sin(l.clone().getYaw() / 180.0F * 3.141592653589793D))) + i * (Math.sin(o / 180.0D * 3.141592653589793D) * -Math.sin((l.clone().getYaw() - 90.0F) / 180.0F * 3.141592653589793D));
	        double y = 0.8D + i * (Math.cos(o / 180.0D * 3.141592653589793D) * Math.sin((90.0F - l.clone().getPitch() + 90.0F) / 180.0F * 3.141592653589793D));
	        double z = i * (Math.cos(o / 180.0D * 3.141592653589793D) * (Math.cos((90.0F - l.clone().getPitch() + 90.0F) / 180.0F * 3.141592653589793D) * Math.cos(l.clone().getYaw() / 180.0F * 3.141592653589793D)) + Math.sin(o / 180.0D * 3.141592653589793D) * Math.cos((l.clone().getYaw() - 90.0F) / 180.0F * 3.141592653589793D));
	        Location Location = l.clone().add(x, y, z);
	        l.clone().getWorld().spawnParticle(Particle.FLAME, Location, 0);
	    }
	}
	*/
	
	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args)  
	{
		if (cmd.getName().equalsIgnoreCase("pvp"))
		{
			File file=new File(getDataFolder(), "/player/"+sender.getName()+".yml");
			FileConfiguration config;
			config = load(file);
			if (args.length==0)
			{
				if(sender instanceof Player)
				{
					
					if(config.getBoolean("PVP.onUse")==false)
					{
						sender.sendMessage("§a[PVP]§3PVP已打开");
						config.set("PVP.onUse", true);
					}
					else
					{
						sender.sendMessage("§a[PVP]§3PVP已关闭");
						config.set("PVP.onUse", false);
					}
					try 
					{
						config.save(file);
					} 
					catch (IOException e) 
					{
						e.printStackTrace();
					}
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("help"))
			{
				sender.sendMessage("§a=========[PVP系统]=========");
				sender.sendMessage("§a/pvp state §3查看当前PVP开关状态");
				sender.sendMessage("§a/pvp help §3查看帮助");
				if(sender.isOp())
				{
					sender.sendMessage("§a/pvp on §3强制打开PVP");
					sender.sendMessage("§a/pvp off §3强制关闭PVP");
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("on"))
			{
				config.set("PVP.onUse", true);
				try {
					config.save(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("off"))
			{
				config.set("PVP.onUse", false);
				try {
					config.save(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("state"))
			{
				if(config.getBoolean("PVP.onUse")==false)
					sender.sendMessage("§a[PVP]§3你的PVP为关闭状态");
				else
					sender.sendMessage("§a[PVP]§3你的PVP为打开状态");
				return true;
			}
			
		}

		return false;
		
	}
	
	
}

