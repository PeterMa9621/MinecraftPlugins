package friendsHelper;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class FriendsHelper extends JavaPlugin
{
	ArrayList<String> addPlayers = new ArrayList<String>();
	HashMap<String, String> sendMessage = new HashMap<String, String>();
	
	public void onEnable() 
	{
		if(!getDataFolder().exists()) 
		{
			getDataFolder().mkdir();
		}
		getServer().getPluginManager().registerEvents(new FriendsHelperListener(this), this);
		Bukkit.getConsoleSender().sendMessage("��a[FriendsHelper] ��eFriendsHelper�������");
	}

	public void onDisable() 
	{
		Bukkit.getConsoleSender().sendMessage("��a[FriendsHelper] ��eFriendsHelperж�����");
	}

	public void loadConfig()
	{
		File file=new File(getDataFolder(),"config.yml");
		FileConfiguration config;
		if(!file.exists())
		{
			config = load(file);
			
			config.set("FriendsHelper.AddFriend.GuiTitle", "��e��ĺ���:");
			config.set("FriendsHelper.AddFriendButton.Index", 30);
			config.set("FriendsHelper.AddFriendButton.Name", "��a��Ӻ���");
			config.set("FriendsHelper.AddFriendButton.Lore", "��7�����Ӻ���");
		}
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
	
	public ItemStack createItem(int ID, int quantity, int durability, String displayName, String lore)
	{
		ItemStack item = new ItemStack(ID, quantity, (short)durability);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(displayName);
		ArrayList<String> loreList = new ArrayList<String>();
		for(String l:lore.split("%"))
		{
			loreList.add(l);
		}
		meta.setLore(loreList);
		item.setItemMeta(meta);
		
		return item;
	}
	
	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args)  
	{
		if (cmd.getName().equalsIgnoreCase("friendsHelper"))
		{
			if (args.length==0)
			{
				sender.sendMessage("��a[FriendsHelper]��6 Plugin made by Jingyuan Ma.");
				return true;
			}
			
		}
		return false;
	}
}

