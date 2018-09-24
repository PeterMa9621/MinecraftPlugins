package checkInSystem;

import java.util.Date;
import java.util.HashMap;
import java.text.SimpleDateFormat;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class CheckInSystemListener implements Listener
{
	private CheckInSystem plugin;
	
	public int checkInDays = 0;
	public boolean checkIn = false;

	SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");//�������ڸ�ʽ
	public CheckInSystemListener(CheckInSystem plugin)
	{
		this.plugin=plugin;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
    {
		String todayDate = plugin.date.format(new Date());
		HashMap<String, String> data = new HashMap<String, String>();
		
		if(plugin.playerData.containsKey(event.getPlayer().getName()))
		{
			data = plugin.playerData.get(event.getPlayer().getName());
			if(!data.get("lastDate").equalsIgnoreCase(todayDate))
				plugin.isCheckIn.put(event.getPlayer().getName(), false);
		}
		else
		{
			data.put("days", "0");
			data.put("lastDate", "0000-00-00");
			plugin.isCheckIn.put(event.getPlayer().getName(), false);
		}
		
		data.put("todayDate", todayDate);
		plugin.playerData.put(event.getPlayer().getName(), data);
		
		return;
    }
	
	@EventHandler
	private void onPlayerClickInventory(InventoryClickEvent event)
	{
		if(event.getInventory().getTitle().equalsIgnoreCase("ǩ��"))
		{
			Player player = (Player)event.getWhoClicked();
			event.setCancelled(true);

			int recentCheckInDate = Integer.valueOf(plugin.playerData.get(player.getName()).get("days"));
			
			if(plugin.isCheckIn.get(player.getName())==true)
			{
				return;
			}
			
			if(event.getRawSlot()==recentCheckInDate)
			{
				HashMap<String, String> data = plugin.playerData.get(player.getName());
				data.put("days", String.valueOf(recentCheckInDate+1));
				data.put("lastDate", date.format(new Date()));
				//plugin.init_gui(player);
				//player.closeInventory();
				plugin.isCheckIn.put(player.getName(), true);
				plugin.playerData.put(player.getName(), data);
				player.getInventory().addItem(plugin.itemList.get(recentCheckInDate));
				if(plugin.isEco)
				{
					if(plugin.moneyList.get(recentCheckInDate)!=0)
					{
						plugin.economy.depositPlayer(player, plugin.moneyList.get(recentCheckInDate));
						player.sendMessage("��a[ǩ��ϵͳ]���ѻ��ǩ����Ʒ������"+plugin.moneyList.get(recentCheckInDate)+"��Ǯ����");
						player.openInventory(plugin.init_gui(player));
						return;
					}
				}
				player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1F, 0.0F);
				
				
				player.openInventory(plugin.init_gui(player));
				
				player.sendMessage("��a[ǩ��ϵͳ]���ѻ��ǩ����Ʒ����");
				return;
			}
		}

	}

}
