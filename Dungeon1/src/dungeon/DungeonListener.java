package dungeon;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import net.milkbowl.vault.economy.Economy;

public class DungeonListener implements Listener
{
	private Dungeon plugin;

	public DungeonListener(Dungeon plugin)
	{
		this.plugin=plugin;
	}
	
	@EventHandler
	public void onPlayerClickInventory(InventoryClickEvent event)
	{
		if(event.getInventory().getTitle().equalsIgnoreCase(plugin.rewardGuiName))
		{
			event.setCancelled(true);
			if(event.getRawSlot()<event.getInventory().getSize() &&
					event.getRawSlot()>=0)
			{
				if(event.getInventory().getItem(event.getRawSlot())!=null && 
						event.getInventory().getItem(event.getRawSlot()).equals(plugin.getReward))
				{
					Player p = (Player)event.getWhoClicked();

					ItemStack reward = plugin.getReward();
					event.getInventory().setItem(event.getRawSlot(), reward);
					if(reward.getItemMeta().hasDisplayName() && 
							reward.getItemMeta().getDisplayName().contains("金币奖励:"))
					{
						double money = Double.valueOf(reward.getItemMeta().getDisplayName().split(":")[1]);

						plugin.economy.depositPlayer(p.getName(), money);
						p.sendMessage("你已获得"+money+"金钱奖励。");
						return;
					}
					
					if(p.getInventory().firstEmpty()==-1)
					{
						p.getWorld().dropItem(p.getLocation(), reward);
					}
					else
						p.getInventory().addItem(reward);
				}
			}
		}
	}

}
