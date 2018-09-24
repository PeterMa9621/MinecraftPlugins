package levelSystem;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class LevelSystemListener implements Listener
{
	private LevelSystem plugin;
	
	public LevelSystemListener(LevelSystem plugin)
	{
		this.plugin=plugin;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
    {
		if(!plugin.player.containsKey(event.getPlayer().getName()))
		{
			PlayerData data = new PlayerData(event.getPlayer().getName(), 1, 0, 0);
			plugin.player.put(event.getPlayer().getName(), data);
		}
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
		event.setFormat("§2["+plugin.player.get(event.getPlayer().getName()).getLevel()+"级]§f"+event.getFormat());
    }
	
	@EventHandler
	public void onPlayerChangeExp(PlayerExpChangeEvent event)
    {
		int amount = event.getAmount();
		Player p = event.getPlayer();
		PlayerData playerData = plugin.player.get(p.getName());
		int level = playerData.getLevel();
		int currentExp = playerData.getCurrentExp();
		int totalExp = playerData.getTotalExp();
		
		int currentLevelExp = plugin.expFormat.get(level);

		if(currentExp+amount<currentLevelExp)
		{
			totalExp += amount;
			currentExp += amount;
		}
		else
		{
			if(level==plugin.maxLevel)
			{
				currentExp = currentLevelExp;
			}
			else
			{
				level += 1;
				playerData.setLevel(level);
				LevelUpEvent levelUp = new LevelUpEvent(p);
				Bukkit.getServer().getPluginManager().callEvent(levelUp);
				totalExp += amount;
				currentExp = currentExp + amount - currentLevelExp;
				
				p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 0.0F);
				p.sendMessage("§6你的历练等级已升级，当前历练等级为:§5§l"+level);
			}
		}

		playerData.setCurrentExp(currentExp);
		playerData.setTotalExp(totalExp);
		
		plugin.player.put(event.getPlayer().getName(), playerData);
		
		//event.getPlayer().sendMessage(""+event.getAmount());
    }

}
