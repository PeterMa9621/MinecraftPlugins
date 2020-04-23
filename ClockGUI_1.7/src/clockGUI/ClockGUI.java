package clockGUI;

import clockGUI.Util.InventoryUtil;
import clockGUI.listener.EventListener;
import clockGUI.manager.ConfigManager;
import clockGUI.manager.DataManager;
import clockGUI.model.ClockGuiItem;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.Bukkit;
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
	public boolean isEco;
	public boolean isPP = true;
	boolean useDps = true;
	
	public PlayerPoints playerPoints;

	public ConfigManager configManager;
	public DataManager dataManager;
	
	private boolean hookPlayerPoints() 
	{
	    final Plugin plugin = this.getServer().getPluginManager().getPlugin("PlayerPoints");
	    playerPoints = PlayerPoints.class.cast(plugin);
	    return playerPoints != null; 
	}
	
	private boolean setupEconomy()
	{
		RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServicesManager().getRegistration(Economy.class);
		if (economyProvider != null)
		{
			economy = economyProvider.getProvider();
		}
		return (economy!=null);
	}
	
	public void onEnable() 
	{
		if(Bukkit.getPluginManager().getPlugin("Vault")!=null)
		{
			isEco=setupEconomy();
		}
		if(!isEco)
		{
			Bukkit.getConsoleSender().sendMessage("§a[ClockGUI] §4Valut未加载!");
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
					Inventory inv = InventoryUtil.initInventory(p, configManager.guiNameList.get(0), configManager.list.get(0), 0, dataManager.getPlayerData());
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
					sender.sendMessage("§a/clock give   §6给予钟表菜单");
					sender.sendMessage("§a/clock open [GUI编号]   §6打开GUI界面(0为主菜单)");
					sender.sendMessage("§a/clock delete [GUI编号] [按钮位置]  §6删除该按钮位置的玩家使用次数数据");
					sender.sendMessage("§a/clock reload   §6重载配置");
				}
				return true;
			}

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
			
			if (args[0].equalsIgnoreCase("give"))
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
						if(args[1].matches("[0-9]*"))
						{
							if(Integer.parseInt(args[1])<=configManager.list.size()-1 && Integer.parseInt(args[1])>=0)
							{
								Inventory inv = InventoryUtil.initInventory(p, configManager.guiNameList.get(Integer.valueOf(args[1])),
										configManager.list.get(Integer.valueOf(args[1])), Integer.parseInt(args[1]), dataManager.getPlayerData());
								p.openInventory(inv);
							}
							else
							{
								sender.sendMessage("§a[钟表菜单] §c没有以这个为编号的GUI");
							}
						}
						else
						{
							sender.sendMessage("§a[钟表菜单] §c编号必须为数字");
						}
					}
				}
				else
				{
					sender.sendMessage("§a/clock open [GUI编号] §6打开钟表菜单(0为主菜单)");
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

