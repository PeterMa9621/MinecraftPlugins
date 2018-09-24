package dps;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class DpsListener implements Listener
{
	private Dps plugin;
	
	public DpsListener(Dps plugin)
	{
		this.plugin=plugin;
	}
	
	@EventHandler
	public void onPlayerDamaged(EntityDamageByEntityEvent event)
    {
		if(event.getEntity() instanceof Player)
			return;
		if(event.getDamager() instanceof Player)
		{
			if(plugin.singleDps.containsKey(event.getDamager().getName()))
			{
				Player damager = (Player)event.getDamager();
				String teamName = plugin.singleDps.get(damager.getName());
				
				double damage = plugin.groupDps.get(teamName).get(damager.getName());

				plugin.groupDps.get(teamName).put(damager.getName(), damage + (event.getDamage()/3.0));
			}
		}
		else if(event.getDamager().getType().equals(EntityType.ARROW))
		{
			if(event.getDamager().getCustomName()==null)
				return;
			if(Bukkit.getPlayer(event.getDamager().getCustomName())==null)
				return;
			if(plugin.singleDps.containsKey(event.getDamager().getCustomName()))
			{
				Player damager = Bukkit.getPlayer(event.getDamager().getCustomName());
				
				String teamName = plugin.singleDps.get(damager.getName());
				
				double damage = plugin.groupDps.get(teamName).get(damager.getName());

				plugin.groupDps.get(teamName).put(damager.getName(), damage + (event.getDamage()/3.0));
			}
		}
    }
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event)
    {
		plugin.clearData(event.getPlayer());
    }
	
	/*
	@EventHandler
	private void onPlayerCommand(PlayerCommandPreprocessEvent event)
	{
		if(plugin.singleDps.containsKey(event.getPlayer()))
		{
			event.setCancelled(true);
			Player p = (Player)event.getPlayer();
			p.sendMessage("§a[挑战系统] §c挑战过程中你无法使用该指令");
			return;
		}
	}
	*/
	
	@EventHandler
	public void onPlayerIsShot(EntityShootBowEvent event)
    {
		if(event.getEntity() instanceof Player)
		{
			event.getProjectile().setCustomName(event.getEntity().getName());
		}
		return;
    }
}
