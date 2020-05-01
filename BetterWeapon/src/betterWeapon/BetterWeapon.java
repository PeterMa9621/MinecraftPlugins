package betterWeapon;

import betterWeapon.Gem.EquipmentInlayListener;
import betterWeapon.Gem.GemEvaluateListener;
import betterWeapon.Gem.GemSynthesisListener;
import betterWeapon.listener.BetterWeaponEntityListener;
import betterWeapon.listener.BetterWeaponGemListener;
import betterWeapon.listener.BetterWeaponListener;
import betterWeapon.manager.*;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.SplittableRandom;

public class BetterWeapon extends JavaPlugin
{
	public boolean notify = false;
	
	public boolean isEco = false;
	
	public GemGuiManager gemGui;

	SplittableRandom rand = new SplittableRandom();
	
	public Economy economy;

	public SmeltManager smeltManager;
	public IntensifyManager intensifyManager;
	public AssistantManager assistantManager;
	public GemManager gemManager;
	public ConfigManager configManager;
	public GuiManager guiManager;

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

		initManager();
		configManager.loadConfig();
		gemGui = new GemGuiManager(this);;
		getServer().getPluginManager().registerEvents(new BetterWeaponListener(this), this);
		getServer().getPluginManager().registerEvents(new BetterWeaponEntityListener(this), this);
		getServer().getPluginManager().registerEvents(new BetterWeaponGemListener(this), this);
		getServer().getPluginManager().registerEvents(new GemEvaluateListener(this), this);
		getServer().getPluginManager().registerEvents(new EquipmentInlayListener(this), this);
		getServer().getPluginManager().registerEvents(new GemSynthesisListener(this), this);
		Bukkit.getConsoleSender().sendMessage("§a[BetterWeapon] §e强化系统加载完毕");
		//getLogger().info("Finish loading");
	}

	public void initManager() {
		smeltManager = new SmeltManager(this);
		intensifyManager = new IntensifyManager(this);
		assistantManager = new AssistantManager(this);
		gemManager = new GemManager(this);
		configManager = new ConfigManager(this);
		guiManager = new GuiManager(this);
	}

	public void onDisable() 
	{
		Bukkit.getConsoleSender().sendMessage("§a[BetterWeapon] §e强化系统卸载完毕");
		//getLogger().info("Unload");
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
						ItemStack item = p.getInventory().getItemInMainHand();
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
						p.getInventory().setItemInMainHand(item);
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
						p.getInventory().addItem(gemManager.gemstone);
						p.sendMessage("§6[宝石系统] §a已获得未鉴定宝石");
					}
				}
				else
				{
					sender.sendMessage("§6[强化系统] §c你没有权限！");
				}
				
			}
			
			if (args[0].equalsIgnoreCase("reload")) {
				if(sender.isOp()) {
					configManager.loadConfig();
					sender.sendMessage("§6[强化系统] §a主配置重载完毕");
				}
				else {
					sender.sendMessage("§6[强化系统] §c你没有权限！");
				}
				
			}
			
			if (args[0].equalsIgnoreCase("gui")) {
				if(sender instanceof Player) {
					Player p = (Player)sender;
					Inventory inv = guiManager.initMainGUI(p);
					p.openInventory(inv);
				}
			}
			
			if (args[0].equalsIgnoreCase("take1")) {
				if(sender.isOp()) {
					if(sender instanceof Player) {
						((Player)sender).getInventory().addItem(intensifyManager.getItem());
					}
					else {
						sender.sendMessage("§6[强化系统] §c控制台无法使用此命令！");
					}
				}
				else {
					sender.sendMessage("§6[强化系统] §c你没有权限！");
				}
			}
			
			if (args[0].equalsIgnoreCase("take2")) {
				if(sender.isOp()) {
					if(sender instanceof Player) {
						((Player)sender).getInventory().addItem(smeltManager.getItem());
					} else {
						sender.sendMessage("§6[强化系统] §c控制台无法使用此命令！");
					}
				} else {
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
								ArrayList<ItemStack> assistantsList = assistantManager.getAssistantsList();
								if(Integer.parseInt(args[1])<=assistantsList.size() && Integer.parseInt(args[1])>0)
								{
									((Player)sender).getInventory().addItem(assistantsList.get(Integer.parseInt(args[1])-1));
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

