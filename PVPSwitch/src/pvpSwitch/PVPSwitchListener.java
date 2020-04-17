package pvpSwitch;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import net.citizensnpcs.api.CitizensAPI;
import pvpSwitch.model.PvpPlayer;

public class PVPSwitchListener implements Listener
{
	private PVPSwitch plugin;

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
    	if(!(event.getEntity() instanceof Player))
    		return;
		if(event.getDamager() instanceof Player) {
			if((event.getDamager()).hasPermission("pvpSwitch.bypass")) {
				return;
			}

			boolean otherPVP = false;
			boolean damagerPVP = false;
			if(plugin.playerData.containsKey(event.getEntity().getUniqueId())) {
				PvpPlayer otherPvpPlayer = plugin.playerData.get((event.getEntity()).getUniqueId());
				otherPVP = otherPvpPlayer.canPvp();
			}
			if(plugin.playerData.containsKey(event.getDamager().getUniqueId())) {
				PvpPlayer damagerPvpPlayer = plugin.playerData.get((event.getDamager()).getUniqueId());
				damagerPVP = damagerPvpPlayer.canPvp();
			}

			if(!damagerPVP) {
				event.setCancelled(true);
				event.setDamage(0);
			}
			else if(!otherPVP) {
				event.setCancelled(true);
				event.setDamage(0);
				event.getDamager().sendMessage("§6[PVP]§3 对方已关闭PVP");
			}
		} else if(event.getDamager() instanceof Projectile) {
			if(CitizensAPI.getNPCRegistry().isNPC(event.getEntity()))
				return;

			Player damager = (Player) ((Projectile) event.getDamager()).getShooter();
			Player p = (Player)event.getEntity();

			if(damager.hasPermission("pvpSwitch.bypass")) {
				return;
			}

			boolean otherPVP = false;
			boolean damagerPVP = false;
			if(plugin.playerData.containsKey(p.getUniqueId())) {
				otherPVP = plugin.playerData.get(p.getUniqueId()).canPvp();
			}
			if(plugin.playerData.containsKey(damager.getUniqueId())) {
				damagerPVP = plugin.playerData.get(damager.getUniqueId()).canPvp();
			}
			
			if(!damagerPVP) {
				event.setCancelled(true);
				event.setDamage(0);
			}
			else if(!otherPVP) {
				event.setCancelled(true);
				event.setDamage(0);
				damager.sendMessage("§6[PVP]§3 对方已关闭PVP");
			}
		}
    }
}
