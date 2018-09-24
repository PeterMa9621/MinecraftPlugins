package moreBackpack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MoreBackpackListener implements Listener
{
	private MoreBackpack plugin;
	public MoreBackpackListener(MoreBackpack plugin)
	{
		this.plugin=plugin;
	}
	@EventHandler(priority=EventPriority.MONITOR)
	public void Inter(PlayerInteractEvent e)
	{
		if(e.getAction()==Action.RIGHT_CLICK_AIR ||
				e.getAction()==Action.RIGHT_CLICK_BLOCK)
		{
			Player player=e.getPlayer();
			ItemStack item=player.getItemInHand();
			if(item==null || item.getType()==Material.AIR)
			{
				return;
			}
			if(!item.hasItemMeta())
				return;
			if(!item.getItemMeta().hasLore())
				return;
			ArrayList<String> lore=(ArrayList<String>)item.getItemMeta().getLore();
			for(String l:lore)
			{
				if(!l.contains(":"))
					continue;
				if(!l.contains("-"))
					continue;
				String[] a=l.split(":");
				String name=a[1];
				a=a[1].split("-");
				if(!a[1].matches("[0-9]*"))
				{
					continue;
				}
				int line=plugin.getConfig().getInt("Line");
				if(line<=0)
				{
					line=1;
				}
				if(line>6)
				{
					line=6;
				}
				line=line*9;
				Inventory inv=plugin.getServer().createInventory(player, line, name);
				File file=new File(plugin.getDataFolder(),"backpacks/"+player.getName()+".yml");
				FileConfiguration config=plugin.load(file);
				if(plugin.hasOwner(a[0], player.getName()))
				{
					for(int x=0;x<line;x++)
					{
						if(!config.contains("Backpacks."+a[1]))
						{
							config.set("Backpacks."+a[1], null);
							try {
								config.save(file);
							} catch (IOException e1) {
								e1.printStackTrace();
							}
							e.getPlayer().openInventory(inv);
							return;
						}
						if(config.contains("Backpacks."+a[1]+".Items."+x))
						{
							ItemStack i=config.getItemStack("Backpacks."+a[1]+"Items."+x);
							inv.setItem(x, i);
						}
					}
				}
				e.getPlayer().openInventory(inv);
			}
		}
	}
}
