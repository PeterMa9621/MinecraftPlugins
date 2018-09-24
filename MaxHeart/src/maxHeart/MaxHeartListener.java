package maxHeart;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import levelSystem.LevelUpEvent;

public class MaxHeartListener implements Listener
{
	private MaxHeart plugin;

	public MaxHeartListener(MaxHeart plugin)
	{
		this.plugin=plugin;
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event)
    {
		new BukkitRunnable()
		{
    		int time;
			public void run()
			{
				if(time==1)
				{
					if(plugin.levelSystem.getAPI().getLevel(event.getPlayer())>=30)
					{
						int extraHeart = calculate(plugin.levelSystem.getAPI().getLevel(event.getPlayer()));
						event.getPlayer().setMaxHealth(20+extraHeart);
					}
					cancel();
				}
				time++;
			}
		}.runTaskTimer(plugin, 0L, 20L);
    }
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
    {
		int extraHeart = calculate(plugin.levelSystem.getAPI().getLevel(event.getPlayer()));
		event.getPlayer().setMaxHealth(20+extraHeart);
		/*
		if(!plugin.playerData.containsKey(event.getPlayer()))
		{
			PlayerData pd = new PlayerData(0);
			plugin.playerData.put(event.getPlayer().getName(), pd);
		}
		else
		{
			if(!plugin.playerData.get(event.getPlayer()).hasExtraHeart)
			{
				double extraHeart = plugin.playerData.get(event.getPlayer()).getExtraHeart();
				event.getPlayer().setMaxHealth(event.getPlayer().getMaxHealth()+extraHeart);
				plugin.playerData.get(event.getPlayer()).changeExtraHeartStatus(true);
			}
		}
		*/
    }
	
	public int calculate(int level)
	{
		int result = 0;
		for(int i:plugin.heart.keySet())
		{
			if(level>=i)
				result+=plugin.heart.get(i);
		}
		return result;
	}
    
	
	@EventHandler
	public void onPlayerLevelUp(LevelUpEvent event)
    {
		int level = plugin.levelSystem.getAPI().getLevel(event.getPlayer());
		if(plugin.heart.containsKey(level))
		{
			double heart = plugin.heart.get(level);
			event.getPlayer().setMaxHealth(event.getPlayer().getMaxHealth()+heart);

			//plugin.playerData.get(event.getPlayer()).addExtraHeart(heart);
			event.getPlayer().sendMessage(plugin.message);
		}
		return;
    }
	
	
}
