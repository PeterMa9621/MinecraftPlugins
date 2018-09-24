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
			Bukkit.getConsoleSender().sendMessage("��a[LookItem] ��cQuickShopδ����");
		}
		getServer().getPluginManager().registerEvents(new LookItemListener(this), this);
		Bukkit.getConsoleSender().sendMessage("��a[LookItem] ��e�鿴QuickShop��Ʒ�������");
	}

	public void onDisable() 
	{
		Bukkit.getConsoleSender().sendMessage("��a[LookItem] ��e�鿴QuickShop��Ʒж�����");
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
	
	public Inventory createGui(Player p, ItemStack item, double itemPrice, int itemAmount)
	{
		Inventory inv = Bukkit.createInventory(p, 9, "��5�鿴�̵���Ʒ");
		
		inv.setItem(0, item);
		
		ItemStack price = new ItemStack(Material.GOLD_NUGGET);
		ItemMeta meta = price.getItemMeta();
		meta.setDisplayName("��6�۸�:��c"+itemPrice+"��6/��");
		price.setItemMeta(meta);
		
		inv.setItem(8, price);
		
		ItemStack amount = new ItemStack(Material.SIGN);
		meta = amount.getItemMeta();
		meta.setDisplayName("��6ʣ������:��c"+itemAmount+"��6��");
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
					sender.sendMessage("��a=========[PVPϵͳ]=========");
					sender.sendMessage("��a/look state ��3�鿴��ǰPVP����״̬");
					sender.sendMessage("��a/look help ��3�鿴����");
				}
				return true;
			}
			return true;
		}
		return false;
	}
}

