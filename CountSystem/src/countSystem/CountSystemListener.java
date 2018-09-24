package countSystem;

import java.util.HashMap;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class CountSystemListener implements Listener
{
	private CountSystem plugin;

	public CountSystemListener(CountSystem plugin)
	{
		this.plugin=plugin;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
    {
		if(!plugin.playerData.containsKey(event.getPlayer().getName()))
		{
			HashMap<String, Integer> typeData = new HashMap<String, Integer>();
			typeData.put("Monster", 0);
			typeData.put("Player", 0);
			plugin.playerData.put(event.getPlayer().getName(), typeData);
		}
		return;
    }
	
	@EventHandler
	public void onPlayerDeathEvent(PlayerDeathEvent event)
    {
		if(event.getEntity().getKiller() instanceof Player)
		{
			HashMap<String, Integer> previousData = plugin.playerData.get(event.getEntity().getKiller().getName());
			previousData.put("Player", previousData.get("Player")+1);
			plugin.playerData.put(event.getEntity().getKiller().getName(), previousData);
		}
		return;
    }
	
	@EventHandler
	public void onEntityDeathEvent(EntityDeathEvent event)
    {
		if(event.getEntity() instanceof Player)
			return;
		if(event.getEntity().getKiller() instanceof Player)
		{
			if(event.getEntityType().equals(EntityType.ZOMBIE) || 
					event.getEntityType().equals(EntityType.CAVE_SPIDER) ||
					event.getEntityType().equals(EntityType.ENDERMAN) ||
					event.getEntityType().equals(EntityType.SPIDER) ||
					event.getEntityType().equals(EntityType.PIG_ZOMBIE) || 
					event.getEntityType().equals(EntityType.BLAZE) ||
					event.getEntityType().equals(EntityType.CREEPER) ||
					event.getEntityType().equals(EntityType.GHAST) ||
					event.getEntityType().equals(EntityType.MAGMA_CUBE) || 
					event.getEntityType().equals(EntityType.SKELETON) ||
					event.getEntityType().equals(EntityType.SLIME) ||
					event.getEntityType().equals(EntityType.WITHER_SKULL) ||
					event.getEntityType().equals(EntityType.ENDERMITE) || 
					event.getEntityType().equals(EntityType.GUARDIAN) ||
					event.getEntityType().equals(EntityType.WITHER) ||
					event.getEntityType().equals(EntityType.ENDER_DRAGON))
			{
				HashMap<String, Integer> previousData = plugin.playerData.get(event.getEntity().getKiller().getName());
				previousData.put("Monster", previousData.get("Monster")+1);
				plugin.playerData.put(event.getEntity().getKiller().getName(), previousData);
			}
		}
		return;
    }

}
