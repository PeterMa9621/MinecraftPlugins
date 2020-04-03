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
import pvpSwitch.model.PvpPlayer;

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
		plugin.loadPlayerConfig(event.getPlayer().getUniqueId());
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
			boolean damagerPVP = false;
			if(plugin.playerData.containsKey((event.getEntity()).getUniqueId()))
			{
				PvpPlayer otherPvpPlayer = plugin.playerData.get((event.getEntity()).getUniqueId());
				otherPVP = otherPvpPlayer.canPvp();
			}
			if(plugin.playerData.containsKey((event.getDamager()).getUniqueId()))
			{
				PvpPlayer damagerPvpPlayer = plugin.playerData.get(((Player)event.getDamager()).getUniqueId());
				damagerPVP = damagerPvpPlayer.canPvp();
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
			if(!damagerPVP)
			{
				event.setCancelled(true);
				event.setDamage(0);
				return;
			}
			else if(!otherPVP)
			{
				event.setCancelled(true);
				event.setDamage(0);
				event.getDamager().sendMessage("§a[PVP]§3 对方已关闭PVP");
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
			boolean damagerPVP = false;
			if(plugin.playerData.containsKey(p.getUniqueId()))
			{
				otherPVP = plugin.playerData.get(p.getUniqueId()).canPvp();
			}
			if(plugin.playerData.containsKey(damager.getUniqueId()))
			{
				damagerPVP = plugin.playerData.get(damager.getUniqueId()).canPvp();
			}
			
			if(!damagerPVP)
			{
				event.setCancelled(true);
				event.setDamage(0);
				return;
			}
			else if(!otherPVP)
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
