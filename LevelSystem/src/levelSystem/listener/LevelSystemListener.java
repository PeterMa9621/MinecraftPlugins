package levelSystem.listener;

import levelSystem.LevelSystem;
import levelSystem.event.LevelUpEvent;
import levelSystem.manager.RewardManager;
import levelSystem.model.LevelPlayer;
import levelSystem.model.LevelReward;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static levelSystem.manager.ConfigManager.useChatPrefix;

public class LevelSystemListener implements Listener
{
	private LevelSystem plugin;
	private HashMap<UUID, Boolean> canGetExp = new HashMap<>();
	
	public LevelSystemListener(LevelSystem plugin)
	{
		this.plugin=plugin;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		plugin.configManager.loadPlayerConfig(event.getPlayer());
    }

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		/*
		int index = event.getMessage().indexOf(":");
		String prefix = event.getMessage().substring(index)+"§2[1级]";
		//String prefix = event.getFormat().re.replace(message, "")+"§2[1级]";
		String message = event.getMessage();
		*/
		if(useChatPrefix)
			event.setFormat("§f[§2"+plugin.players.get(event.getPlayer().getUniqueId()).getLevel()+"级§f]"+event.getFormat());
    }

    @EventHandler
	public void onPlayerFishing(PlayerFishEvent event) {
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		canGetExp.put(uuid, false);
	}

	@EventHandler
	public void onPlayerChangeExp(PlayerExpChangeEvent event) {
		Player p = event.getPlayer();
		if(canGetExp.get(p.getUniqueId())) {
			int expAmount = event.getAmount();
			LevelPlayer levelPlayer = plugin.players.get(p.getUniqueId());
			levelPlayer.addExp(expAmount);
		}
		canGetExp.put(p.getUniqueId(), true);
    }

    @EventHandler
	public void onPlayerLevelUp(LevelUpEvent event) {
		LevelPlayer levelPlayer = event.getPlayer();
		int level = levelPlayer.getLevel();
		Player player = levelPlayer.getPlayer();
		player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 0.0F);
		player.sendTitle("§6你的历练等级已升级", "§2当前历练等级为:§5§l" + level + "§2级", 10, 60, 10);

		LevelReward levelReward = plugin.rewardManager.getReward(level);
		if(levelReward==null)
			return;

		String msg = levelReward.getMsg();
		List<String> commands = levelReward.getCommands();
		if(commands.size()>0){
			for(String command:commands) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
			}
		}

		if(!msg.equalsIgnoreCase(""))
			player.sendMessage("§6[等级系统] §f" + msg);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) throws IOException {
		Player player = event.getPlayer();
		plugin.configManager.savePlayerConfig(player);
	}

}
