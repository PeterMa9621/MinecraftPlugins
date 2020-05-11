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
			Bukkit.getConsoleSender().sendMessage("��a[ClockGUI] ��4Valutδ����!");
			isEco = false;
		}
		if(!hookPlayerPoints())
		{
			Bukkit.getConsoleSender().sendMessage("��a[ClockGUI] ��4PlayerPointsδ����!");
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
		Bukkit.getConsoleSender().sendMessage("��a[ClockGUI] ��e�ӱ�˵��������");
		Bukkit.getConsoleSender().sendMessage("��a[ClockGUI] ��e������QQ:920157557");
	}

	public void onDisable() 
	{
		configManager.savePlayerConfig();
		Bukkit.getConsoleSender().sendMessage("��a[ClockGUI] ��e�ӱ�˵�ж�����");
		Bukkit.getConsoleSender().sendMessage("��a[ClockGUI] ��e������QQ:920157557");
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
				sender.sendMessage("��a=======[�ӱ�˵�]=======");
				sender.sendMessage("��a/clock   ��6�򿪽���");
				sender.sendMessage("��a/clock help  ��6�򿪰���");
				if(sender.isOp())
				{
					sender.sendMessage("��a/clock get   ��6�����ӱ�˵�");
					sender.sendMessage("��a/clock open [GUI ID]   ��6��GUI����");
					//sender.sendMessage("��a/clock delete [GUI���] [��ťλ��]  ��6ɾ���ð�ťλ�õ����ʹ�ô�������");
					sender.sendMessage("��a/clock reload   ��6��������");
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
							sender.sendMessage("��a[�ӱ�˵�] ��c���ֻ��Ϊ����!");
							return true;
						}
						
						if(!args[2].matches("[0-9]*"))
						{
							sender.sendMessage("��a[�ӱ�˵�] ��cλ��ֻ��Ϊ����!");
							return true;
						}
						
						int guiNumber = Integer.parseInt(args[1]);
						int position = Integer.parseInt(args[2]);

						HashMap<Integer, HashMap<Integer, ClockGuiItem>> list = configManager.list;
						if(list.containsKey(guiNumber))
						{
							if(list.get(guiNumber).containsKey(position))
							{
								sender.sendMessage("��a[�ӱ�˵�] ��6׼��ɾ��...");
								configManager.deletePlayerData(guiNumber, position);
								sender.sendMessage("��a[�ӱ�˵�] ��6ɾ���ɹ�!");
							}
							else
							{
								sender.sendMessage("��a[�ӱ�˵�] ��cû�������λ�õİ�ť");
							}
						}
						else
						{
							sender.sendMessage("��a[�ӱ�˵�] ��cû�������Ϊ��ŵ�GUI");
						}
					}
					else
					{
						sender.sendMessage("��a/clock delete [GUI���] [��ťλ��]  ��6ɾ���ð�ťλ�õ����ʹ�ô�������");
					}
				}
				else
				{
					sender.sendMessage("��a[�ӱ�˵�] ��cû��Ȩ�ޣ�");
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
					sender.sendMessage("��a[�ӱ�˵�] ��cû��Ȩ�ޣ�");
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
							sender.sendMessage("��a[�ӱ�˵�] ��cû�������Ϊ��ŵ�GUI");
						}
					}
				}
				else
				{
					sender.sendMessage("��a/clock open [GUI ID] ��6���ӱ�˵�");
				}
			}
			
			if (args[0].equalsIgnoreCase("reload"))
			{
				if(sender.isOp())
				{
					configManager.loadConfig();
					sender.sendMessage("��a[�ӱ�˵�] ��6�������óɹ�");
				}
				else
				{
					sender.sendMessage("��a[�ӱ�˵�] ��cû��Ȩ�ޣ�");
				}
			}

			
			return true;
		}

		return false;
		
	}
	
	
}

