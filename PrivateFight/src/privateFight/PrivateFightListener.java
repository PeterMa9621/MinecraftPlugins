package privateFight;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PrivateFightListener implements Listener
{
	private PrivateFight plugin;

	public PrivateFightListener(PrivateFight plugin)
	{
		this.plugin=plugin;
	}

	/**
	 *  Used to check if player quit the game, then the player's opponent leave the fight.
	 * @param event
	 */
	@EventHandler
	private void onPlayerQuit(PlayerQuitEvent event)
	{
		if(plugin.allFightingPlayers.containsKey(event.getPlayer().getName()))
		{
			Player p = event.getPlayer();
			String locationName = plugin.allFightingPlayers.get(p.getName());
			int index = plugin.fightPlayers.get(locationName).indexOf(p.getName());
			Player otherPlayer = Bukkit.getServer().getPlayer(plugin.fightPlayers.get(locationName).get(1-index));
			plugin.leaveFight(locationName, otherPlayer, p);
			otherPlayer.sendTitle("§a[决斗系统]", "§c对手离开了游戏,决斗结束");
		}
	}
	
	/**
	 *  Used to check if the two players are both ready for fighting. Once the last one right click the
	 *  Ready Sign, then teleport them to the fight area.
	 * @param event
	 */
	@EventHandler
	private void onPlayerClickSign(PlayerInteractEvent event)
	{
		if(event.getAction()!=Action.RIGHT_CLICK_BLOCK)
			return;

		if(event.getClickedBlock().getType().equals(Material.WALL_SIGN))
		{
			Sign sign = (Sign)event.getClickedBlock().getState();
			if(sign.getLines().length==0)
				return;

			if(sign.getLine(0).equalsIgnoreCase("§a[player1Ready]") ||
					sign.getLine(0).equalsIgnoreCase("§a[player2Ready]"))
			{
				Player p = event.getPlayer();
				String locationName = sign.getLine(1).substring(2);
				
				if(plugin.fightPlayers.get(locationName)==null)
					return;
				if(!plugin.fightPlayers.get(locationName).contains(p.getName()))
					return;

				String state = sign.getLine(3).substring(4);
				Location otherSignLocation = null, player1Fight = null, player2Fight = null;

				if(state.equalsIgnoreCase("已准备"))
				{
					return;
				}
				else if(state.equalsIgnoreCase("未准备"))
				{
					sign.setLine(3, "§a§l已准备");
					sign.update();
				}
				if(sign.getLine(0).equalsIgnoreCase("§a[player1Ready]"))
				{
					otherSignLocation = plugin.location.get(locationName).get("player2Ready");
				}
				else if(sign.getLine(0).equalsIgnoreCase("§a[player2Ready]"))
				{
					otherSignLocation = plugin.location.get(locationName).get("player1Ready");
				}
				
				player1Fight = plugin.location.get(locationName).get("player1Fight");
				player2Fight = plugin.location.get(locationName).get("player2Fight");
				Sign otherSign = (Sign)p.getWorld().getBlockAt(otherSignLocation).getState();
				if(otherSign.getLine(3).equalsIgnoreCase("§a§l已准备"))
				{
					if(sign.getLine(0).equalsIgnoreCase("§a[player1Ready]"))
					{
						p.teleport(player1Fight);
						if(plugin.fightPlayers.get(locationName).get(0).equalsIgnoreCase(p.getName()))
						{
							Bukkit.getServer().getPlayer(plugin.fightPlayers.get(locationName).get(1)).teleport(player2Fight);
						}
						else
						{
							Bukkit.getServer().getPlayer(plugin.fightPlayers.get(locationName).get(0)).teleport(player2Fight);
						}
					}
					else if(sign.getLine(0).equalsIgnoreCase("§a[player2Ready]"))
					{
						p.teleport(player2Fight);
						if(plugin.fightPlayers.get(locationName).get(0).equalsIgnoreCase(p.getName()))
						{
							Bukkit.getServer().getPlayer(plugin.fightPlayers.get(locationName).get(1)).teleport(player1Fight);
						}
						else
						{
							Bukkit.getServer().getPlayer(plugin.fightPlayers.get(locationName).get(0)).teleport(player1Fight);
						}
					}
					plugin.isReady.add(locationName);
					Bukkit.getServer().getPlayer(plugin.fightPlayers.get(locationName).get(0)).sendTitle("§a开始战斗", "§6你们有"+plugin.timeForFight/60+"分钟时间来决斗");
					Bukkit.getServer().getPlayer(plugin.fightPlayers.get(locationName).get(1)).sendTitle("§a开始战斗", "§6你们有"+plugin.timeForFight/60+"分钟时间来决斗");
					
					/**
					 *  Used to give players the maximum time to fight, if they still can not kill
					 *  the other one, then they have to leave this place and give this place to other
					 *  players who are waiting.
					 */
					new BukkitRunnable()
					{
			    		int time;
						public void run()
						{
							if(!plugin.fightPlayers.containsKey(locationName))
							{
								cancel();
							}
							if(time>plugin.timeForWaitReady)
							{
								plugin.leaveFight(locationName, Bukkit.getServer().getPlayer(plugin.fightPlayers.get(locationName).get(0)), Bukkit.getServer().getPlayer(plugin.fightPlayers.get(locationName).get(1)));
								cancel();
							}
							time++;
						}
					}.runTaskTimer(plugin, 0L, 20L);
				}
			}
		}
		
	}
	
}
