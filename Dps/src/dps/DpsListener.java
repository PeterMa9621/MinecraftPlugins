package dps;

import de.erethon.dungeonsxl.api.dungeon.Game;
import de.erethon.dungeonsxl.api.event.group.GroupFinishDungeonEvent;
import de.erethon.dungeonsxl.api.event.group.GroupPlayerKickEvent;
import de.erethon.dungeonsxl.api.event.group.GroupPlayerLeaveEvent;
import de.erethon.dungeonsxl.api.event.player.GamePlayerFinishEvent;
import de.erethon.dungeonsxl.api.event.world.GameWorldStartGameEvent;
import de.erethon.dungeonsxl.api.event.world.InstanceWorldUnloadEvent;
import de.erethon.dungeonsxl.api.player.GamePlayer;
import de.erethon.dungeonsxl.api.player.PlayerGroup;
import de.erethon.dungeonsxl.api.world.GameWorld;
import dps.listener.PlayerListener;
import dps.model.DpsPlayer;
import dps.model.DpsPlayerManager;
import dps.rewardBox.RewardBoxManager;
import dps.rewardBox.RewardTable;
import dps.util.ScoreBoardUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
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
    }

    @EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {
		if(event.getHitEntity() instanceof Player)
			return;
		if(!(event.getEntity().getShooter() instanceof Player))
			return;
		Player player = (Player) event.getEntity().getShooter();
		if(DpsPlayerManager.isPlayerInDps(player)) {
			if(event.getHitEntity()!=null) {
				DpsPlayer dpsPlayer = DpsPlayerManager.getDpsPlayer(player);
				Bukkit.getScheduler().runTaskLater(plugin, () -> {
					double dpsScore = dpsPlayer.getDpsScore() + (event.getHitEntity().getLastDamageCause().getDamage()/3.0);
					dpsPlayer.setDpsScore(dpsScore);
				}, 1);
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

    @EventHandler
	public void onDungeonStart(GameWorldStartGameEvent event) {
		//Bukkit.getConsoleSender().sendMessage("GameWorldStartGame");
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
	public void onDungeonEnd(InstanceWorldUnloadEvent event) {
		World world = event.getBukkitWorld();

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
	public void onFinishDungeon(GroupFinishDungeonEvent event) {
		PlayerGroup playerGroup = event.getGroup();
		UUID wordId = playerGroup.getGameWorld().getWorld().getUID();
		for(Player player:playerGroup.getMembers().getOnlinePlayers()) {
			DpsPlayer dpsPlayer = DpsPlayerManager.dpsData.get(wordId).get(player.getUniqueId());

			ArrayList<DpsPlayer> players;
			if(!pendingDpsPlayers.containsKey(wordId)) {
				players = new ArrayList<>();
			} else {
				players = pendingDpsPlayers.get(wordId);
			}

			players.add(dpsPlayer);
			pendingDpsPlayers.put(wordId, players);
			DpsPlayerManager.markPlayerExitDungeon(player);
		}
	}

	@EventHandler
	public void onPlayerEscape(GroupPlayerLeaveEvent event) {
		Dps.scoreBoard.getAPI().restartScoreBoard(event.getPlayer().getPlayer());
		DpsPlayerManager.markPlayerExitDungeon(event.getPlayer().getPlayer());
	}

	@EventHandler
	public void onPlayerKicked(GroupPlayerKickEvent event) {
		Dps.scoreBoard.getAPI().restartScoreBoard(event.getPlayer().getPlayer());
		DpsPlayerManager.markPlayerExitDungeon(event.getPlayer().getPlayer());
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
				SplittableRandom random = new SplittableRandom();
				double randomNumber = random.nextDouble();
				RewardTable rewardTable = RewardBoxManager.getRewardTable(dpsPlayer.getDungeonName());
				// To Do: check if the map contains reward
				if(randomNumber < rewardTable.getBonusRewardProb() &&
						dpsPlayer.getNumBonusReward() < RewardBoxManager.maxBonusRewards) {
					dpsPlayer.addBonusReward();
					player.sendMessage(ChatColor.GOLD + "你获得了额外的奖励，副本结束后你的奖励会+1");
				}
			}

		}
	}
}
