package dailyQuest.listener;

import dailyQuest.DailyQuest;
import dailyQuest.model.Quest;
import dailyQuest.model.QuestPlayer;
import dailyQuest.config.ConfigManager;
import dailyQuest.manager.QuestManager;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Date;

public class DailyQuestListener implements Listener
{
	private DailyQuest plugin;

	public DailyQuestListener(DailyQuest plugin)
	{
		this.plugin=plugin;
	}
	
	@EventHandler
	public void onPlayerRightClickNPC(NPCRightClickEvent event)
    {
    	Player player = event.getClicker();
		if(plugin.configManager.npcIds.contains(event.getNPC().getId())) {
			QuestPlayer questPlayer = plugin.questPlayerManager.getQuestPlayer(player);
			if(questPlayer==null)
				return;
			if(!questPlayer.isDoingQuest())
				return;

			Quest quest = QuestManager.quests.get(questPlayer.getWhatTheQuestIs());
			if(questPlayer.getCurrentNumber()!=0 && quest.getNPCId()==event.getNPC().getId()) {
				Player p = event.getClicker();
				int id = event.getNPC().getId();
				p.openInventory(plugin.guiManager.createGUI(p, event.getNPC().getName(), id));
				p.playSound(p.getLocation(), Sound.BLOCK_SNOW_PLACE, 1F, 0.0F);
				return;
			}
		}
		
		if(event.getNPC().getId()==plugin.configManager.getQuestNPCId) {
			player.openInventory(plugin.guiManager.getQuestGUI(player, event.getNPC().getName()));
			player.playSound(player.getLocation(), Sound.BLOCK_SNOW_PLACE, 1F, 0.0F);
		}
    }

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		plugin.configManager.loadPlayerConfig(event.getPlayer());
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		QuestPlayer player = plugin.questPlayerManager.getQuestPlayer(event.getPlayer());
		if(player!=null)
			player.setLastLogout(plugin.configManager.date.format(new Date()));
	}
}
