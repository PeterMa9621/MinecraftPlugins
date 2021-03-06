package chatBubble;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ChatBubble extends JavaPlugin
{
	HashMap<String, String> playerData = new HashMap<String, String>();
	
	public void onEnable()
	{
	    if (!Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays"))
	    {
	    	Bukkit.getConsoleSender().sendMessage("§a[ChatBubble] §cHolographicDisplays未加载");
	    	setEnabled(false);
	    	return;
	    }
		if(!getDataFolder().exists()) 
		{
			getDataFolder().mkdir();
		}
		getServer().getPluginManager().registerEvents(new ChatBubbleListener(this), this);
		Bukkit.getConsoleSender().sendMessage("§a[ChatBubble] §e聊天气泡加载完毕");
	}

	public void onDisable() 
	{
		Bukkit.getConsoleSender().sendMessage("§a[ChatBubble] §e聊天气泡卸载完毕");
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
	
	public Inventory openGui(Player p)
	{
		Inventory inv = Bukkit.createInventory(p, 18, "§1聊§2天§3气§4泡§5颜§6色§7切§8换");
		

		ItemStack color = createItem(Material.BLUE_WOOL, 1, "§1点击切换深蓝色");
		inv.setItem(0, color);

		color = createItem(Material.GREEN_WOOL, 1, "§2点击切换深绿色");
		inv.setItem(1, color);

		color = createItem(Material.CYAN_WOOL, 1, "§3点击切换青色");
		inv.setItem(2, color);

		color = createItem(Material.RED_WOOL, 1, "§4点击切换深红色");
		inv.setItem(3, color);
		
		color = createItem(Material.PURPLE_WOOL, 1, "§5点击切换紫色");
		inv.setItem(4, color);
		
		color = createItem(Material.ORANGE_WOOL, 1, "§6点击切换橙色");
		inv.setItem(5, color);
		
		color = createItem(Material.LIGHT_GRAY_WOOL, 1, "§7点击切换灰色");
		inv.setItem(6, color);
		
		color = createItem(Material.BLACK_WOOL, 1, "§8点击切换黑色");
		inv.setItem(7, color);
		
		color = createItem(Material.LIGHT_BLUE_WOOL, 1, "§9点击切换淡蓝色");
		inv.setItem(8, color);
		
		color = createItem(Material.LIME_WOOL, 1, "§a点击切换淡绿色");
		inv.setItem(9, color);
		
		color = createItem(Material.LIGHT_BLUE_CONCRETE, 1, "§b点击切换淡青色");
		inv.setItem(10, color);
		
		color = createItem(Material.BROWN_WOOL, 1, "§c点击切换淡红色");
		inv.setItem(11, color);
		
		color = createItem(Material.PINK_WOOL, 1, "§d点击切换粉红色");
		inv.setItem(12, color);
		
		color = createItem(Material.YELLOW_WOOL, 1, "§e点击切换黄色");
		inv.setItem(13, color);
		
		color = createItem(Material.WHITE_WOOL, 1, "§f点击切换白色");
		inv.setItem(14, color);

		return inv;
	}
	
	public ItemStack createItem(Material material, int quantity, String displayName)
	{
		ItemStack item = new ItemStack(material, quantity);

		ItemMeta meta = item.getItemMeta();

		meta.setDisplayName(displayName);
		item.setItemMeta(meta);
		
		return item;
	}
	
	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args)  
	{
		if (cmd.getName().equalsIgnoreCase("cb"))
		{
			if (args.length==0)
			{
				if(sender instanceof Player)
				{
					Player p = (Player)sender;
					if(p.hasPermission("chatBubble.color"))
					{
						p.openInventory(openGui(p));
					}
					else
					{
						p.sendMessage("§6[聊天气泡] §c你没有权限这么做!");
					}
				}
				return true;
			}
			return true;
		}

		return false;
		
	}
	
	
}

