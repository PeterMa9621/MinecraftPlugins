package clockGUI;

import clockGUI.Util.InventoryUtil;
import clockGUI.listener.EventListener;
import clockGUI.manager.ConfigManager;
import clockGUI.manager.DataManager;
import clockGUI.model.ClockGuiItem;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

//import org.bukkit.Location;

public class ClockGUI extends JavaPlugin
{
	public Economy economy;
	public boolean isEco = true;
	public boolean isPP = true;
	
	public PlayerPoints playerPoints;

	public ConfigManager configManager;
	public DataManager dataManager;
	
	private boolean hookPlayerPoints() 
	{
		if(Bukkit.getPluginManager().getPlugin("PlayerPoints")!=null) {
			final Plugin plugin = this.getServer().getPluginManager().getPlugin("PlayerPoints");
			playerPoints = (PlayerPoints) plugin;
			return playerPoints != null;
		}
	    return false;
	}
	
	private boolean setupEconomy()
	{
		if(Bukkit.getPluginManager().getPlugin("Vault")!=null) {
			RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServicesManager().getRegistration(Economy.class);
			if (economyProvider != null) {
				economy = economyProvider.getProvider();
			}
			return economy != null;
		}
		return false;
	}
	
	public void onEnable() 
	{
		if(!setupEconomy())
		{
			Bukkit.getConsoleSender().sendMessage("§a[ClockGUI] §4Valut未加载!");
			isEco = false;
		}
		if(!hookPlayerPoints())
		{
			Bukkit.getConsoleSender().sendMessage("§a[ClockGUI] §4PlayerPoints未加载!");
			isPP = false;
		}

		if(!getDataFolder().exists())
		{
			getDataFolder().mkdir();
		}

		dataManager = new DataManager(this);
		configManager = new ConfigManager(this);

		configManager.loadConfig();

		getServer().getPluginManager().registerEvents(new EventListener(this), this);
		Bukkit.getConsoleSender().sendMessage("§a[ClockGUI] §e钟表菜单加载完毕");
		Bukkit.getConsoleSender().sendMessage("§a[ClockGUI] §e制作者QQ:920157557");
	}

	public void onDisable() 
	{
		configManager.savePlayerConfig();
		Bukkit.getConsoleSender().sendMessage("§a[ClockGUI] §e钟表菜单卸载完毕");
		Bukkit.getConsoleSender().sendMessage("§a[ClockGUI] §e制作者QQ:920157557");
	}

	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args)  
	{
		if (cmd.getName().equalsIgnoreCase("clock")) 
		{
			if(args.length==0) {
				if (sender instanceof Player) {
					Player p = (Player)sender;
					World world = p.getWorld();
					if(!configManager.enableWorlds.contains(world.getName()))
						return true;
					String mainGuiId = configManager.mainGuiId;
					Inventory inv = InventoryUtil.initInventory(p, configManager.guiNameList.get(mainGuiId),
							configManager.list.get(mainGuiId), configManager.mainGuiId, dataManager.getPlayerData());
					p.openInventory(inv);
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("help"))
			{
				sender.sendMessage("§a=======[钟表菜单]=======");
				sender.sendMessage("§a/clock   §6打开界面");
				sender.sendMessage("§a/clock help  §6打开帮助");
				if(sender.isOp())
				{
					sender.sendMessage("§a/clock get   §6给予钟表菜单");
					sender.sendMessage("§a/clock open [GUI ID]   §6打开GUI界面");
					//sender.sendMessage("§a/clock delete [GUI编号] [按钮位置]  §6删除该按钮位置的玩家使用次数数据");
					sender.sendMessage("§a/clock reload   §6重载配置");
				}
				return true;
			}

			/*
			if (args[0].equalsIgnoreCase("delete"))
			{
				if (sender.isOp())
				{
					if(args.length==3)
					{
						if(!args[1].matches("[0-9]*"))
						{
							sender.sendMessage("§a[钟表菜单] §c编号只能为数字!");
							return true;
						}
						
						if(!args[2].matches("[0-9]*"))
						{
							sender.sendMessage("§a[钟表菜单] §c位置只能为数字!");
							return true;
						}
						
						int guiNumber = Integer.parseInt(args[1]);
						int position = Integer.parseInt(args[2]);

						HashMap<Integer, HashMap<Integer, ClockGuiItem>> list = configManager.list;
						if(list.containsKey(guiNumber))
						{
							if(list.get(guiNumber).containsKey(position))
							{
								sender.sendMessage("§a[钟表菜单] §6准备删除...");
								configManager.deletePlayerData(guiNumber, position);
								sender.sendMessage("§a[钟表菜单] §6删除成功!");
							}
							else
							{
								sender.sendMessage("§a[钟表菜单] §c没有在这个位置的按钮");
							}
						}
						else
						{
							sender.sendMessage("§a[钟表菜单] §c没有以这个为编号的GUI");
						}
					}
					else
					{
						sender.sendMessage("§a/clock delete [GUI编号] [按钮位置]  §6删除该按钮位置的玩家使用次数数据");
					}
				}
				else
				{
					sender.sendMessage("§a[钟表菜单] §c没有权限！");
				}
			}

			 */
			
			if (args[0].equalsIgnoreCase("get"))
			{
				if(sender.isOp())
				{
					if (sender instanceof Player)
					{
						Player p = (Player)sender;
						p.getInventory().addItem(configManager.clock);
					}
				}
				else
				{
					sender.sendMessage("§a[钟表菜单] §c没有权限！");
				}
			}
			
			if (args[0].equalsIgnoreCase("open"))
			{
				if(args.length==2)
				{
					if (sender instanceof Player)
					{
						Player p = (Player)sender;
						String guiId = args[1];
						if(configManager.guiNameList.containsKey(guiId))
						{
							Inventory inv = InventoryUtil.initInventory(p, configManager.guiNameList.get(guiId),
									configManager.list.get(guiId), guiId, dataManager.getPlayerData());
							p.openInventory(inv);
						}
						else
						{
							sender.sendMessage("§a[钟表菜单] §c没有以这个为编号的GUI");
						}
					}
				}
				else
				{
					sender.sendMessage("§a/clock open [GUI ID] §6打开钟表菜单");
				}
			}
			
			if (args[0].equalsIgnoreCase("reload"))
			{
				if(sender.isOp())
				{
					configManager.loadConfig();
					sender.sendMessage("§a[钟表菜单] §6重载配置成功");
				}
				else
				{
					sender.sendMessage("§a[钟表菜单] §c没有权限！");
				}
			}

			
			return true;
		}

		return false;
		
	}
	
	
}

