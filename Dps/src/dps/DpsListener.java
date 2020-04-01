package dps;

import de.erethon.dungeonsxl.event.dplayer.instance.game.DGamePlayerEscapeEvent;
import de.erethon.dungeonsxl.event.dplayer.instance.game.DGamePlayerFinishEvent;
import de.erethon.dungeonsxl.event.gameworld.GameWorldStartGameEvent;
import de.erethon.dungeonsxl.event.gameworld.GameWorldUnloadEvent;
import de.erethon.dungeonsxl.game.Game;
import dps.listener.PlayerListener;
import dps.model.DpsPlayer;
import dps.rewardBox.RewardBoxManager;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class DpsListener implements Listener
{
	private Dps plugin;

	private HashMap<UUID, HashMap<UUID, DpsPlayer>> dpsData = new HashMap<>();

	private HashMap<UUID, ArrayList<DpsPlayer>> pendingDpsPlayers = new HashMap<>();

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
    }

    @EventHandler
	public void onDungeonStart(GameWorldStartGameEvent event) {
		Bukkit.getConsoleSender().sendMessage("Dungeon Start");
		Game game = event.getGame();
		Collection<Player> players = game.getPlayers();
		HashMap<UUID, DpsPlayer> dpsPlayers = new HashMap<>();
		PlayerListener playerListener = new PlayerListener(dpsPlayers);
		players.forEach(player -> {
			Dps.scoreBoard.getAPI().stopScoreBoard(player);
			DpsPlayer dpsPlayer = new DpsPlayer(player, 0d, true, playerListener);
			dpsPlayer.setGroupSize(players.size());
			dpsPlayers.put(player.getUniqueId(), dpsPlayer);
		});
		UUID worldId = game.getWorld().getWorld().getUID();
		dpsData.put(worldId, dpsPlayers);

		ScoreBoardUtil.displayDpsBoard(dpsPlayers);
	}

	@EventHandler
	public void onDungeonEnd(GameWorldUnloadEvent event) {
		Bukkit.getConsoleSender().sendMessage(event.getGameWorld().getName());
		World world = event.getGameWorld().getWorld();

		dpsData.remove(world.getUID());
		if(pendingDpsPlayers.containsKey(world.getUID())){
			pendingDpsPlayers.get(world.getUID()).forEach(dpsPlayer -> {
				Dps.scoreBoard.getAPI().restartSocreBoard(dpsPlayer.getPlayer());
				RewardBoxManager.showRewardBox(dpsPlayer);
			});
			pendingDpsPlayers.remove(world.getUID());
		}
	}

	@EventHandler
	public void onFinishDungeon(DGamePlayerFinishEvent event) {
		Bukkit.getConsoleSender().sendMessage("Dungeon End, player: " + event.getDPlayer().getPlayer().getName());
		Bukkit.getConsoleSender().sendMessage("Dungeon End, world: " + event.getDPlayer().getWorld().getName());
		UUID wordId = event.getDPlayer().getWorld().getUID();
		DpsPlayer dpsPlayer = dpsData.get(wordId).get(event.getDPlayer().getPlayer().getUniqueId());

		ArrayList<DpsPlayer> players;
		if(!pendingDpsPlayers.containsKey(wordId)) {
			players = new ArrayList<>();
		} else {
			players = pendingDpsPlayers.get(wordId);
		}

		players.add(dpsPlayer);
		pendingDpsPlayers.put(wordId, players);
	}

	@EventHandler
	public void onPlayerEscape(DGamePlayerEscapeEvent event) {
		Dps.scoreBoard.getAPI().restartSocreBoard(event.getDPlayer().getPlayer());
	}
}
