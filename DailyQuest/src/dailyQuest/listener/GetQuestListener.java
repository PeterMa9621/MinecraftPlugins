package dailyQuest.listener;

import dailyQuest.DailyQuest;
import dailyQuest.model.Quest;
import dailyQuest.model.QuestPlayer;
import dailyQuest.util.Util;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class GetQuestListener implements Listener
{
	private DailyQuest plugin;

	public GetQuestListener(DailyQuest plugin)
	{
		this.plugin=plugin;
	}

	@EventHandler
	private void onPlayerClickQuestItemGUI(InventoryClickEvent event)
	{
		if(event.getView().getTitle().contains("��8�ճ�����"))
		{
			event.setCancelled(true);
			Player p = (Player)event.getWhoClicked();
			ItemStack clickedItem = event.getCurrentItem();
			if(event.getRawSlot()==2) {

				QuestPlayer questPlayer = plugin.questPlayerManager.getQuestPlayer(p);
				if(questPlayer.getCurrentNumber()!=0) {
					Util.changeLore(clickedItem, new ArrayList<String>() {{
						add("��c��Ŀǰ�������������ȡ�6ȡ�������c��������ȡ����!");
					}});
					return;
				}

				if(!questPlayer.canGetQuest()) {
					Util.changeLore(clickedItem, new ArrayList<String>() {{
						add("��c�����������6�Ѵ����ޡ�c������������!");
					}});
					return;
				}
				
				// Get the state of this player
				// the first index means the current index of the quest
				// the second index means what the quest is
				// the last index means how many quests totally this player has already finished
				QuestPlayer player = plugin.questPlayerManager.getQuestPlayer(p);
				player.getNextQuest();
				Quest quest = player.getCurrentQuest();
				p.sendMessage("��6[�� 1 ��] ��a" + quest.getQuestDescribe());
				p.closeInventory();
				return;
			}
			
			if(event.getRawSlot()==3)
			{
				p.performCommand("rw quit");
				p.closeInventory();
				return;
			}
			
			if(event.getRawSlot()==4)
			{
				p.performCommand("ob open �����ֲ�");
				return;
			}
			
			if(event.getRawSlot()==5)
			{
				p.closeInventory();
			}
		}

	}
	
	@EventHandler
	private void onPlayerCloseInventory(InventoryCloseEvent event)
	{
		if(event.getView().getTitle().equalsIgnoreCase("��8�ճ�����"))
		{
			Player p = (Player)event.getPlayer();
			p.playSound(p.getLocation(), Sound.BLOCK_SNOW_BREAK, 1F, 0.0F);
		}
	}
}
