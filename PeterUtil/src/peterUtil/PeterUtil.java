package peterUtil;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public class PeterUtil extends JavaPlugin
{
	public void onEnable() 
	{
		Bukkit.getConsoleSender().sendMessage("°Ïa[CheckInSystem] °ÏePeterUtilº”‘ÿÕÍ±œ");
	}

	public void onDisable() 
	{
		Bukkit.getConsoleSender().sendMessage("°Ïa[CheckInSystem] °ÏePeterUtil–∂‘ÿÕÍ±œ");
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

