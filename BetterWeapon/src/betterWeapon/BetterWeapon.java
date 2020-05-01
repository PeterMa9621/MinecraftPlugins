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
		Bukkit.getConsoleSender().sendMessage("��a[BetterWeapon] ��eǿ��ϵͳ�������");
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
		Bukkit.getConsoleSender().sendMessage("��a[BetterWeapon] ��eǿ��ϵͳж�����");
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
		int i = rand.nextInt(range); //���������
		return i;
	}
	
	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args)  
	{
		if (cmd.getName().equalsIgnoreCase("qh"))
		{
			
			if (args.length==0)
			{
				sender.sendMessage("��a=========[��6ǿ��ϵͳ��a]=========");
				sender.sendMessage("��6/qh gui ��a- ��ǿ��ϵͳ����");
				if(sender.isOp())
				{
					sender.sendMessage("��6/qh ��a- reload ���ز��");
					sender.sendMessage("��6/qh ��a- take1 ��ȡһ��ǿ��ʯ");
					sender.sendMessage("��6/qh ��a- take2 ��ȡһ������ʯ");
					sender.sendMessage("��6/qh ��a- get [������Ʒ���]");	
					sender.sendMessage("��6/qh ��a- give ����һ��δ�����ı�ʯ");
					sender.sendMessage("��6/qh ��a- test <0|1|2> ����������Ե����ϵ�װ��");
					sender.sendMessage("��6/qh ��a- notify �����˺���ʾ");
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
							sender.sendMessage("��6�ѿ���");
						}
						else
						{
							notify=false;
							sender.sendMessage("��6�ѹر�");
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
							lore.add(0, "��aʣ�࿪��:��c0");
							lore.add(0, "��2��Ƕ:��c����(�ٷֱ�)+18.0");
							lore.add(0, "��2��Ƕ:��c����+9.0");
							lore.add(0, "��2��Ƕ:��c����+9.0");
							lore.add(0, "��e[�ѿ���]");
							lore.add(0, "��a��������:��c����+10");
							lore.add(0, "��e[����]");
						}
						else if(args[1].equalsIgnoreCase("1"))
						{
							lore.add(0, "��aʣ�࿪��:��c0");
							lore.add(0, "��2��Ƕ:��c����(�ٷֱ�)+18.0");
							lore.add(0, "��2��Ƕ:��c����+9.0");
							lore.add(0, "��e[�ѿ���]");
							lore.add(0, "��a��������:��c����+10");
							lore.add(0, "��e[����]");
						}
						else if(args[1].equalsIgnoreCase("2"))
						{
							lore.add(0, "��aʣ�࿪��:��c0");
							lore.add(0, "��2��Ƕ:��c��(�ٷֱ�)+18.0");
							lore.add(0, "��2��Ƕ:��c����+9.0");
							lore.add(0, "��e[�ѿ���]");
							lore.add(0, "��a��������:��c����+5.0");
							lore.add(0, "��e[����]");
						}
						
						
						meta.setLore(lore);
						item.setItemMeta(meta);
						p.getInventory().setItemInMainHand(item);
						p.sendMessage("��6[��ʯϵͳ] ��a��ǿ��");
					}
				}
				else
				{
					sender.sendMessage("��6[ǿ��ϵͳ] ��c��û��Ȩ�ޣ�");
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
						p.sendMessage("��6[��ʯϵͳ] ��a�ѻ��δ������ʯ");
					}
				}
				else
				{
					sender.sendMessage("��6[ǿ��ϵͳ] ��c��û��Ȩ�ޣ�");
				}
				
			}
			
			if (args[0].equalsIgnoreCase("reload")) {
				if(sender.isOp()) {
					configManager.loadConfig();
					sender.sendMessage("��6[ǿ��ϵͳ] ��a�������������");
				}
				else {
					sender.sendMessage("��6[ǿ��ϵͳ] ��c��û��Ȩ�ޣ�");
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
						sender.sendMessage("��6[ǿ��ϵͳ] ��c����̨�޷�ʹ�ô����");
					}
				}
				else {
					sender.sendMessage("��6[ǿ��ϵͳ] ��c��û��Ȩ�ޣ�");
				}
			}
			
			if (args[0].equalsIgnoreCase("take2")) {
				if(sender.isOp()) {
					if(sender instanceof Player) {
						((Player)sender).getInventory().addItem(smeltManager.getItem());
					} else {
						sender.sendMessage("��6[ǿ��ϵͳ] ��c����̨�޷�ʹ�ô����");
					}
				} else {
					sender.sendMessage("��6[ǿ��ϵͳ] ��c��û��Ȩ�ޣ�");
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
									sender.sendMessage("��6[ǿ��ϵͳ] ��cû���ҵ�����Ʒ");
								}
							}
							else
							{
								sender.sendMessage("��6[ǿ��ϵͳ] ��c��Ʒ���ֻ��Ϊ����");
							}
						}
						else
						{
							sender.sendMessage("��6[ǿ��ϵͳ] ��a/qh get [������Ʒ���]");
						}
						
					}
					else
					{
						sender.sendMessage("��6[ǿ��ϵͳ] ��c����̨�޷�ʹ�ô����");
					}
				}
				else
				{
					sender.sendMessage("��6[ǿ��ϵͳ] ��c��û��Ȩ�ޣ�");
				}
				
			}
			return true;
		}

		return false;
		
	}
	
	
}

