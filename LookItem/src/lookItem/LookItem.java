package lookItem;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.maxgamer.quickshop.QuickShop;

import java.io.File;
import java.io.IOException;

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

public class LookItem extends JavaPlugin
{
	QuickShop qs;
	
	private boolean hookQuickShop() 
    {
	    final Plugin plugin = this.getServer().getPluginManager().getPlugin("QuickShop");
	    qs = QuickShop.class.cast(plugin);
	    return qs != null; 
	}
	
	public void onEnable() 
	{
		if(!getDataFolder().exists()) 
		{
			getDataFolder().mkdir();
		}
		if(hookQuickShop()==false)
		{
			Bukkit.getConsoleSender().sendMessage("§a[LookItem] §cQuickShop未加载");
		}
		getServer().getPluginManager().registerEvents(new LookItemListener(this), this);
		Bukkit.getConsoleSender().sendMessage("§a[LookItem] §e查看QuickShop物品加载完成");
	}

	public void onDisable() 
	{
		Bukkit.getConsoleSender().sendMessage("§a[LookItem] §e查看QuickShop物品卸载完成");
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
	
	public Inventory createGui(Player p, ItemStack item, double itemPrice, int itemAmount)
	{
		Inventory inv = Bukkit.createInventory(p, 9, "§5查看商店物品");
		
		inv.setItem(0, item);
		
		ItemStack price = new ItemStack(Material.GOLD_NUGGET);
		ItemMeta meta = price.getItemMeta();
		meta.setDisplayName("§6价格:§c"+itemPrice+"§6/个");
		price.setItemMeta(meta);
		
		inv.setItem(8, price);
		
		ItemStack amount = new ItemStack(Material.SIGN);
		meta = amount.getItemMeta();
		meta.setDisplayName("§6剩余数量:§c"+itemAmount+"§6个");
		amount.setItemMeta(meta);
		
		inv.setItem(7, amount);
		
		return inv;
	}
	
	
	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args)  
	{
		if (cmd.getName().equalsIgnoreCase("look"))
		{
			if (args.length==0)
			{
				if(sender.isOp())
				{
					sender.sendMessage("§a=========[PVP系统]=========");
					sender.sendMessage("§a/look state §3查看当前PVP开关状态");
					sender.sendMessage("§a/look help §3查看帮助");
				}
				return true;
			}
			return true;
		}
		return false;
	}
}

