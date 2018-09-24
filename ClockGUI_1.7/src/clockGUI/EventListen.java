package clockGUI;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import clockGUI.ClockGUI;

import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;


public class EventListen implements Listener 
{
	private ClockGUI plugin;
	public EventListen(ClockGUI plugin)
	{
		this.plugin=plugin;
	}

	public void getItem(Player player)
	{
		if (!player.getInventory().contains(plugin.clock))
		{
			player.getInventory().addItem(plugin.clock);
		}
		
	}
	
	//监视玩家进入时，执行
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
    {
		if(plugin.autoGetClock)
			getItem(event.getPlayer());
    }

	@EventHandler
	public void onPlayerInteraction(PlayerInteractEvent event)
	{
		if((event.getAction()==Action.RIGHT_CLICK_BLOCK)||
				(event.getAction()==Action.RIGHT_CLICK_AIR))
		{
			if(event.getPlayer().getItemInHand().getType()==null || 
					event.getPlayer().getItemInHand().getType()==Material.AIR)
				return;
			if(!event.getPlayer().getItemInHand().getItemMeta().hasLore())
				return;
			if(!event.getPlayer().getItemInHand().getItemMeta().hasDisplayName())
				return;
			if(event.getPlayer().getItemInHand().getItemMeta().getDisplayName().
					equals(plugin.clock.getItemMeta().getDisplayName()))
			{
				if(event.getPlayer().getItemInHand().getItemMeta().getLore().
						equals(plugin.clock.getItemMeta().getLore()))
				{
					Player player = event.getPlayer();
					Inventory inv = plugin.initInventory(player, plugin.guiNameList.get(0), plugin.list.get(0), 0);
					player.openInventory(inv);
				}
			}
		}
	}

	
	@EventHandler
	public void onPlayerClickInventory(InventoryClickEvent event)
	{
		if(plugin.guiNameList.containsValue(event.getInventory().getTitle()))
		{
			Player p = (Player) event.getWhoClicked();
			event.setCancelled(true);
			if(event.getRawSlot()<0)
				return;

			// ==================================================================================
			// To get the index of "plugin.list" depends on the index of the inventory's title in
			// "plugin.guiNameList".
			// ==================================================================================
			int index = 0;
			for(int i:plugin.guiNameList.keySet())
			{
				if(plugin.guiNameList.get(i).equalsIgnoreCase(event.getInventory().getTitle()))
				{
					index = i;
					break;
				}
			}
			
			if(!plugin.list.get(index).keySet().contains(event.getRawSlot()))
			{
				return;
			}
			
			// ===========================================
			// Return if click nothing
			// ===========================================
			if(event.getInventory().getItem(event.getRawSlot())==null)
				return;

			// ====================================
			// Check if the player has enough money
			// ====================================
			int money = plugin.list.get(index).get(event.getRawSlot()).getMoney().getPrice();

			if(plugin.list.get(index).get(event.getRawSlot()).getMoney().getCostType().equalsIgnoreCase("Money") 
					&& plugin.isEco)
			{
				if(money!=0)
				{
					if(plugin.economy.has(p.getName(), money))
					{
						p.sendMessage("§a[钟表菜单] §6你花费了"+money+"金币");
						plugin.economy.withdrawPlayer(p.getName(), money);
					}
					else
					{
						p.sendMessage("§a[钟表菜单] §c你没有足够的金币");
						return;
					}
				}
			}
			else if(plugin.list.get(index).get(event.getRawSlot()).getMoney().getCostType().equalsIgnoreCase("PlayerPoints")
					&& plugin.isPP)
			{
				if(money!=0)
				{
					if(plugin.playerPoints.getAPI().look(p.getName())>=money)
					{
						p.sendMessage("§a[钟表菜单] §6你花费了"+money+"点券");
						plugin.playerPoints.getAPI().take(p.getName(), money);
					}
					else
					{
						p.sendMessage("§a[钟表菜单] §c你没有足够的点券");
						return;
					}
				}
			}

			// ==================================================
			// Send Messages first if the player has enough money
			// ==================================================
			if(!plugin.list.get(index).get(event.getRawSlot()).getMessage().isEmpty())
			{
				ArrayList<String> messageList = plugin.list.get(index).get(event.getRawSlot()).getMessage();
				for(String i:messageList)
				{
					p.sendMessage(i);
				}
			}
			
			
			// ============================================================
			// Check if this player has already run out the number of times
			// ============================================================
			if(plugin.list.get(index).get(event.getRawSlot()).getFrequency()>=1)
			{
				int usedNumber = 0;
				PlayerData playerData = null;
				if((!plugin.playerData.isEmpty()) && plugin.playerData.containsKey(p.getName()))
				{
					playerData = plugin.playerData.get(p.getName());
				}
				else
				{
					playerData = new PlayerData();
					playerData.setNumber(index, event.getRawSlot(), 0);
				}
				
				usedNumber = playerData.getNumber(index, event.getRawSlot());
				playerData.setNumber(index, event.getRawSlot(), usedNumber+1);
				plugin.playerData.put(p.getName(), playerData);
				if(plugin.list.get(index).get(event.getRawSlot()).getFrequency()<=usedNumber+1)
				{
					event.getInventory().setItem(event.getRawSlot(), null);
				}
			}
			
			// =====================================
			// Check if the functions are both false
			// =====================================
			if(plugin.list.get(index).get(event.getRawSlot()).getFunction().getType().equalsIgnoreCase("none"))
				return;
			
			// ========================================
			// Run functions depends on function's type
			// ========================================
			if(plugin.list.get(index).get(event.getRawSlot()).getFunction().getType().equalsIgnoreCase("command"))
			{
				
				for(String i:plugin.list.get(index).get(event.getRawSlot()).getFunction().getCommand())
				{
					if(i.contains("{ignoreOP}"))
					{
						if(p.hasPermission("clock.bypass"))
						{
							p.performCommand(i.replace("{ignoreOP}", "").replace("/", "").replace("{player}", event.getWhoClicked().getName()));
						}
						else
						{
							p.setOp(true);
							p.performCommand(i.replace("{ignoreOP}", "").replace("/", "").replace("{player}", event.getWhoClicked().getName()));
							p.setOp(false);
						}
					}
					else
					{
						p.performCommand(i.replace("/", ""));
					}
				}
				
				return;
			}

			else if(plugin.list.get(index).get(event.getRawSlot()).getFunction().getType().equalsIgnoreCase("gui"))
			{

				int OpenGUINumber = plugin.list.get(index).get(event.getRawSlot()).getFunction().getGuiNumber();

				Inventory inv = plugin.initInventory(p, plugin.guiNameList.get(OpenGUINumber),
						plugin.list.get(OpenGUINumber), OpenGUINumber);

				p.openInventory(inv);
				return;
			}
			
			else if(plugin.list.get(index).get(event.getRawSlot()).getFunction().getType().equalsIgnoreCase("guiAndCommand"))
			{
				for(String i:plugin.list.get(index).get(event.getRawSlot()).getFunction().getCommand())
				{
					if(i.contains("{ignoreOP}"))
					{
						if(p.hasPermission("clock.bypass"))
						{
							p.performCommand(i.replace("{ignoreOP}", "").replace("/", "").replace("{player}", event.getWhoClicked().getName()));
						}
						else
						{
							p.setOp(true);
							p.performCommand(i.replace("{ignoreOP}", "").replace("/", "").replace("{player}", event.getWhoClicked().getName()));
							p.setOp(false);
						}
					}
					else
					{
						p.performCommand(i.replace("/", ""));
					}
				}
				
				int OpenGUINumber = plugin.list.get(index).get(event.getRawSlot()).getFunction().getGuiNumber();

				Inventory inv = plugin.initInventory(p, plugin.guiNameList.get(OpenGUINumber),
						plugin.list.get(OpenGUINumber), OpenGUINumber);

				p.openInventory(inv);
				return;
			}

		}
		
	}

}

