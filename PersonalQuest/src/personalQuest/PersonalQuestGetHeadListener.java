package personalQuest;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class PersonalQuestGetHeadListener implements Listener
{
	private PersonalQuest plugin;
	
	public PersonalQuestGetHeadListener(PersonalQuest plugin)
	{
		this.plugin=plugin;
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event)
	{
		if(event.getEntity().getKiller() instanceof Player)
		{
			if(plugin.needToBeKilled.isEmpty() || plugin.needToBeKilled==null)
			{
				return;
			}
				
			if(plugin.needToBeKilled.contains(event.getEntity().getName()))
			{
				String lore = "��a["+plugin.message.get("QuestGui.HeadQuestTag")+"]��e�ѻ�ɱ��c"+event.getEntity().getName()
						+"%��e��ɱ��:"+event.getEntity().getKiller().getName();
				ItemStack head = plugin.createItem(397, 1, 3, "��c"+event.getEntity().getName()+"��a����ͷ", lore);
				
				event.getEntity().getKiller().getInventory().addItem(head);
				plugin.needToBeKilled.remove(event.getEntity().getName());
			}
		}
	}
}
