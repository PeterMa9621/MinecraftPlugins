package betterWeapon;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import betterWeapon.Gem.EquipmentInlayListener;
import betterWeapon.Gem.GemEvaluateListener;
import betterWeapon.Gem.GemSynthesisListener;
import net.milkbowl.vault.economy.Economy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BetterWeapon extends JavaPlugin
{
	ItemStack itemForIntensify = null;
	ItemStack itemForSmelt = null;
	ArrayList<ItemStack> assistantsList = new ArrayList<ItemStack>();
	ArrayList<Integer> possibilityList = new ArrayList<Integer>();
	HashMap<Integer, Integer> rule = new HashMap<Integer, Integer>();
	int priceForSmelt = 0;
	HashMap<Integer, String> ruleSmelt = new HashMap<Integer, String>();
	ArrayList<Integer> possibilitySmeltList = new ArrayList<Integer>();
	
	/**
	 *  The keys are "Equipment", "HolePossibility", "EvaPossibility", "ItemPossibility", 
	 *  "SynthesisPossibility" and "InlayWeapon".
	 */
	public HashMap<String, ArrayList<Integer>> gem = new HashMap<String, ArrayList<Integer>>();
	public ArrayList<String> gemType = new ArrayList<String>();
	public int priceForHole = 0;
	public int priceForInlay = 0;
	public int priceForEvaluate = 0;
	public int priceForSynthesis = 0;
	
	ArrayList<String> inlayWeapones = new ArrayList<String>();
	
	public boolean notify = false;
	
	public boolean isEco = false;
	
	BetterWeaponGemGui gemGui = new BetterWeaponGemGui(this);
	
	public ItemStack gemstone = this.createItem(263, 1, 1, "§f未鉴定的宝石", "§e[未鉴定]%§6一块看起来普通的石头");
	
	Random rand = new Random();
	
	public Economy economy;
	
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
		if(!getDataFolder().exists()) 
		{
			getDataFolder().mkdir();
		}
		loadConfig();
		getServer().getPluginManager().registerEvents(new BetterWeaponListener(this), this);
		getServer().getPluginManager().registerEvents(new BetterWeaponEntityListener(this), this);
		getServer().getPluginManager().registerEvents(new BetterWeaponGemListener(this), this);
		getServer().getPluginManager().registerEvents(new GemEvaluateListener(this), this);
		getServer().getPluginManager().registerEvents(new EquipmentInlayListener(this), this);
		getServer().getPluginManager().registerEvents(new GemSynthesisListener(this), this);
		Bukkit.getConsoleSender().sendMessage("§a[BetterWeapon] §e强化系统加载完毕");
		//getLogger().info("Finish loading");
	}

	public void onDisable() 
	{
		Bukkit.getConsoleSender().sendMessage("§a[BetterWeapon] §e强化系统卸载完毕");
		//getLogger().info("Unload");
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
	
	public void init()
	{
		assistantsList.clear();
		possibilityList.clear();
		possibilitySmeltList.clear();
	}
	
	public Inventory initMainGUI(Player player)
	{
		ItemStack enchant = createItem(403, 1, 0, "§5点击打开附魔强化界面");
		//--------------------------------------------
		ItemStack attribute = createItem(384, 1, 0, "§e点击打开熔炼强化界面");
		//--------------------------------------------
		ItemStack gem = createItem(264, 1, 0, "§e点击打开宝石系统界面");
		gem.addUnsafeEnchantment(Enchantment.LUCK, 1);
		
		Inventory inv = Bukkit.createInventory(player, 9, "§5强化系统");
		inv.setItem(2, enchant);
		inv.setItem(4, gem);
		inv.setItem(6, attribute);
		
		return inv;
	}
	
	public Inventory initSmeltGUI(Player player)
	{
		ItemStack windows = createItem(160, 1, 4, " ");
		//--------------------------------------------
		ItemStack equipment = createItem(160, 1, 1, "§3上方请放入要熔炼的装备");
		//--------------------------------------------
		ItemStack intensify = createItem(160, 1, 5, "§3上方请放入熔炼石");
		//--------------------------------------------
		ItemStack start = createItem(388, 1, 0, "§5点击开始熔炼", "§e需要花费§c"+Integer.valueOf(priceForSmelt)+"§e金币");
		//--------------------------------------------
		ItemStack back = createItem(331, 1, 0, "§e点击返回主界面");
		
		Inventory inv = Bukkit.createInventory(player, 45, "§5熔炼系统");
		for(int i=0; i<45; i++)
		{
			inv.setItem(i, windows);
		}
		inv.setItem(10, null);
		inv.setItem(16, null);
		inv.setItem(19, equipment);
		inv.setItem(25, intensify);
		inv.setItem(31, start);
		inv.setItem(40, null);
		inv.setItem(44, back);
		return inv;
	}
	
	public Inventory initIntensifyGUI(Player player)
	{
		ItemStack windows = createItem(160, 1, 0, " ");
		//--------------------------------------------
		ItemStack equipment = createItem(160, 1, 1, "§3上方请放入要强化的装备");
		//--------------------------------------------
		ItemStack intensify = createItem(160, 1, 4, "§3上方请放入强化石");
		//--------------------------------------------
		ItemStack assistant = createItem(160, 1, 5, "§3上方请放入辅助物品(可以不放)");
		//--------------------------------------------
		ItemStack start = createItem(388, 1, 0, "§5点击开始强化");
		//--------------------------------------------
		ItemStack back = createItem(331, 1, 0, "§e点击返回主界面");
		
		Inventory inv = Bukkit.createInventory(player, 45, "§5附魔强化系统");
		for(int i=0; i<10; i++)
		{
			inv.setItem(i, windows);
		}
		for(int i=17; i<45; i++)
		{
			inv.setItem(i, windows);
		}
		for(int i=0; i<4; i+=3)
		{
			inv.setItem(11+i, windows);
			inv.setItem(12+i, windows);
		}
		inv.setItem(19, equipment);
		inv.setItem(22, intensify);
		inv.setItem(25, assistant);
		inv.setItem(31, start);
		inv.setItem(40, null);
		inv.setItem(44, back);
		return inv;
	}
	
	public void loadConfig()
	{
		File file=new File(getDataFolder(),"config.yml");
		FileConfiguration config;
		if (!file.exists())
		{
			config = load(file);
			config.set("Item.ItemID", 388);
			config.set("Item.Name", "§a强化石");
			config.set("Item.Lore", "§3这是一块不同寻常的石头%§2[使用方法]§a打开强化系统%§a强化装备");
			
			config.set("Assistants.1.ItemID", 264);
			config.set("Assistants.1.Name", "§b金刚石");
			config.set("Assistants.1.Lore", "§3这是一种十分坚硬的石头%§2[用途]§a用于辅助强化装备%§a在装备强化失败时装备%§a不会碎掉");
			
			config.set("Assistants.2.ItemID", 322);
			config.set("Assistants.2.Name", "§b幸运果");
			config.set("Assistants.2.Lore", "§3幸运果实%§2[用途]§a用于辅助强化装备%§a在下一次强化装备时%§a一定成功");

			config.set("Equipment", "276:16,278:35");
			config.set("Possibility", "100,80,70,60,40,15,8,5,3,1");
			
			config.set("Smelt.Item.ItemID", 318);
			config.set("Smelt.Item.Name", "§a熔炼石");
			config.set("Smelt.Item.Lore", "§3一种稀有的石头%§2[使用方法]§a打开熔炼系统%§a熔炼装备");
			
			config.set("Smelt.Equipment", "276:attack,311:defend");
			config.set("Smelt.Possibility", "70,60,40,20,15,8,5,4,2,1");
			config.set("Smelt.Price", 10000);
			
			config.set("Gem.Hole.Equipment", "276,310,311,312,313");
			config.set("Gem.Hole.Possibility", "50,10,5");
			config.set("Gem.Hole.Price", 5000);
			
			config.set("Gem.Inlay.Price", 5000);
			config.set("Gem.Inlay.Weapon", "267,268,272,276,283");
			
			config.set("Gem.Evaluate.Possibility", "100,40,20,5,3,1");
			config.set("Gem.Evaluate.Price", 1000);

			config.set("Gem.Item.Type", "attack,defend,block,crit,penetrate");
			config.set("Gem.Item.Possibility", "100,100,50,50,20");
			
			config.set("Gem.Synthesis.Price", 100);
			config.set("Gem.Synthesis.Possibility", "70,50,20,10,3");
			
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
		init();
		int itemID = 0;
		String itemName = null;
		String itemLore = null;
		ArrayList<String> lore = new ArrayList<String>();
		
		//------------------------------------------------
		itemID = config.getInt("Item.ItemID");
		itemName =  config.getString("Item.Name");
		itemLore = config.getString("Item.Lore");
		
		itemForIntensify = new ItemStack(itemID);
		ItemMeta itemMeta = itemForIntensify.getItemMeta();
		itemMeta.setDisplayName(itemName);
		
		for(String l:itemLore.split("%"))
		{
			lore.add(l);
		}
		
		itemMeta.setLore(lore);
		itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		itemForIntensify.setItemMeta(itemMeta);
		itemForIntensify.addUnsafeEnchantment(Enchantment.LUCK, 1);
		
		//------------------------------------------------
		lore.clear();
		itemID = config.getInt("Smelt.Item.ItemID");
		itemName =  config.getString("Smelt.Item.Name");
		itemLore = config.getString("Smelt.Item.Lore");
		
		itemForSmelt = new ItemStack(itemID);
		itemMeta = itemForIntensify.getItemMeta();
		itemMeta.setDisplayName(itemName);
		
		for(String l:itemLore.split("%"))
		{
			lore.add(l);
		}
		
		itemMeta.setLore(lore);
		itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		itemForSmelt.setItemMeta(itemMeta);
		itemForSmelt.addUnsafeEnchantment(Enchantment.LUCK, 1);
		
		//------------------------------------------------
		
		int assistantID = 0;
		String assistantName = null;
		String assistantLore = null;

		for(int i=0; config.contains("Assistants."+(i+1)); i++)
		{
			lore.clear();
			assistantID = config.getInt("Assistants."+(i+1)+".ItemID");
			assistantName = config.getString("Assistants."+(i+1)+".Name");
			assistantLore = config.getString("Assistants."+(i+1)+".Lore");
			for(String l:assistantLore.split("%"))
			{
				lore.add(l);
			}
			ItemStack assistant = new ItemStack(assistantID);
			ItemMeta assistantMeta = assistant.getItemMeta();
			assistantMeta.setDisplayName(assistantName);
			assistantMeta.setLore(lore);
			assistantMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			assistant.setItemMeta(assistantMeta);
			assistant.addUnsafeEnchantment(Enchantment.LUCK, 1);

			assistantsList.add(assistant);    //第一个是保护装备不碎的，第二个是百分百强化的
		}
		
		//------------------------------------------------
		
		int EquipID = 0;
		int EnchantID = 0;
		
		for(String e:config.getString("Equipment").split(","))
		{
			EquipID = Integer.valueOf(e.split(":")[0]);
			EnchantID = Integer.valueOf(e.split(":")[1]);
			rule.put(EquipID, EnchantID);
		}
		
		//------------------------------------------------
		
		String possibility = config.getString("Possibility");
		for(String p:possibility.split(","))
		{
			possibilityList.add(Integer.valueOf(p));
		}
		
		//------------------------------------------------
		
		priceForSmelt = config.getInt("Smelt.Price");
		String equipmentForSmelt = config.getString("Smelt.Equipment");
		
		for(String i:equipmentForSmelt.split(","))
		{
			ruleSmelt.put(Integer.valueOf(i.split(":")[0]), i.split(":")[1]);
		}
		
		//------------------------------------------------
		
		String possibilityForSmelt = config.getString("Smelt.Possibility");
		
		for(String p:possibilityForSmelt.split(","))
		{
			possibilitySmeltList.add(Integer.valueOf(p));
		}
		
		//------------------------------------------------
		
		priceForEvaluate = config.getInt("Gem.Evaluate.Price");
		priceForInlay = config.getInt("Gem.Inlay.Price");
		priceForHole = config.getInt("Gem.Hole.Price");
		priceForSynthesis = config.getInt("Gem.Synthesis.Price");
		String gemEquip = config.getString("Gem.Hole.Equipment");
		String gemHolePossibility = config.getString("Gem.Hole.Possibility");
		String inlayWeapon = config.getString("Gem.Inlay.Weapon");
		String gemEvaPossibility = config.getString("Gem.Evaluate.Possibility");
		String gemItemPossibility = config.getString("Gem.Item.Possibility");
		String gemSynthesisPossibility = config.getString("Gem.Synthesis.Possibility");
		String gemType = config.getString("Gem.Item.Type");
		// =============================================
		ArrayList<Integer> list = new ArrayList<Integer>();
		for(String equip:gemEquip.split(","))
		{
			list.add(Integer.valueOf(equip));
		}
		gem.put("Equipment", list);
		// =============================================
		list = new ArrayList<Integer>();
		for(String p:gemHolePossibility.split(","))
		{
			list.add(Integer.valueOf(p));
		}
		gem.put("HolePossibility", list);
		// =============================================
		list = new ArrayList<Integer>();
		for(String w:inlayWeapon.split(","))
		{
			list.add(Integer.valueOf(w));
		}
		gem.put("InlayWeapon", list);
		// =============================================
		list = new ArrayList<Integer>();
		for(String p:gemEvaPossibility.split(","))
		{
			list.add(Integer.valueOf(p));
		}
		gem.put("EvaPossibility", list);
		// =============================================
		list = new ArrayList<Integer>();
		for(String p:gemItemPossibility.split(","))
		{
			list.add(Integer.valueOf(p));
		}
		gem.put("ItemPossibility", list);
		// =============================================
		list = new ArrayList<Integer>();
		for(String p:gemSynthesisPossibility.split(","))
		{
			list.add(Integer.valueOf(p));
		}
		gem.put("SynthesisPossibility", list);
		//=================================================

		for(String p:gemType.split(","))
		{
			this.gemType.add(p);
		}

		return;
	}
	
	public boolean isExist(int number, int[] numberList)
	{
		for(int i=0; i<numberList.length; i++)
		{
			if(number==numberList[i])
			{
				return true;
			}
		}
		return false;
	}
	
	public int random(int range)
	{
		int i = rand.nextInt(range); //生成随机数
		return i;
	}
	
	public ItemStack createItem(Material material, String displayName)
	{
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(displayName);
		item.setItemMeta(meta);
		
		return item;
	}
	
	public ItemStack createItem(Material material, String displayName, ArrayList<String> lore)
	{
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(displayName);
		
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		return item;
	}
	
	public ItemStack createItem(Material material, String displayName, String lore)
	{
		ItemStack item = new ItemStack(material);
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
	
	public ItemStack createItem(int ID, int quantity, int durability, String displayName)
	{
		ItemStack item = new ItemStack(ID, quantity, (short)durability);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(displayName);
		item.setItemMeta(meta);
		
		return item;
	}
	
	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args)  
	{
		if (cmd.getName().equalsIgnoreCase("qh"))
		{
			
			if (args.length==0)
			{
				sender.sendMessage("§a=========[§6强化系统§a]=========");
				sender.sendMessage("§6/qh gui §a- 打开强化系统界面");
				if(sender.isOp())
				{
					sender.sendMessage("§6/qh §a- reload 重载插件");
					sender.sendMessage("§6/qh §a- take1 获取一个强化石");
					sender.sendMessage("§6/qh §a- take2 获取一个熔炼石");
					sender.sendMessage("§6/qh §a- get [辅助物品编号]");	
					sender.sendMessage("§6/qh §a- give 给予一个未鉴定的宝石");
					sender.sendMessage("§6/qh §a- test <0|1|2> 给予最大化属性到手上的装备");
					sender.sendMessage("§6/qh §a- notify 开启伤害显示");
				}
				
				return true;
			}
			
			if (args[0].equalsIgnoreCase("notify"))
			{
				if(sender.isOp())
				{
					if(sender instanceof Player)
					{
						if(notify==false)
						{
							notify=true;
							sender.sendMessage("§6已开启");
						}
						else
						{
							notify=false;
							sender.sendMessage("§6已关闭");
						}
						
					}
				}
			}
			
			if (args[0].equalsIgnoreCase("test"))
			{
				if(sender.isOp())
				{
					if(sender instanceof Player)
					{
						Player p = (Player)sender;
						ItemStack item = p.getItemInHand();
						ItemMeta meta = item.getItemMeta();
						List<String> lore = null;
						if(meta.hasLore())
						{
							lore = meta.getLore();
						}
						
						if(args[1].equalsIgnoreCase("0"))
						{
							lore.add(0, "§a剩余开孔:§c0");
							lore.add(0, "§2镶嵌:§c暴击(百分比)+18.0");
							lore.add(0, "§2镶嵌:§c攻击+9.0");
							lore.add(0, "§2镶嵌:§c穿刺+9.0");
							lore.add(0, "§e[已开孔]");
							lore.add(0, "§a熔炼属性:§c攻击+10");
							lore.add(0, "§e[熔炼]");
						}
						else if(args[1].equalsIgnoreCase("1"))
						{
							lore.add(0, "§a剩余开孔:§c0");
							lore.add(0, "§2镶嵌:§c暴击(百分比)+18.0");
							lore.add(0, "§2镶嵌:§c攻击+9.0");
							lore.add(0, "§e[已开孔]");
							lore.add(0, "§a熔炼属性:§c攻击+10");
							lore.add(0, "§e[熔炼]");
						}
						else if(args[1].equalsIgnoreCase("2"))
						{
							lore.add(0, "§a剩余开孔:§c0");
							lore.add(0, "§2镶嵌:§c格挡(百分比)+18.0");
							lore.add(0, "§2镶嵌:§c防御+9.0");
							lore.add(0, "§e[已开孔]");
							lore.add(0, "§a熔炼属性:§c防御+5.0");
							lore.add(0, "§e[熔炼]");
						}
						
						
						meta.setLore(lore);
						item.setItemMeta(meta);
						p.setItemInHand(item);
						p.sendMessage("§6[宝石系统] §a已强化");
					}
				}
				else
				{
					sender.sendMessage("§6[强化系统] §c你没有权限！");
				}
				
			}
			
			if (args[0].equalsIgnoreCase("give"))
			{
				if(sender.isOp())
				{
					if(sender instanceof Player)
					{
						
						Player p = (Player)sender;
						p.getInventory().addItem(gemstone);
						p.sendMessage("§6[宝石系统] §a已获得未鉴定宝石");
					}
				}
				else
				{
					sender.sendMessage("§6[强化系统] §c你没有权限！");
				}
				
			}
			
			if (args[0].equalsIgnoreCase("reload"))
			{
				if(sender.isOp())
				{
					loadConfig();
					sender.sendMessage("§6[强化系统] §a主配置重载完毕");
				}
				else
				{
					sender.sendMessage("§6[强化系统] §c你没有权限！");
				}
				
			}
			
			if (args[0].equalsIgnoreCase("gui"))
			{
				if(sender instanceof Player)
				{
					Player p = (Player)sender;
					Inventory inv = initMainGUI(p);
					p.openInventory(inv);
				}
			}
			
			if (args[0].equalsIgnoreCase("take1"))
			{
				if(sender.isOp())
				{
					if(sender instanceof Player)
					{
						((Player)sender).getInventory().addItem(itemForIntensify);
					}
					else
					{
						sender.sendMessage("§6[强化系统] §c控制台无法使用此命令！");
					}
				}
				else
				{
					sender.sendMessage("§6[强化系统] §c你没有权限！");
				}
				
			}
			
			if (args[0].equalsIgnoreCase("take2"))
			{
				if(sender.isOp())
				{
					if(sender instanceof Player)
					{
						((Player)sender).getInventory().addItem(itemForSmelt);
					}
					else
					{
						sender.sendMessage("§6[强化系统] §c控制台无法使用此命令！");
					}
				}
				else
				{
					sender.sendMessage("§6[强化系统] §c你没有权限！");
				}
				
			}
			
			if (args[0].equalsIgnoreCase("get"))
			{
				if(sender.isOp())
				{
					if(sender instanceof Player)
					{
						if(args.length==2)
						{
							if(args[1].matches("[0-9]*"))
							{
								if(Integer.valueOf(args[1])<=assistantsList.size() && Integer.valueOf(args[1])>0)
								{
									((Player)sender).getInventory().addItem(assistantsList.get(Integer.valueOf(args[1])-1));
								}
								else
								{
									sender.sendMessage("§6[强化系统] §c没有找到该物品");
								}
							}
							else
							{
								sender.sendMessage("§6[强化系统] §c物品编号只能为数字");
							}
						}
						else
						{
							sender.sendMessage("§6[强化系统] §a/qh get [辅助物品编号]");
						}
						
					}
					else
					{
						sender.sendMessage("§6[强化系统] §c控制台无法使用此命令！");
					}
				}
				else
				{
					sender.sendMessage("§6[强化系统] §c你没有权限！");
				}
				
			}
			return true;
		}

		return false;
		
	}
	
	
}

