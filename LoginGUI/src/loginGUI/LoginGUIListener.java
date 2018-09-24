package loginGUI;

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

public class LoginGUIListener implements Listener
{
	private LoginGUI plugin;

	public LoginGUIListener(LoginGUI plugin)
	{
		this.plugin=plugin;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
    {
		loadConfig(event.getPlayer());
		
		return;
    }
	
	public void loadConfig(Player p)
	{
		File file=new File(plugin.getDataFolder(),"/player/"+p.getName()+".yml");
		FileConfiguration config;
		if (!file.exists())
		{
			config = plugin.load(file);
			
			config.set("PVP.onUse", false);
			
			try 
			{
				config.save(file);
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			loadConfig(p);
			return;
		}

		
	}
	
	@EventHandler
	public void onPlayerIsDamaged(EntityDamageByEntityEvent event)
    {
		if(event.getEntity() instanceof Player && event.getDamager() instanceof Player)
		{
			//------------------------------------------------------------------------
			File BeingDamagedfile=new File(plugin.getDataFolder(),"/player/"+((Player)event.getEntity()).getName()+".yml");
			FileConfiguration config1;
			config1 = plugin.load(BeingDamagedfile);
			//------------------------------------------------------------------------
			File Damagerfile=new File(plugin.getDataFolder(),"/player/"+((Player)event.getDamager()).getName()+".yml");
			FileConfiguration config2;
			config2 = plugin.load(Damagerfile);
			//------------------------------------------------------------------------
			boolean otherPVP = config1.getBoolean("PVP.onUse");
			boolean DamagerPVP = config2.getBoolean("PVP.onUse");
			if(DamagerPVP==false)
			{
				event.setCancelled(true);
				return;
			}
			else if(otherPVP==false)
			{
				event.setCancelled(true);
				((Player)event.getDamager()).sendMessage("§a[PVP]§3对方已关闭PVP");
				return;
			}
			
		}
		
		
		return;
    }
	

}
