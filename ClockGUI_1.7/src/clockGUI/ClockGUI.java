package clockGUI;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import clockGUI.EventListen;
import net.milkbowl.vault.economy.Economy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.Bukkit;
import org.bukkit.Material;
//import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ClockGUI extends JavaPlugin
{
	public ItemStack clock = new ItemStack(Material.WATCH);

	HashMap<Integer, String> guiNameList = new HashMap<Integer, String>();
	
	HashMap<Integer, HashMap<Integer, ClockGuiItem>> list = new HashMap<Integer, HashMap<Integer, ClockGuiItem>>();
	
	HashMap<String, PlayerData> playerData = new HashMap<String, PlayerData>();
	
	boolean autoGetClock = false;
	
	public Economy economy;
	boolean isEco;
	boolean isPP = true;
	
	public PlayerPoints playerPoints;
	
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
		if(isEco==false)
		{
			Bukkit.getConsoleSender().sendMessage("��a[ClockGUI] ��4Valutδ����!");
		}
		if(hookPlayerPoints()==false)
		{
			Bukkit.getConsoleSender().sendMessage("��a[ClockGUI] ��4PlayerPointsδ����!");
			isPP = false;
		}
		if(!getDataFolder().exists())
		{
			getDataFolder().mkdir();
		}
		loadConfig();
		loadPlayerConfig();
		getServer().getPluginManager().registerEvents(new EventListen(this), this);
		Bukkit.getConsoleSender().sendMessage("��a[ClockGUI] ��e�ӱ�˵��������");
		Bukkit.getConsoleSender().sendMessage("��a[ClockGUI] ��e������QQ:920157557");
	}

	public void onDisable() 
	{
		savePlayerConfig();
		Bukkit.getConsoleSender().sendMessage("��a[ClockGUI] ��e�ӱ�˵�ж�����");
		Bukkit.getConsoleSender().sendMessage("��a[ClockGUI] ��e������QQ:920157557");
	}
	
	public ItemStack setItem(ItemStack item, String name, ArrayList<String> lore, int ItemID)
	{
		ItemMeta meta = item.getItemMeta();
		if(name!=null)
		{
			meta.setDisplayName(name);
		}

		if(lore!=null)
		{
			meta.setLore(lore);
		}
		item.setItemMeta(meta);

		if(ItemID!=0)
		{
			item.setTypeId(ItemID);
		}
		return item;
	}
	
	public void loadPlayerConfig()
	{
		File file=new File(getDataFolder(),"/Data/player.yml");
		FileConfiguration config;
		if (!file.exists())
		{
			return;
		}
		
		config = load(file);
		for(String playerName:config.getKeys(false))
		{
			PlayerData playerData = new PlayerData();
			for(String guiNumber:config.getConfigurationSection(playerName).getKeys(false))
			{
				for(String position:config.getConfigurationSection(playerName+"."+guiNumber).getKeys(false))
				{
					int usedNumber = config.getInt(playerName+"."+guiNumber+"."+position);
					playerData.setNumber(Integer.valueOf(guiNumber), Integer.valueOf(position), usedNumber);
				}
			}
			this.playerData.put(playerName, playerData);
		}
	}
	
	public void savePlayerConfig()
	{
		File file=new File(getDataFolder(),"/Data/player.yml");
		FileConfiguration config;
		config = load(file);
		
		for(String playerName:playerData.keySet())
		{
			for(int guiNumber:playerData.get(playerName).getGuiInfo().keySet())
			{
				for(int position:playerData.get(playerName).getGuiInfo().get(guiNumber).keySet())
				{
					config.set(playerName+"."+guiNumber+"."+position, playerData.get(playerName).getNumber(guiNumber, position));
				}
			}
		}
		
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void loadConfig()
	{
		File file=new File(getDataFolder(),"config.yml");
		FileConfiguration config;
		if (!file.exists())
		{
			config = load(file);
			
			// Clock Settings
			config.set("Clock.AutoGetClock", true);
			config.set("Clock.Name", "��a�ӱ�˵�");
			config.set("Clock.Lore", "��1�ҵ������ӱ�˵�%��2�Ҽ��Ҵ򿪲˵�");
			
			config.set("GUI.0.Name", "��1�ҵ������ӱ�˵�");
			config.set("GUI.0.Item.1.Position", 1);
			config.set("GUI.0.Item.1.ItemID", "264");
			config.set("GUI.0.Item.1.Name", "ʾ��1");
			config.set("GUI.0.Item.1.Lore", "���ǵ�һ��%���ǵڶ���%���ǵ�����");
			config.set("GUI.0.Item.1.Enchantment.ID", 0);
			config.set("GUI.0.Item.1.Enchantment.Level", 1);
			//config.set("GUI.0.Item.1.HideEnchant", true);
			config.set("GUI.0.Item.1.Cost.Type", "Money");
			config.set("GUI.0.Item.1.Cost.Price", 1000);
			config.set("GUI.0.Item.1.Message", "��a���Ѱ��������ť%��c����");
			config.set("GUI.0.Item.1.Frequency", 1);
			config.set("GUI.0.Item.1.Function.OpenAnotherGUI.Use", true);
			config.set("GUI.0.Item.1.Function.OpenAnotherGUI.Number", 1);
			config.set("GUI.0.Item.1.Function.Command.Use", false);
			config.set("GUI.0.Item.1.Function.Command.Content", "/say �ӱ�˵�");
			config.set("GUI.0.Item.2.Position", 10);
			config.set("GUI.0.Item.2.ItemID", "397:1");
			config.set("GUI.0.Item.2.Name", "ʾ��2");
			config.set("GUI.0.Item.2.Lore", "���ǵ�һ��%���ǵڶ���%���ǵ�����");
			config.set("GUI.0.Item.2.Cost.Type", "PlayerPoints");
			config.set("GUI.0.Item.2.Cost.Price", 500);
			config.set("GUI.0.Item.2.Function.Command.Use", true);
			config.set("GUI.0.Item.2.Function.Command.Content", "/say �ӱ�˵�%/eco set {player} 10000{ignoreOP}");
			config.set("GUI.0.Item.2.Function.OpenAnotherGUI.Use", false);
			config.set("GUI.0.Item.2.Function.OpenAnotherGUI.Number", 2);
			
			// GUI Settings
			config.set("GUI.1.Name", "��һ��GUI");
			config.set("GUI.1.Item.1.Position", 3);
			config.set("GUI.1.Item.1.ItemID", "264");
			config.set("GUI.1.Item.1.Name", "ʾ��3");
			config.set("GUI.1.Item.1.Lore", "���ǵ�һ��%���ǵڶ���%���ǵ�����");
			config.set("GUI.1.Item.1.Function.Command.Use", true);
			config.set("GUI.1.Item.1.Function.Command.Content", "/say ��һ��GUI");
			config.set("GUI.1.Item.2.Position", 16);
			config.set("GUI.1.Item.2.ItemID", "388");
			config.set("GUI.1.Item.2.Name", "ʾ��4");
			config.set("GUI.1.Item.2.Lore", "���ǵ�һ��%���ǵڶ���%���ǵ�����");
			config.set("GUI.1.Item.2.Function.Command.Use", true);
			config.set("GUI.1.Item.2.Function.Command.Content", "/say �ӱ�˵�");
			
			
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
		
		autoGetClock = config.getBoolean("Clock.AutoGetClock");
		
		String clockName = config.getString("Clock.Name");
		ArrayList<String> clockLore = new ArrayList<String>();
		for(String i:config.getString("Clock.Lore").split("%"))
		{
			clockLore.add(i);
		}
		clock = setItem(clock, clockName, clockLore, 0);

		for(int i=0; config.contains("GUI."+i); i++)
		{
			String GUIName = config.getString("GUI."+i+".Name");
			
			HashMap<Integer, ClockGuiItem> guiList = new HashMap<Integer, ClockGuiItem>();
			
			for(int x=0; config.contains("GUI."+i+".Item."+(x+1)); x++)
			{
				int enchantID = config.getInt("GUI."+i+".Item."+(x+1)+".Enchantment.ID");
				int enchantLevel = config.getInt("GUI."+i+".Item."+(x+1)+".Enchantment.Level");
				boolean hide = config.getBoolean("GUI."+i+".Item."+(x+1)+".HideEnchant");
				
				int position = config.getInt("GUI."+i+".Item."+(x+1)+".Position");
				String itemID = config.getString("GUI."+i+".Item."+(x+1)+".ItemID");
				String itemName = config.getString("GUI."+i+".Item."+(x+1)+".Name");
				String itemLore = config.getString("GUI."+i+".Item."+(x+1)+".Lore");
				Boolean openGUI = config.getBoolean("GUI."+i+".Item."+(x+1)+".Function.OpenAnotherGUI.Use");
				int guiNumber = config.getInt("GUI."+i+".Item."+(x+1)+".Function.OpenAnotherGUI.Number");
				Boolean command = config.getBoolean("GUI."+i+".Item."+(x+1)+".Function.Command.Use");
				String commandContent = config.getString("GUI."+i+".Item."+(x+1)+".Function.Command.Content");
				String costType = config.getString("GUI."+i+".Item."+(x+1)+".Cost.Type");
				int price = config.getInt("GUI."+i+".Item."+(x+1)+".Cost.Price");
				String message = config.getString("GUI."+i+".Item."+(x+1)+".Message");
				int frequency = config.getInt("GUI."+i+".Item."+(x+1)+".Frequency");
				
				ArrayList<String> commandList = new ArrayList<String>();
				ItemStack item = null;
				if(itemID.contains(":"))
				{
					int id = Integer.valueOf(itemID.split(":")[0]);
					int damage = Integer.valueOf(itemID.split(":")[1]);
					item = this.createItem(id, 1, damage, itemName, itemLore);
				}
				else
				{
					item = this.createItem(Integer.valueOf(itemID), 1, 0, itemName, itemLore);
				}
				
				if(enchantID>=0 && enchantLevel>0)
					item.addUnsafeEnchantment(Enchantment.getById(enchantID), enchantLevel);
				
				Money money = null;
				if(costType!=null)
				{
					money = new Money(costType, price);
				}
				else
				{
					money = new Money("Money", 0);
				}
				
				
				Function function = null;
				if(commandContent==null)
					commandContent="say ������";
				for(String everyCommand:commandContent.split("%"))
				{
					commandList.add(everyCommand);
				}

				if(command==true && openGUI==false)
				{
					function = new Function("command", commandList);
				}
				else if(command==false && openGUI==true)
				{
					function = new Function("gui", guiNumber);
				}
				else if(command==true && openGUI==true)
				{
					function = new Function("guiAndCommand", guiNumber, commandList);
				}
				else if(command==false && openGUI==false)
				{
					function = new Function("none", null);
				}
				
				ClockGuiItem guiItem = new ClockGuiItem(item, function, money, message, frequency);
				
				guiList.put(position, guiItem);

			}
			guiNameList.put(i, GUIName);
			list.put(i, guiList);
		}
		return;
		
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
	
	public ItemStack createItem(int ID, int quantity, int durability, String displayName, String lore)
	{
		ItemStack item = new ItemStack(ID, quantity, (short)durability);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(displayName);
		ArrayList<String> loreList = new ArrayList<String>();
		for(String l:lore.split("%"))
		{
			loreList.add(l);
		}
		meta.setLore(loreList);
		item.setItemMeta(meta);
		
		return item;
	}

	
	public Inventory initInventory(Player player, String inventoryName, 
			HashMap<Integer, ClockGuiItem> guiItems, int guiNumber)
	{
		int largest = 0;
		int time = 1;
		for(int i:guiItems.keySet())
		{
			if(i>largest)
			{
				largest=i;
			}
		}
		if(largest%9!=0)
		{
			time = (largest/9)+1;
		}
		else
		{
			time = largest/9;
		}
		
		Inventory inv = Bukkit.createInventory(player, 9*time, inventoryName);
		
		for(int i:guiItems.keySet())
		{
			if(guiItems.get(i).getFrequency()>=1)
			{
				if(playerData.containsKey(player.getName()))
				{
					if(playerData.get(player.getName()).getGuiInfo().containsKey(guiNumber))
					{
						if(playerData.get(player.getName()).getButtonInfo(guiNumber).containsKey(i))
						{
							int playerUsedNumber = playerData.get(player.getName()).getNumber(guiNumber, i);
							
							int frequency = guiItems.get(i).getFrequency();
							if(playerUsedNumber<frequency)
							{
								inv.setItem(i, guiItems.get(i).getItem());
							}
						}
					}
					else
					{
						inv.setItem(i, guiItems.get(i).getItem());
					}
				}
				else
				{
					inv.setItem(i, guiItems.get(i).getItem());
				}
			}
			else
			{
				inv.setItem(i, guiItems.get(i).getItem());
			}
		}
		return inv;
	}

	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args)  
	{
		if (cmd.getName().equalsIgnoreCase("clock")) 
		{
			if (args.length==0)
			{
				sender.sendMessage("��a=======[�ӱ�˵�]=======");
				sender.sendMessage("��a/clock gui   ��6�򿪽���");
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
						
						int guiNumber = Integer.valueOf(args[1]);
						int position = Integer.valueOf(args[2]);
						if(list.containsKey(guiNumber))
						{
							if(list.get(guiNumber).containsKey(position))
							{
								sender.sendMessage("��a[�ӱ�˵�] ��6׼��ɾ��...");
								File file=new File(getDataFolder(),"/Data/player.yml");
								FileConfiguration config;
								config = load(file);
								
								for(String playerName:config.getKeys(false))
								{
									config.set(playerName+"."+guiNumber+"."+position, null);
								}
								
								for(String playerName:playerData.keySet())
								{
									playerData.get(playerName).setNumber(guiNumber, position, 0);
								}
								
								try {
									config.save(file);
								} catch (IOException e) {
									e.printStackTrace();
								}
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
			
			if (args[0].equalsIgnoreCase("gui"))
			{
				if (sender instanceof Player)
				{
					Player p = (Player)sender;
					Inventory inv = initInventory(p, guiNameList.get(0), list.get(0), 0);
					p.openInventory(inv);
				}
				
			}
			
			if (args[0].equalsIgnoreCase("give"))
			{
				if(sender.isOp())
				{
					if (sender instanceof Player)
					{
						Player p = (Player)sender;
						p.getInventory().addItem(clock);
					}
				}
				else
				{
					sender.sendMessage("��a[�ӱ�˵�] ��cû��Ȩ�ޣ�");
				}
			}
			
			if (args[0].equalsIgnoreCase("open"))
			{
				if(sender.isOp())
				{
					if(args.length==2)
					{
						if (sender instanceof Player)
						{
							Player p = (Player)sender;
							if(args[1].matches("[0-9]*"))
							{
								if(Integer.valueOf(args[1])<=list.size()-1 && Integer.valueOf(args[1])>=0)
								{
									Inventory inv = initInventory(p, guiNameList.get(Integer.valueOf(args[1])),
											list.get(Integer.valueOf(args[1])), Integer.valueOf(args[1]));
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
				else
				{
					sender.sendMessage("��a[�ӱ�˵�] ��cû��Ȩ�ޣ�");
				}
				
			}
			
			if (args[0].equalsIgnoreCase("reload"))
			{
				if(sender.isOp())
				{
					loadConfig();
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

