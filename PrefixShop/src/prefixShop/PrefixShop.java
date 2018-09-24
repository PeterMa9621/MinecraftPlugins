package prefixShop;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.black_ixx.playerpoints.PlayerPoints;

public class PrefixShop extends JavaPlugin
{
	ArrayList<String> prefixList = new ArrayList<String>();
	ArrayList<String> costTypeList = new ArrayList<String>();
	ArrayList<Integer> priceList = new ArrayList<Integer>();
	ArrayList<String> definePlayer = new ArrayList<String>();
	HashMap<String, ArrayList<String>> playerData = new HashMap<String, ArrayList<String>>();
	HashMap<String, String> playerOnUse = new HashMap<String, String>();
	
	//Inventory inv = null;
	boolean economySupport = false;
	
	public Economy economy;
	PlayerPoints playerPoints;
	
	private boolean setupEconomy()
	{
		RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServicesManager().getRegistration(Economy.class);
		if (economyProvider != null)
		{
			economy = economyProvider.getProvider();
		}
		return (economy!=null);
	}

	private boolean hookPlayerPoints() {
	    final Plugin plugin = this.getServer().getPluginManager().getPlugin("PlayerPoints");
	    playerPoints = PlayerPoints.class.cast(plugin);
	    return playerPoints != null; 
	}
	
	public void onEnable() 
	{
		if(!new File(getDataFolder(),"Data").exists()) 
		{
			new File(getDataFolder(),"Data").mkdirs();
		}
		if(setupEconomy()!=false)
		{
			if(hookPlayerPoints()!=false)
			{
				economySupport = true;
			}
			else
			{
				Bukkit.getConsoleSender().sendMessage("��a[PrefixShop] ��cǰ�ò����������");
				//getLogger().info("ǰ�ò����������");
			}
		}
		else
		{
			Bukkit.getConsoleSender().sendMessage("��a[PrefixShop] ��cǰ�ò����������");
			//getLogger().info("ǰ�ò����������");
		}

		loadConfig();
		loadPlayerConfig();
		getServer().getPluginManager().registerEvents(new PrefixShopListener(this), this);
		Bukkit.getConsoleSender().sendMessage("��a[PrefixShop] ��e�ƺ��̵��Ѽ������");
	}

	public void onDisable() 
	{
		saveConfig();
		Bukkit.getConsoleSender().sendMessage("��a[PrefixShop] ��e�ƺ��̵���ж�����");
	}
	
	public void loadPlayerConfig()
	{
		for(OfflinePlayer p:Bukkit.getOfflinePlayers())
		{
			File file=new File(getDataFolder(),"/Data/"+p.getName()+".yml");
			FileConfiguration config;
			ArrayList<String> myPrefix = new ArrayList<String>();
			if(!file.exists())
			{
				config = load(file);
				try {
					config.save(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				loadConfig();
			}
			config = load(file);
			String prefix = config.getString("MyPrefix");
			String onUse = config.getString("OnUse");
			if(prefix!=null)
			{
				if(prefix.contains("%%"))
				{
					for(String pre:prefix.split("%%"))
					{
						myPrefix.add(pre);
					}
				}
				else
				{
					myPrefix.add(prefix);
				}
			}
			playerData.put(p.getName(), myPrefix);
			playerOnUse.put(p.getName(), onUse);
		}
		
	}
	
	public void saveConfig()
	{
		for(String name:playerData.keySet())
		{
			File file=new File(getDataFolder(),"/Data/"+name+".yml");
			FileConfiguration config;
			config = load(file);
			String myPrefix = null;
			for(String prefix:playerData.get(name))
			{
				if(myPrefix==null)
				{
					myPrefix = prefix;
				}
				else
				{
					myPrefix = myPrefix + "%%" + prefix;
				}
			}
			config.set("MyPrefix", myPrefix);
			config.set("OnUse", playerOnUse.get(name));
			try {
				config.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void loadConfig()
	{
		File file=new File(getDataFolder(),"config.yml");
		FileConfiguration config;
		if (!file.exists())
		{
			config = load(file);
			
			config.set("Prefix.1.Content", "&a�ƺ�");
			config.set("Prefix.1.Cost.Type", "Money");
			config.set("Prefix.1.Cost.Price", 1000);
			config.set("Prefix.2.Content", "&a����");
			config.set("Prefix.2.Cost.Type", "PlayerPoints");
			config.set("Prefix.2.Cost.Price", 500);
			config.set("Prefix.3.Content", "&c���Ǵ���");
			config.set("Prefix.3.Cost.Type", "Money");
			config.set("Prefix.3.Cost.Price", 0);
			
			try 
			{
				config.save(file);
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			loadConfig();
			return;
		}
		
		config = load(file);
		costTypeList.clear();
		prefixList.clear();
		priceList.clear();
		String prefix = null;
		String costType = null;
		int price = 0;

		for(int i=0; config.contains("Prefix."+(i+1)); i++)
		{
			prefix = config.getString("Prefix."+(i+1)+".Content");
			if(prefix.contains("&"))
			{
				prefix = prefix.replace("&", "��");
			}
			costType = config.getString("Prefix."+(i+1)+".Cost.Type");
			price = config.getInt("Prefix."+(i+1)+".Cost.Price");

			costTypeList.add(costType);
			priceList.add(price);
			prefixList.add(prefix);
		}
	}
	
	public Inventory initShop(Player p)
	{
		if(economySupport==false)
		{
			p.sendMessage("��a[�ƺ�ϵͳ]��c�ƺ��̵��޷���ȷʹ�ã�");
			Inventory inv = Bukkit.createInventory(p, 9, "��5�ƺ��̵�");
			return inv;
		}
		ArrayList<String> myPrefix = playerData.get(p.getName());
		
		Inventory inv = Bukkit.createInventory(p, ((prefixList.size()/9)+1)*9, "��5�ƺ��̵�");
		ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
		String costType = null;
		
		for(int i=0; i<prefixList.size(); i++)
		{
			if(costTypeList.get(i).equalsIgnoreCase("Money"))
				costType = "���";
			else
				costType = "��ȯ";
			ArrayList<String> lore = new ArrayList<String>();
			lore.add("��5�������óƺ�");
			lore.add("��5�۸�:��a"+priceList.get(i)+"��5"+costType);
			if(!myPrefix.isEmpty())
			{
				if(myPrefix.contains(prefixList.get(i)))
				{
					lore.add("��4��ӵ��");
				}
			}
			ItemMeta meta = book.getItemMeta();
			meta.setDisplayName("��f�ƺ�:"+prefixList.get(i));
			meta.setLore(lore);
			book.setItemMeta(meta);
			
			inv.setItem(i, book);
		}
		
		return inv;
	}
	
	public Inventory initMyGUI(Player p)
	{
		ArrayList<String> myPrefix = playerData.get(p.getName());
		
		Inventory inv = Bukkit.createInventory(p, ((myPrefix.size()/9)+1)*9, "��5�ҵĳƺ�");
		if(myPrefix.isEmpty())
			return inv;
		ItemStack book = new ItemStack(Material.BOOK);
		String onUse = playerOnUse.get(p.getName());
		
		for(int i=0; i<myPrefix.size(); i++)
		{
			
			ArrayList<String> lore = new ArrayList<String>();
			lore.add("��a�������л��óƺ�");
			lore.add("��c�Ҽ������4ɾ����c�óƺ�");
			if(onUse!=null && onUse.equalsIgnoreCase(myPrefix.get(i)))
			{
				lore.add("��a��ǰ�ƺ�");
			}
			ItemMeta meta = book.getItemMeta();
			meta.setDisplayName("��f�ƺ�:"+myPrefix.get(i));
			meta.setLore(lore);
			book.setItemMeta(meta);
			
			inv.setItem(i, book);
		}
		
		return inv;
	}
	
	public FileConfiguration load(File file)
	{
        if (!(file.exists())) 
        { //�����ļ�������
        	try   //��׽�쳣����Ϊ�п��ܴ������ɹ�
        	{
        		file.createNewFile();
        	}
        	catch(IOException e)
        	{
        		e.printStackTrace();
        	}
        }
        return YamlConfiguration.loadConfiguration(file);
	}
	public FileConfiguration load(String path)
	{
		File file=new File(path);
		if(!file.exists())
		{
			try
		{
				file.createNewFile();
		}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		return YamlConfiguration.loadConfiguration(new File(path));
	}
	
	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args)  
	{
		if (cmd.getName().equalsIgnoreCase("ch"))
		{
			if (args.length==0)
			{
				sender.sendMessage("��a=========[�ƺ�ϵͳ]=========");
				sender.sendMessage("��a/ch shop ��3�򿪳ƺ��̵�");
				sender.sendMessage("��a/ch my ��3���ҵĳƺŽ���");
				if(sender.isOp())
				{
					sender.sendMessage("��a/ch give [�����] [�ƺ�] ��3�Զ���ƺ�");
					sender.sendMessage("��a/ch define ��3�Զ���ƺ�");
					sender.sendMessage("��a/ch reload ��3��������");
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("reload"))
			{
				if(sender instanceof Player)
				{
					if(sender.isOp())
					{
						loadConfig();
						sender.sendMessage("��a[�ƺ�ϵͳ] ��6�ض����óɹ���");
					}
					return true;
				}
			}
			
			if(args[0].equalsIgnoreCase("shop"))
			{
				if(sender instanceof Player)
				{
					Player p = (Player)sender;
					Inventory shop = initShop(p);
					p.openInventory(shop);
					return true;
				}
			}
			
			if(args[0].equalsIgnoreCase("give"))
			{
				if(sender.isOp())
				{
					if(args.length==3)
					{
						if(Bukkit.getServer().getPlayer(args[1])!=null)
						{
							ArrayList<String> myPrefix = new ArrayList<String>();
							myPrefix = playerData.get(args[1]);
							myPrefix.add(args[2]);
							playerData.put(args[1], myPrefix);
							sender.sendMessage("��a[�ƺ�ϵͳ] ��6�Ѹ�����ҡ�c"+ args[1] + "��6�ƺ�:��e"+args[2]);
						}
						else
						{
							sender.sendMessage("��a[�ƺ�ϵͳ] ��c����Ҳ����߻򲻴���");
						}
					}
					else
					{
						sender.sendMessage("��a/ch give [�����] [�ƺ�] ��3�Զ���ƺ�");
						return true;
					}
				}
				else
				{
					sender.sendMessage("��a[�ƺ�ϵͳ] ��c��û��Ȩ��");
				}
			}
			
			if(args[0].equalsIgnoreCase("my"))
			{
				if(sender instanceof Player)
				{
					Player p = (Player)sender;
					Inventory my = initMyGUI(p);
					p.openInventory(my);
					return true;
				}
			}
			
			if(args[0].equalsIgnoreCase("define"))
			{
				if(sender instanceof Player)
				{
					Player p = (Player)sender;
					if(p.hasPermission("PrefixShop.define"))
					{
						definePlayer.add(p.getName());
						p.sendMessage("��a[�ƺ�ϵͳ] ��3��Ŀǰ�ѽ����Զ���ƺŽ׶�");
						p.sendMessage("��a[�ƺ�ϵͳ] ��3��������Ҫ�Զ���ĳƺ�(���5���֣�����exit�˳�):");
						return true;
					}
					else
					{
						p.sendMessage("��a[�ƺ�ϵͳ] ��c��û��Ȩ��");
						return true;
					}
				}
			}
			return true;
		}
		return false;
	}
	
	
}

