package loginGUI;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.qianniancc.BookAPI;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class LoginGUI extends JavaPlugin
{
	BookAPI api = new BookAPI();;
	public void onEnable() 
	{
		if(!getDataFolder().exists()) 
		{
			getDataFolder().mkdir();
		}
		getServer().getPluginManager().registerEvents(new LoginGUIListener(this), this);
		Bukkit.getConsoleSender().sendMessage("§a[LoginGUI] §e登录界面加载完毕");
	}

	public void onDisable() 
	{
		Bukkit.getConsoleSender().sendMessage("§a[LoginGUI] §e登录界面卸载完毕");
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
	
	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args)  
	{
		if (cmd.getName().equalsIgnoreCase("lg"))
		{

			if (args.length==0)
			{
				sender.sendMessage("§a=========[PVP系统]=========");
				sender.sendMessage("§a/lg open §3打开一本书");
				if(sender.isOp())
				{
					sender.sendMessage("§a/pvp on §3强制打开PVP");
					sender.sendMessage("§a/pvp off §3强制关闭PVP");
				}
				return true;
			}

			if(args[0].equalsIgnoreCase("open"))
			{
				ArrayList<String> pages1 = new ArrayList<String>();;
				if(sender instanceof Player)
				{
					Player p = (Player)sender;
					ItemStack book = p.getItemInHand();
					BookMeta a = (BookMeta)book.getItemMeta();
					p.setCustomNameVisible(true);
					
					pages1.add("aaa");
					pages1.add("bbb");
					BookAPI.a(p, pages1);
				}
				return true;
			}
			
		}

		return false;
		
	}
	
	
}

