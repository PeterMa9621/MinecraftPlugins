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
			Bukkit.getConsoleSender().sendMessage("��a[ClockGUI] ��4Valutδ����!");
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
					Inventory inv = InventoryUtil.initInventory(p, configManager.guiNameList.get(0), configManager.list.get(0), 0, dataManager.getPlayerData());
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
					sender.sendMessage("��a/clock give   ��6�����ӱ�˵�");
					sender.sendMessage("��a/clock open [GUI���]   ��6��GUI����(0Ϊ���˵�)");
					sender.sendMessage("��a/clock delete [GUI���] [��ťλ��]  ��6ɾ���ð�ťλ�õ����ʹ�ô�������");
					sender.sendMessage("��a/clock reload   ��6��������");
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
								sender.sendMessage("��a[�ӱ�˵�] ��cû�������Ϊ��ŵ�GUI");
							}
						}
						else
						{
							sender.sendMessage("��a[�ӱ�˵�] ��c��ű���Ϊ����");
						}
					}
				}
				else
				{
					sender.sendMessage("��a/clock open [GUI���] ��6���ӱ�˵�(0Ϊ���˵�)");
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

