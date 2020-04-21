package dailyQuest.listener;

import java.util.ArrayList;
import java.util.Date;

import dailyQuest.DailyQuest;
import dailyQuest.gui.GuiManager;
import dailyQuest.model.Quest;
import dailyQuest.model.QuestPlayer;
import dailyQuest.manager.QuestManager;
import dailyQuest.util.QuestFinishType;
import dailyQuest.util.Util;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class FinishQuestListener implements Listener
{
	private DailyQuest plugin;

	public FinishQuestListener(DailyQuest plugin)
	{
		this.plugin=plugin;
	}
	
	@EventHandler
	private void onPlayerClickQuestItemGUI(InventoryClickEvent event)
	{
		if(event.getView().getTitle().contains("所有任务物品-页数:"))
		{
			Player p = (Player)event.getWhoClicked();
			ArrayList<Inventory> list = plugin.guiManager.questItemGUI(p);
			int page = Integer.parseInt(event.getView().getTitle().split(":")[1]);
			event.setCancelled(true);
			if(event.getRawSlot()==48 && page!=1)
			{
				p.openInventory(list.get(page-2));
				return;
			}
			if(event.getRawSlot()==51 && page<list.size())
			{
				p.openInventory(list.get(page));
			}
		}
		else if(event.getView().getTitle().contains("所有奖励物品-页数:"))
		{
			Player p = (Player)event.getWhoClicked();
			ArrayList<Inventory> list = plugin.guiManager.rewardItemGUI(p);
			int page = Integer.parseInt(event.getView().getTitle().split(":")[1]);
			event.setCancelled(true);
			if(event.getRawSlot()==48 && page!=1)
			{
				p.openInventory(list.get(page-2));
				return;
			}
			if(event.getRawSlot()==51 && page<list.size())
			{
				p.openInventory(list.get(page));
			}
		}
	}
	
	@EventHandler
	private void onPlayerClickInventory(InventoryClickEvent event)
	{
		if(event.getView().getTitle().equalsIgnoreCase(GuiManager.npcTitle))
		{
			event.setCancelled(true);
			Player p = (Player)event.getWhoClicked();
			ItemStack clickedItem = event.getCurrentItem();
			if(event.getRawSlot()==3 && event.getInventory().getItem(3)!=null)
			{
				QuestPlayer questPlayer = plugin.questPlayerManager.getQuestPlayer(p);

				QuestFinishType questFinishType = QuestManager.isQuestFinished(questPlayer);
				if(questFinishType.equals(QuestFinishType.ItemQuestNotFinished)) {
					Util.changeLore(clickedItem, new ArrayList<String>() {{
						add("§c你手上没有我要的东西!");
						add("§d请把你要给我的东西拿在手上再给我(§c注意数量§d)!");
					}});
					return;
				} else if(questFinishType.equals(QuestFinishType.MobQuestNotFinished)) {
					Util.changeLore(clickedItem, new ArrayList<String>() {{
						add("§c你并没有完成任务!");
						add("§d请击杀完怪物再来找我吧!");
					}});
					return;
				} else if(questFinishType.equals(QuestFinishType.ItemQuestFinished)) {
					p.getInventory().setItemInMainHand(null);
				}

				if(p.getInventory().firstEmpty()==-1) {
					p.sendMessage("§6[日常任务] §c你的身上满了,请至少留出§e1§c个空位来接收奖励!");
					p.closeInventory();
					return;
				}

				boolean canContinue = questPlayer.finishQuestAndGetNext(plugin);

				if(!canContinue) {
					p.sendMessage("§6[日常任务] §d你今天的所有任务都完成了，明天再继续吧!");
					p.closeInventory();
					return;
				}

				Quest nextQuest = questPlayer.getCurrentQuest();
				p.sendMessage("§6[第 "+questPlayer.getCurrentNumber()+" 环] §a"+nextQuest.getQuestDescribe());
				p.closeInventory();
				return;
			}
			if((event.getRawSlot()==4 && event.getInventory().getItem(4)!=null)
					|| (event.getRawSlot()==5 && event.getInventory().getItem(5)!=null))
			{
				p.closeInventory();
			}
		}
	}
	
	@EventHandler
	private void onPlayerCloseInventory(InventoryCloseEvent event)
	{
		if(event.getView().getTitle().equalsIgnoreCase("§8NPC"))
		{
			Player p = (Player)event.getPlayer();
			p.playSound(p.getLocation(), Sound.BLOCK_SNOW_BREAK, 1F, 0.0F);
		}
	}
}
