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
		Bukkit.getConsoleSender().sendMessage("��a[LoginGUI] ��e��¼����������");
	}

	public void onDisable() 
	{
		Bukkit.getConsoleSender().sendMessage("��a[LoginGUI] ��e��¼����ж�����");
	}

	public FileConfiguration load(File file)
	{
        if (!(file.exists())) 
        { //�����ļ�������
        	try   //��׽�쳣����Ϊ�п��ܴ������ɹ�
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
				sender.sendMessage("��a=========[PVPϵͳ]=========");
				sender.sendMessage("��a/lg open ��3��һ����");
				if(sender.isOp())
				{
					sender.sendMessage("��a/pvp on ��3ǿ�ƴ�PVP");
					sender.sendMessage("��a/pvp off ��3ǿ�ƹر�PVP");
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

