package peterUtil;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import peterUtil.config.ConfigManager;

import java.util.ArrayList;

public class PeterUtil extends JavaPlugin
{
	public static PeterUtil peterUtil;
	public static ConfigManager configManager;
	public void onEnable() 
	{
		peterUtil = this;
		configManager = new ConfigManager(this);
		configManager.loadConfig();
		Bukkit.getConsoleSender().sendMessage("§a[PeterUtil] §ePeterUtil加载完毕");
	}

	public void onDisable() 
	{
		Bukkit.getConsoleSender().sendMessage("§a[PeterUtil] §ePeterUtil卸载完毕");
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (cmd.getName().equalsIgnoreCase("peterutil") && sender.isOp())
		{
			if (args.length==0)
			{
				sender.sendMessage("§a=========[PeterUtil]=========");
				sender.sendMessage("§a/peterutil reload §3重载配置");

				return true;
			}

			if(args[0].equalsIgnoreCase("reload")){
				configManager.loadConfig();
				sender.sendMessage("§a[PeterUtil] §e配置重载成功");
			}

			return true;
		}
		return false;
	}

	public ItemStack createItem(String ID, int quantity, String displayName, String lore)
	{
		ItemStack item = new ItemStack(Material.getMaterial(ID.toUpperCase()), quantity);
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
	
	public ItemStack createItem(String ID, int quantity, String displayName)
	{
		ItemStack item = new ItemStack(Material.getMaterial(ID), quantity);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(displayName);
		item.setItemMeta(meta);
		
		return item;
	}
	
	public ItemStack createItem2(String ID, int quantity, String lore)
	{
		ItemStack item = new ItemStack(Material.getMaterial(ID), quantity);
		ItemMeta meta = item.getItemMeta();
		ArrayList<String> loreList = new ArrayList<String>();
		for(String l:lore.split("%"))
		{
			loreList.add(l);
		}
		meta.setLore(loreList);
		item.setItemMeta(meta);
		
		return item;
	}
}

