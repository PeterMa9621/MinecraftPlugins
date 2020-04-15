package levelSystem.listener;

import levelSystem.LevelSystem;
import levelSystem.event.LevelUpEvent;
import levelSystem.model.LevelPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.IOException;

public class LevelSystemListener implements Listener
{
	private LevelSystem plugin;
	
	public LevelSystemListener(LevelSystem plugin)
	{
		this.plugin=plugin;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		plugin.configManager.loadPlayerConfig(event.getPlayer());
    }

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event)
    {
		/*
		int index = event.getMessage().indexOf(":");
		String prefix = event.getMessage().substring(index)+"§2[1级]";
		//String prefix = event.getFormat().re.replace(message, "")+"§2[1级]";
		String message = event.getMessage();
		*/
		// event.setFormat("§2["+plugin.players.get(event.getPlayer().getUniqueId()).getLevel()+"级]§f"+event.getFormat());
    }
	
	@EventHandler
	public void onPlayerChangeExp(PlayerExpChangeEvent event)
    {
		int expAmount = event.getAmount();
		Player p = event.getPlayer();
		LevelPlayer levelPlayer = plugin.players.get(p.getUniqueId());
		levelPlayer.addExp(expAmount);
    }

    @EventHandler
	public void onPlayerLevelUp(LevelUpEvent event) {
		LevelPlayer levelPlayer = event.getPlayer();
		int level = levelPlayer.getLevel();
		Player player = levelPlayer.getPlayer();
		player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 0.0F);
		player.sendTitle("§6你的历练等级已升级", "§2当前历练等级为:§5§l" + level + "§2级", 10, 60, 10);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) throws IOException {
		plugin.configManager.savePlayerConfig(event.getPlayer());
	}

}
