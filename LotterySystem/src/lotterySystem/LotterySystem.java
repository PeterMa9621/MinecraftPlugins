package lotterySystem;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class LotterySystem extends JavaPlugin implements Listener
{
	public ItemStack egg;
	ArrayList<ItemStack> items = new ArrayList<ItemStack>();
	ArrayList<String> mainLore = new ArrayList<String>();
	public int time=1;
	public int cd1=0;
	public int cd2=10;
	private int type = 0;
	private int amount = 0;
	private String name = null;
	private int enchantment = 0;
	private int enchantment_level = 0;
	private int probability = 0;
	private int mainType = 0;
	private String mainName = null;
	
	java.util.Random random=new java.util.Random();
	
	public void onEnable() 
	{
		if(!getDataFolder().exists()) 
		{
			getDataFolder().mkdir();
		}
		loadConfig();
		getServer().getPluginManager().registerEvents(new EventListen(this), this);
		Bukkit.getConsoleSender().sendMessage("§a[LotterySystem] §e抽奖系统加载完毕");
	}
	
	public void init(int TypeID, String name, ArrayList<String> lore)
	{
		egg = createNewItem(TypeID, name, lore);
	}
	
	public static ItemStack createNewItem(int itemID,String name, ArrayList<String> lore)
	{
		ItemStack item=new ItemStack(itemID);
		ItemMeta a = item.getItemMeta();
		a.setDisplayName(name);
		a.setLore(lore);
		item.setItemMeta(a);
		return item;
	}
	public void loadConfig() 
	{
		File file=new File(getDataFolder(),"config.yml");
		FileConfiguration config;
		if (!file.exists())
		{
			config = load(file);
			config.set("Items.Main.TypeID", 388);
			config.set("Items.Main.Name", "§2神圣蛋");
			config.set("Items.Main.Lore", "§3这是一个神圣的蛋，看似可以打开%§4右键打开它");
			config.set("Items.1.TypeID", 347);
			config.set("Items.1.Amount", 1);
			config.set("Items.1.Name", "§cGODLIKE_EGG");
			config.set("Items.1.Lore", "§aTest1%§bTest2");
			config.set("Items.1.Enchantment.ID", 35);
			config.set("Items.1.Enchantment.Level", 2);
			config.set("Items.1.Probability", 20);
			config.set("Items.2.TypeID", 264);
			config.set("Items.2.Amount", 10);
			config.set("Items.2.Lore", "§eTest1");
			config.set("Items.2.Enchantment.ID", 34);
			config.set("Items.2.Enchantment.Level", 1);
			config.set("Items.2.Probability", 10);
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
		
		// initialize the main item which is used to get gifts
		mainType = config.getInt("Items.Main.TypeID");
		mainName = config.getString("Items.Main.Name");
		for(String l:config.getString("Items.Main.Lore").split("%"))
		{
			mainLore.add(l);
		}
		init(mainType, mainName, mainLore);
		
		egg.addUnsafeEnchantment(Enchantment.getById(35), 1);
		
		// initialize every item, and put them into the arraylist items
		for(int i=0; config.contains("Items."+(i+1)); i++)
		{

			ArrayList<String> lore = new ArrayList<String>();
			type = config.getInt("Items."+(i+1)+".TypeID");
			amount = config.getInt("Items."+(i+1)+".Amount");
			name = config.getString("Items."+(i+1)+".Name");
			probability = config.getInt("Items."+(i+1)+".Probability");

			if(config.getString("Items."+(i+1)+".Lore")!=null)
			{
				for(String l:config.getString("Items."+(i+1)+".Lore").split("%"))
				{
					lore.add(l);
				}
			}

			enchantment = config.getInt("Items."+(i+1)+".Enchantment.ID");
			enchantment_level = config.getInt("Items."+(i+1)+".Enchantment.Level");
			
			ItemStack item = new ItemStack(type);
			item.setAmount(amount);
			ItemMeta a = item.getItemMeta();
			if(name!=null)
			{
				a.setDisplayName(name);
			}
			if(!lore.isEmpty())
			{
				a.setLore(lore);
			}
			item.setItemMeta(a);
			if(enchantment>=0 && enchantment_level>0)
			{
				item.addUnsafeEnchantment(Enchantment.getById(enchantment), enchantment_level);
			}
			int x = 0;

			while(x<probability)
			{
				int index = 0;
				if(items.size()>1)
					index = random.nextInt(items.size()-1);
				items.add(index, item);
				x+=1;
			}

		}
		return;
	}

	public void onDisable() 
	{
		Bukkit.getConsoleSender().sendMessage("§a[LotterySystem] §e抽奖系统卸载完毕");
	}

	public FileConfiguration load(File file)
	{
        if (!(file.exists())) 
        { //假如文件不存在
        	try   //捕捉异常，因为有可能创建不成功
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
	
	public int lottery(ArrayList<ItemStack> list)
	{
		int length = list.size();
		
		int result=random.nextInt(length);
		return result;
	}
	
	// 留着做抽奖蛋交易商店
	public void gui(Player player)
	{
		Inventory inv = Bukkit.createInventory(player, 36, "抽奖");
		for(int i=0; i<18; i++)
		{
			ItemStack item = new ItemStack(160);
			if(i<9)
			{
				inv.setItem(i, item);
			}
		}
		ItemStack item = new ItemStack(388);
		ItemMeta a = item.getItemMeta();
		a.setDisplayName("点击打开这颗蛋");
		item.setItemMeta(a);
		//item.addEnchantment(Enchantment.LUCK, 10);
		inv.setItem(31, item);
		player.openInventory(inv);
		
	}
	
	
	/*
	ItemStack item = new ItemStack(388);
	@EventHandler
	public void onPlayerClickInventory(InventoryClickEvent event)
	{
		
		if (event.getWhoClicked() instanceof Player == true)
		{ 
			Player p = (Player) event.getWhoClicked();
			if (event.getInventory().getTitle().equalsIgnoreCase("抽奖"))
			{
				event.setCancelled(true);
				if (event.getRawSlot()==31)
				{
					ItemMeta a = item.getItemMeta();
					a.setDisplayName("");
					item.setItemMeta(a);
					event.getInventory().setItem(9,item);
					cd1 = 0;
					if(guiTask(event))
					{
						int result = lottery(items);
						p.getInventory().addItem(items.get(result));
						p.sendMessage("你得到了一个"+items.get(result).getItemMeta().getDisplayName());
					}
				}
				
			}

		}
	}
	
	private boolean guiTask(InventoryClickEvent event) 
	{
		Bukkit.getScheduler().runTaskTimer((Plugin)this, new Runnable()
		{
			public void run()
			{
				java.util.Random random=new java.util.Random();
				int result=random.nextInt(30)+40;
				event.getInventory().removeItem(item);
				event.getInventory().setItem(9+(cd1%9), item);
				if(cd1<result)
				{
					cd1+=1;
				}
				else
				{
					event.getWhoClicked().closeInventory();
				}
			}
		}, cd1*20, 1*(1+cd1));
		return true;
	}
	*/
	
	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args)  
	{
		if (cmd.getName().equalsIgnoreCase("cj"))
		{
			if (args.length==0)
			{
				if(sender.isOp())
				{
					sender.sendMessage("§a=========§6[抽奖系统]§a=========");
					sender.sendMessage("§6/cj gui §a打开抽奖界面");
					sender.sendMessage("§6/cj start §a直接从礼物里抽奖（无需消耗品）");
					sender.sendMessage("§6/cj give §a给予一个抽奖蛋");
					sender.sendMessage("§6/cj reload §a重载插件");
				}
				return true;
			}
			
			if (args[0].equalsIgnoreCase("start"))
			{
				if(sender instanceof Player)
				{
					if(sender.isOp())
					{
						int result = lottery(items);
						((Player) sender).getInventory().addItem(items.get(result));
					}
				}
				else
				{
					sender.sendMessage("控制台不可用");
				}
				return true;
				
			}
			
			if (args[0].equalsIgnoreCase("gui"))
			{
				if(sender instanceof Player)
				{
					if(sender.isOp())
					{
						Player p = (Player)sender;
						gui(p);
					}
					else
					{
						sender.sendMessage("§c你没有权限");
					}
				}
				return true;
			}
			
			if (args[0].equalsIgnoreCase("give"))
			{
				if(sender instanceof Player)
				{
					if(sender.isOp())
					{
						Player p = (Player)sender;
						p.getInventory().addItem(egg);
					}
				}
				return true;
			}
			
			if (args[0].equalsIgnoreCase("reload"))
			{
				if(sender instanceof Player)
				{
					if(sender.isOp())
					{
						mainLore.clear();
						items.clear();
						loadConfig();
						sender.sendMessage("§6重载成功");
					}
				}
				return true;
			}
			return true;
		}
		
		return false;
	}
	
	
}

