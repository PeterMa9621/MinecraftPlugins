package checkInSystem;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.text.SimpleDateFormat;
import java.util.UUID;

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

	SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
	public CheckInSystemListener(CheckInSystem plugin)
	{
		this.plugin=plugin;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
    {
		plugin.loadPlayerConfig(event.getPlayer().getUniqueId());
    }
	
	@EventHandler
	private void onPlayerClickInventory(InventoryClickEvent event) throws IOException {
		if(event.getView().getTitle().equalsIgnoreCase("签到"))
		{
			Player player = (Player)event.getWhoClicked();
			event.setCancelled(true);

			HashMap<String, String> data = plugin.playerData.get(player.getUniqueId());
			int recentCheckInDate = Integer.parseInt(data.get("days"));
			
			if(plugin.isCheckIn.get(player.getUniqueId())) {
				return;
			}
			
			if(event.getRawSlot()==recentCheckInDate)
			{
				data.put("days", String.valueOf(recentCheckInDate+1));
				data.put("lastDate", date.format(new Date()));
				//plugin.init_gui(player);
				//player.closeInventory();
				plugin.savePlayerConfig(player.getUniqueId(), true, data);

				player.getInventory().addItem(plugin.itemList.get(recentCheckInDate));
				if(plugin.isEco)
				{
					if(plugin.moneyList.get(recentCheckInDate)!=0)
					{
						plugin.economy.depositPlayer(player, plugin.moneyList.get(recentCheckInDate));
						player.sendMessage("§a[签到系统]你已获得签到物品奖励和"+plugin.moneyList.get(recentCheckInDate)+"金钱奖励");
						player.openInventory(plugin.init_gui(player));
						return;
					}
				}
				player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1F, 0.0F);
				
				
				player.openInventory(plugin.init_gui(player));
				
				player.sendMessage("§a[签到系统]你已获得签到物品奖励");
			}
		}

	}

}
