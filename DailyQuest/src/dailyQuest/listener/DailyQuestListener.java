package dailyQuest.listener;

import dailyQuest.DailyQuest;
import dailyQuest.gui.GuiManager;
import dailyQuest.manager.QuestPlayerManager;
import dailyQuest.model.Quest;
import dailyQuest.model.QuestPlayer;
import dailyQuest.config.ConfigManager;
import dailyQuest.manager.QuestManager;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

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
				ItemStack itemStack = p.getInventory().getItemInMainHand();
				if(itemStack.getType().isEdible()) {
					p.getInventory().setItemInMainHand(null);
					Bukkit.getScheduler().runTaskLater(plugin, () -> {
						player.getInventory().setItemInMainHand(itemStack);
					}, 2);
				}
				return;
			}
		}
		
		if(event.getNPC().getId()==plugin.configManager.getQuestNPCId) {
			player.openInventory(plugin.guiManager.getQuestGUI(player, event.getNPC().getName()));
			player.playSound(player.getLocation(), Sound.BLOCK_SNOW_PLACE, 1F, 0.0F);
		}
    }

	@EventHandler
	public void onItemConsumeEvent(PlayerItemConsumeEvent event) {
		/*
		Player player = event.getPlayer();
		QuestPlayer questPlayer = plugin.questPlayerManager.getQuestPlayer(player);
		if(questPlayer==null)
			return;
		if(!questPlayer.isDoingQuest())
			return;
		String title = player.getOpenInventory().getTitle();
		if(title.equalsIgnoreCase(GuiManager.npcTitle)) {
			ItemStack itemStack = player.getInventory().getItemInMainHand();
			player.getInventory().setItemInMainHand(null);
			Bukkit.getScheduler().runTaskLater(plugin, () -> {
				player.getInventory().setItemInMainHand(itemStack);
			}, 20);
			event.setCancelled(true);
		}

		 */
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
