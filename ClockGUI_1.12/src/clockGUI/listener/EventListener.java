package clockGUI.listener;

import clockGUI.ClockGUI;
import clockGUI.model.ClockGuiItem;
import clockGUI.model.Function;
import clockGUI.model.PlayerData;
import clockGUI.Util.InventoryUtil;
import clockGUI.manager.ConfigManager;
import clockGUI.manager.DataManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;


public class EventListener implements Listener
{
	private ClockGUI plugin;
	private ConfigManager configManager;
	private DataManager dataManager;
	public EventListener(ClockGUI plugin)
	{
		this.plugin=plugin;
		configManager = plugin.configManager;
		dataManager = plugin.dataManager;
	}

	public void getItem(Player player)
	{

		if (!player.getInventory().contains(configManager.clock))
		{
			player.getInventory().addItem(configManager.clock);
		}
		
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
    {
		if(configManager.autoGetClock)
			getItem(event.getPlayer());
    }

	@EventHandler
	public void onPlayerInteraction(PlayerInteractEvent event)
	{
		if((event.getAction()==Action.RIGHT_CLICK_BLOCK)||
				(event.getAction()==Action.RIGHT_CLICK_AIR))
		{
			Player player = event.getPlayer();
			World world = player.getWorld();
			if(!configManager.enableWorlds.contains(world.getName()))
				return;

			ItemStack itemStack = event.getPlayer().getInventory().getItemInMainHand();
			ItemMeta itemMeta = itemStack.getItemMeta();
			if(itemStack.getType()==Material.AIR)
				return;
			if(!itemMeta.hasLore())
				return;
			if(!itemMeta.hasDisplayName())
				return;
			if(itemMeta.getDisplayName().
					equals(configManager.clock.getItemMeta().getDisplayName()))
			{
				if(itemMeta.getLore().equals(configManager.clock.getItemMeta().getLore()))
				{
					String mainGuiId = configManager.mainGuiId;
					Inventory inv = InventoryUtil.initInventory(player, configManager.guiNameList.get(mainGuiId),
							configManager.list.get(mainGuiId), mainGuiId, dataManager.getPlayerData());
					player.openInventory(inv);
				}
			}
		}
	}

	
	@EventHandler
	public void onPlayerClickInventory(InventoryClickEvent event)
	{
		if(configManager.guiNameList.containsValue(event.getView().getTitle()))
		{
			Player p = (Player) event.getWhoClicked();
			event.setCancelled(true);
			if(event.getRawSlot()<0)
				return;

			// ==================================================================================
			// To get the index of "plugin.list" depends on the index of the inventory's title in
			// "plugin.guiNameList".
			// ==================================================================================
			String key = null;
			for(String guiId:configManager.guiNameList.keySet())
			{
				if(configManager.guiNameList.get(guiId).equalsIgnoreCase(event.getView().getTitle()))
				{
					key = guiId;
					break;
				}
			}
			
			if(key==null || !configManager.list.get(key).containsKey(event.getRawSlot()))
			{
				return;
			}
			
			// ===========================================
			// Return if click nothing
			// ===========================================
			if(event.getInventory().getItem(event.getRawSlot())==null)
				return;


			ClockGuiItem clockGuiItem = configManager.list.get(key).get(event.getRawSlot());
			// ====================================
			// Check if the player has enough money
			// ====================================
			int money = clockGuiItem.getMoney().getPrice();

			if(clockGuiItem.getMoney().getCostType().equalsIgnoreCase("Money")
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
			else if(clockGuiItem.getMoney().getCostType().equalsIgnoreCase("PlayerPoints")
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
			if(!clockGuiItem.getMessage().isEmpty())
			{
				List<String> messageList = clockGuiItem.getMessage();
				for(String i:messageList)
				{
					p.sendMessage(i.replace('&', ChatColor.COLOR_CHAR));
				}
			}
			
			// ============================================================
			// Check if this player has already run out the number of times
			// ============================================================
			if(clockGuiItem.getFrequency()>=1)
			{
				int usedNumber = 0;
				PlayerData playerData = null;
				if((!dataManager.getPlayerData().isEmpty()) && dataManager.getPlayerData().containsKey(p.getName()))
				{
					playerData = dataManager.getPlayerData().get(p.getName());
				}
				else
				{
					playerData = new PlayerData();
					playerData.setNumber(key, event.getRawSlot(), 0);
				}
				
				usedNumber = playerData.getNumber(key, event.getRawSlot());
				playerData.setNumber(key, event.getRawSlot(), usedNumber+1);
				dataManager.getPlayerData().put(p.getName(), playerData);
				if(clockGuiItem.getFrequency()<=usedNumber+1)
				{
					event.getInventory().setItem(event.getRawSlot(), null);
				}
			}
			
			// =====================================
			// Check if the functions are both false
			// =====================================
			if(clockGuiItem.getFunction().getType().equalsIgnoreCase("none"))
				return;
			
			// ========================================
			// Run functions depends on function's type
			// ========================================
			if(clockGuiItem.getFunction().getType().equalsIgnoreCase("command"))
			{
				Function function = clockGuiItem.getFunction();
				if(function.shouldCloseGui())
					p.closeInventory();

				for(String command:function.getCommand())
				{
					command = command.replace("{player}", p.getName());
					if(function.shouldRunAsOp())
					{
						if(p.hasPermission("clock.bypass"))
						{
							Bukkit.dispatchCommand(p, command);
						}
						else
						{
							p.setOp(true);
							Bukkit.dispatchCommand(p, command);
							p.setOp(false);
						}
					}
					else
					{
						Bukkit.dispatchCommand(p, command);
					}
				}
			} else if(clockGuiItem.getFunction().getType().equalsIgnoreCase("gui")) {
				String OpenGUIId = clockGuiItem.getFunction().getGuiId();

				Inventory inv = InventoryUtil.initInventory(p, configManager.guiNameList.get(OpenGUIId),
						configManager.list.get(OpenGUIId), OpenGUIId, dataManager.getPlayerData());

				p.openInventory(inv);
			} else if(clockGuiItem.getFunction().getType().equalsIgnoreCase("guiAndCommand"))
			{
				for(String i:clockGuiItem.getFunction().getCommand())
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
				
				String OpenGUIId = clockGuiItem.getFunction().getGuiId();

				Inventory inv = InventoryUtil.initInventory(p, configManager.guiNameList.get(OpenGUIId),
						configManager.list.get(OpenGUIId), OpenGUIId, dataManager.getPlayerData());

				p.openInventory(inv);
			}
		}
	}
}

