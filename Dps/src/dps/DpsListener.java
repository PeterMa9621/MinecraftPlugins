package dps;

import de.erethon.dungeonsxl.event.gameworld.GameWorldStartGameEvent;
import de.erethon.dungeonsxl.event.gameworld.GameWorldUnloadEvent;
import de.erethon.dungeonsxl.game.Game;
import de.erethon.dungeonsxl.player.DInstancePlayer;
import dps.listener.PlayerListener;
import dps.model.DpsPlayer;
import dps.util.ScoreBoardUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import scoreBoard.ScoreBoard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class DpsListener implements Listener
{
	private Dps plugin;

	private HashMap<UUID, HashMap<UUID, DpsPlayer>> dpsData = new HashMap<>();

	public DpsListener(Dps plugin)
	{
		this.plugin=plugin;
	}
	
	@EventHandler
	public void onPlayerDamaged(EntityDamageByEntityEvent event)
    {
		if(event.getEntity() instanceof Player)
			return;
		UUID currentWorldId = event.getDamager().getWorld().getUID();
		if(event.getDamager() instanceof Player)
		{
			if(dpsData.containsKey(currentWorldId)){
				HashMap<UUID, DpsPlayer> players = dpsData.get(currentWorldId);
				if(players.containsKey(event.getDamager().getUniqueId()))
				{
					Player damager = (Player)event.getDamager();
					DpsPlayer dpsPlayer = players.get(damager.getUniqueId());

					double dpsScore = dpsPlayer.getDpsScore() + (event.getDamage()/3.0);

					dpsPlayer.setDpsScore(dpsScore);
				}
			}

		}
		else if(event.getDamager().getType().equals(EntityType.ARROW))
		{
			if(event.getDamager().getCustomName()==null)
				return;
			if(Bukkit.getPlayer(event.getDamager().getCustomName())==null)
				return;
			HashMap<UUID, DpsPlayer> players = dpsData.get(currentWorldId);
			Player damager = Bukkit.getPlayer(event.getDamager().getCustomName());
			assert damager != null;
			if(players.containsKey(damager.getUniqueId()))
			{
				DpsPlayer dpsPlayer = players.get(damager.getUniqueId());
				double dpsScore = dpsPlayer.getDpsScore() + (event.getDamage()/3.0);

				dpsPlayer.setDpsScore(dpsScore);
			}
		}
    }
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event)
    {
    	Player player = event.getPlayer();
		UUID currentWorldId = player.getWorld().getUID();
		if(dpsData.containsKey(currentWorldId)){
			HashMap<UUID, DpsPlayer> dpsPlayers = dpsData.get(currentWorldId);
			dpsPlayers.remove(player.getUniqueId());
		}
    }
	
	/*
	@EventHandler
	private void onPlayerCommand(PlayerCommandPreprocessEvent event)
	{
		if(plugin.singleDps.containsKey(event.getPlayer()))
		{
			event.setCancelled(true);
			Player p = (Player)event.getPlayer();
			p.sendMessage("��a[��սϵͳ] ��c��ս���������޷�ʹ�ø�ָ��");
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
    }

    @EventHandler
	public void onDungeonStart(GameWorldStartGameEvent event) {
		Bukkit.getConsoleSender().sendMessage("Dungeon Start");
		Game game = event.getGame();

		HashMap<UUID, DpsPlayer> dpsPlayers = new HashMap<>();
		PlayerListener playerListener = new PlayerListener(dpsPlayers);
		game.getPlayers().forEach(player -> {
			Dps.scoreBoard.getAPI().stopScoreBoard(player);
			DpsPlayer dpsPlayer = new DpsPlayer(player, 0d, true, playerListener);

			dpsPlayers.put(player.getUniqueId(), dpsPlayer);
		});
		UUID worldId = game.getWorld().getWorld().getUID();
		dpsData.put(worldId, dpsPlayers);

		ScoreBoardUtil.displayDpsBoard(dpsPlayers);
	}

	@EventHandler
	public void onDungeonEnd(GameWorldUnloadEvent event) {
		Bukkit.getConsoleSender().sendMessage("Dungeon End, players: " + event.getGameWorld().getPlayers().size());
		dpsData.remove(event.getGameWorld().getWorld().getUID());
		event.getGameWorld().getPlayers().forEach(dInstancePlayer -> {
			Player player = dInstancePlayer.getPlayer();
			Dps.scoreBoard.getAPI().restartSocreBoard(player);
		});
	}
}
