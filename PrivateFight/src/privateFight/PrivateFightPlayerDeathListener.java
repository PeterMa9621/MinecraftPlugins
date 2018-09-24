package privateFight;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PrivateFightPlayerDeathListener implements Listener
{
	private PrivateFight plugin;

	public PrivateFightPlayerDeathListener(PrivateFight plugin)
	{
		this.plugin=plugin;
	}
	
	@EventHandler
	private void onPlayerDeath(PlayerDeathEvent event)
	{
		if(!(event.getEntity().getKiller() instanceof Player))
			return;
		if(plugin.allFightingPlayers.containsKey(event.getEntity().getKiller().getName()))
		{
			Player p1 = event.getEntity().getKiller();
			String name = plugin.allFightingPlayers.get(p1.getName());

			Player p2 = (Player)event.getEntity();
			plugin.leaveFight(name, p1, p2);
			p1.sendMessage("��a[����ϵͳ] ��6��ϲ,��Ӯ���˴˴ξ�����ʤ��!");
			p2.sendMessage("��a[����ϵͳ] ��6�㱻�������仨��ˮ");
			Bukkit.getServer().broadcastMessage("��a[����ϵͳ] ��6����һ����ս����5"+p1.getName()+"��6����սʤ�ˡ�5"+p2.getName()+"��6��");
			plugin.whoDied.add(p2.getName());
			return;
		}
	}
	
	@EventHandler
	private void onPlayerDeath(PlayerRespawnEvent event)
	{
		if(plugin.whoDied.contains(event.getPlayer().getName()))
		{
			Player p = event.getPlayer();
			new BukkitRunnable()
			{
	    		int time;
				public void run()
				{
					if(time>1)
					{
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tp "+p.getName()+" "+plugin.spawn.getBlockX()+" "+plugin.spawn.getBlockY()+" "+plugin.spawn.getBlockZ());
						cancel();
					}
					time++;
				}
			}.runTaskTimer(plugin, 0L, 20L);
			plugin.whoDied.remove(event.getPlayer().getName());
		}
	}
}
