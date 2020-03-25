package pvpSwitch;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import net.citizensnpcs.api.CitizensAPI;

public class PVPSwitchListener implements Listener
{
	private PVPSwitch plugin;
	
	HashMap<String, String> test = new HashMap<String, String>();

	public PVPSwitchListener(PVPSwitch plugin)
	{
		this.plugin=plugin;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
    {
		if(!plugin.playerData.containsKey(event.getPlayer().getName()))
		{
			plugin.playerData.put(event.getPlayer().getName(), false);
		}
		
		if(!plugin.bannedPlayer.containsKey(event.getPlayer().getName()))
		{
			plugin.bannedPlayer.put(event.getPlayer().getName(), false);
		}
		return;
    }
	
	@EventHandler
	public void onPlayerIsDamaged(EntityDamageByEntityEvent event)
    {
		if(event.getEntity() instanceof Player && event.getDamager() instanceof Player)
		{
			if(((Player)event.getDamager()).hasPermission("pvpSwitch.bypass"))
			{
				return;
			}

			boolean otherPVP = false;
			boolean DamagerPVP = false;
			if(plugin.playerData.containsKey(((Player)event.getEntity()).getName()))
			{
				otherPVP = plugin.playerData.get(((Player)event.getEntity()).getName());
			}
			if(plugin.playerData.containsKey(((Player)event.getDamager()).getName()))
			{
				DamagerPVP = plugin.playerData.get(((Player)event.getDamager()).getName());
			}
			/*
			if(plugin.getClanManager().getClanByPlayerUniqueId(((Player)event.getDamager()).getUniqueId())!=null)
			{
				if(plugin.getClanManager().getClanPlayer(((Player)event.getDamager()).getUniqueId()).isFriendlyFire()==false)
				{
					event.setCancelled(true);
					return;
				}
			}
			else if(plugin.getClanManager().getClanByPlayerUniqueId(((Player)event.getEntity()).getUniqueId())!=null)
			{
				if(plugin.getClanManager().getClanPlayer(((Player)event.getEntity()).getUniqueId()).isFriendlyFire()==false)
				{
					event.setCancelled(true);
					return;
				}
			}
			*/
			if(DamagerPVP==false)
			{
				event.setCancelled(true);
				event.setDamage(0);
				return;
			}
			else if(otherPVP==false)
			{
				event.setCancelled(true);
				event.setDamage(0);
				((Player)event.getDamager()).sendMessage("§a[PVP]§3 对方已关闭PVP");
				return;
			}
			
		}
		else if(event.getEntity() instanceof Player && event.getDamager().getType().equals(EntityType.ARROW))
		{
			if(CitizensAPI.getNPCRegistry().isNPC(event.getEntity()))
				return;
			Player p = (Player)event.getEntity();

			if(event.getDamager().getCustomName()==null)
				return;
			if(Bukkit.getPlayer(event.getDamager().getCustomName())==null)
				return;
			Player damager = Bukkit.getPlayer(event.getDamager().getCustomName());
			if(damager.hasPermission("pvpSwitch.bypass"))
			{
				return;
			}

			boolean otherPVP = false;
			boolean DamagerPVP = false;
			if(plugin.playerData.containsKey(p.getName()))
			{
				otherPVP = plugin.playerData.get(p.getName());
			}
			if(plugin.playerData.containsKey(damager.getName()))
			{
				DamagerPVP = plugin.playerData.get(damager.getName());
			}
			
			if(DamagerPVP==false)
			{
				event.setCancelled(true);
				event.setDamage(0);
				return;
			}
			else if(otherPVP==false)
			{
				event.setCancelled(true);
				event.setDamage(0);
				damager.sendMessage("§a[PVP]§3 对方已关闭PVP");
				return;
			}
		}
		return;
    }
	
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
