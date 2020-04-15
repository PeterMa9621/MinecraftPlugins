package dps;

import de.erethon.dungeonsxl.event.dplayer.DPlayerKickEvent;
import de.erethon.dungeonsxl.event.dplayer.instance.game.DGamePlayerEscapeEvent;
import de.erethon.dungeonsxl.event.dplayer.instance.game.DGamePlayerFinishEvent;

import de.erethon.dungeonsxl.event.gameworld.GameWorldStartGameEvent;
import de.erethon.dungeonsxl.event.gameworld.GameWorldUnloadEvent;
import de.erethon.dungeonsxl.game.Game;
import dps.listener.PlayerListener;
import dps.model.DpsPlayer;
import dps.model.DpsPlayerManager;
import dps.rewardBox.RewardBoxManager;
import dps.rewardBox.RewardTable;
import dps.util.ScoreBoardUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class DpsListener implements Listener
{
	private Dps plugin;

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
			if(DpsPlayerManager.dpsData.containsKey(currentWorldId)){
				HashMap<UUID, DpsPlayer> players = DpsPlayerManager.dpsData.get(currentWorldId);
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
			HashMap<UUID, DpsPlayer> players = DpsPlayerManager.dpsData.get(currentWorldId);
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
		if(DpsPlayerManager.dpsData.containsKey(currentWorldId)){
			HashMap<UUID, DpsPlayer> dpsPlayers = DpsPlayerManager.dpsData.get(currentWorldId);
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
		Bukkit.getConsoleSender().sendMessage("GameWorldStartGame");
		Game game = event.getGame();
		final String dungeonName = game.getDungeon().getName();
		final UUID worldId = game.getWorld().getWorld().getUID();
		Collection<Player> players = game.getPlayers();
		HashMap<UUID, DpsPlayer> dpsPlayers = new HashMap<>();
		PlayerListener playerListener = new PlayerListener(dpsPlayers);
		players.forEach(player -> {
			Dps.scoreBoard.getAPI().stopScoreBoard(player);
			DpsPlayer dpsPlayer = new DpsPlayer(player, 0d, true, worldId, dungeonName, playerListener);

			DpsPlayerManager.dpsPlayers.put(dpsPlayer.getPlayer().getUniqueId(), dpsPlayer);
			dpsPlayer.setGroupSize(players.size());
			dpsPlayers.put(player.getUniqueId(), dpsPlayer);
		});

		DpsPlayerManager.dpsData.put(worldId, dpsPlayers);
		ScoreBoardUtil.displayDpsBoard(dpsPlayers);
	}

	@EventHandler
	public void onDungeonEnd(GameWorldUnloadEvent event) {
		World world = event.getGameWorld().getWorld();

		DpsPlayerManager.dpsData.remove(world.getUID());
		if(pendingDpsPlayers.containsKey(world.getUID())){
			pendingDpsPlayers.get(world.getUID()).forEach(dpsPlayer -> {
				Dps.scoreBoard.getAPI().restartScoreBoard(dpsPlayer.getPlayer());
				RewardTable rewardTable = RewardBoxManager.getRewardTable(dpsPlayer.getDungeonName());
				if(rewardTable!=null) {
					RewardBoxManager.showRewardBox(dpsPlayer);

					String playerName = dpsPlayer.getPlayer().getName();
					int exp = rewardTable.getRandomExp();
					String command = String.format("level add %s %d", playerName, exp);
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
				}

				DpsPlayerManager.markPlayerExitDungeon(dpsPlayer.getPlayer());
			});
			pendingDpsPlayers.remove(world.getUID());
		}
	}

	@EventHandler
	public void onFinishDungeon(DGamePlayerFinishEvent event) {
		UUID wordId = event.getDPlayer().getWorld().getUID();
		DpsPlayer dpsPlayer = DpsPlayerManager.dpsData.get(wordId).get(event.getDPlayer().getPlayer().getUniqueId());

		ArrayList<DpsPlayer> players;
		if(!pendingDpsPlayers.containsKey(wordId)) {
			players = new ArrayList<>();
		} else {
			players = pendingDpsPlayers.get(wordId);
		}

		players.add(dpsPlayer);
		pendingDpsPlayers.put(wordId, players);
		DpsPlayerManager.markPlayerExitDungeon(event.getDPlayer().getPlayer());
	}

	@EventHandler
	public void onPlayerEscape(DGamePlayerEscapeEvent event) {
		Dps.scoreBoard.getAPI().restartScoreBoard(event.getDPlayer().getPlayer());
		DpsPlayerManager.markPlayerExitDungeon(event.getDPlayer().getPlayer());
	}

	@EventHandler
	public void onPlayerKicked(DPlayerKickEvent event) {
		Dps.scoreBoard.getAPI().restartScoreBoard(event.getDPlayer().getPlayer());
		DpsPlayerManager.markPlayerExitDungeon(event.getDPlayer().getPlayer());
	}

	@EventHandler
	public void onPlayerKillEntity(EntityDeathEvent event) {
		Player player = event.getEntity().getKiller();
		if(player==null)
			return;

		UUID playerId = player.getUniqueId();
		if(DpsPlayerManager.dpsPlayers.containsKey(playerId)){
			DpsPlayer dpsPlayer = DpsPlayerManager.dpsPlayers.get(playerId);
			if(dpsPlayer.isInDungeon()) {
				Random random = new Random(Calendar.getInstance().getTimeInMillis());
				double randomNumber = random.nextDouble();
				RewardTable rewardTable = RewardBoxManager.getRewardTable(dpsPlayer.getDungeonName());
				if(randomNumber < rewardTable.getBonusRewardProb() &&
						dpsPlayer.getNumBonusReward() < RewardBoxManager.maxBonusRewards) {
					dpsPlayer.addBonusReward();
					player.sendMessage(ChatColor.GOLD + "你获得了额外的奖励，副本结束后你的奖励会+1");
				}
			}

		}
	}
}
