package vipSystem;

import me.lucko.luckperms.common.util.Uuids;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.Node;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.luckperms.api.*;
import vipSystem.util.Util;

import static vipSystem.util.Util.getNewDate;

public class VipSystem extends JavaPlugin
{
	HashMap<String, String> vipGroups = new HashMap<String, String>();
	String defaultGroup = "";
	
	SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd-HH");
	
	VipSystemAPI api = new VipSystemAPI(this);
	
	HashMap<String, VipReward> reward = new HashMap<String, VipReward>();

	ConfigLoader configLoader = new ConfigLoader(this);

	public Economy economy;
	public LuckPerms luckPerms;
	
	private boolean setupEconomy()
	{
		RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServicesManager().getRegistration(Economy.class);
		if (economyProvider != null)
		{
			economy = economyProvider.getProvider();
		}
		return (economy!=null);
	}

	private boolean setupLuckPerms()
	{
		RegisteredServiceProvider<LuckPerms> economyProvider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
		if (economyProvider != null)
		{
			luckPerms = economyProvider.getProvider();
		}
		return (luckPerms!=null);
	}

	public void onEnable()
	{
		if(!new File(getDataFolder(),"Data").exists()) 
		{
			new File(getDataFolder(),"Data").mkdirs();
		}
		if(!setupEconomy())
			Bukkit.getConsoleSender().sendMessage("��a[vipSystem] ��cVault���δ����!");
		if(!setupLuckPerms()){
			Bukkit.getConsoleSender().sendMessage("��a[vipSystem] ��cLuckPerms���δ����!");
			Bukkit.getServicesManager().unregister(this);
			return;
		}

		defaultGroup = configLoader.loadConfig(vipGroups);
		configLoader.loadItems(vipGroups, reward);

		getServer().getPluginManager().registerEvents(new VipSystemListener(this), this);
		Bukkit.getConsoleSender().sendMessage("��a[vipSystem] ��e��Աϵͳ�������");
	}

	public void onDisable() 
	{
		Bukkit.getConsoleSender().sendMessage("��a[vipSystem] ��e��Աϵͳж�����");
	}
	
	public VipSystemAPI getAPI()
	{
		return api;
	}
	
	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args)  
	{
		if (cmd.getName().equalsIgnoreCase("vip"))
		{
			if (args.length==0)
			{
				sender.sendMessage("��6=========[��Աϵͳ]=========");
				sender.sendMessage("��a/vip my      ��3�鿴�ҵĻ�Ա״̬");
				if(sender.isOp())
				{
					//sender.sendMessage("��a/vip list      ��3�г����л�Ա����״̬");
					sender.sendMessage("��a/vip check [�����]      ��3�鿴Ŀ����ҵĻ�Ա״̬");
					sender.sendMessage("��a/vip give [�����] [��Ա��] [��Աʱ��(��)] ��3�趨Ŀ������ض������Ļ�Ա");
					sender.sendMessage("��a/vip remove [�����]      ��3ǿ�Ƴ���Ŀ����һ�Ա");
					sender.sendMessage("��a/vip add [�����] [��Աʱ��(��)]      ��3ǿ������Ŀ����һ�Աʱ��");
					sender.sendMessage("��a/vip reload      ��3���ز������");
					//sender.sendMessage("��a/vip reloadp      ��3����������á�4(����������)");
				}
				return true;
			}

			/*
			if(args[0].equalsIgnoreCase("list"))
			{
				if(!sender.isOp())
				{
					sender.sendMessage("��6[��Աϵͳ] ��4��û��Ȩ��");
					return true;
				}
				sender.sendMessage("��6======================================");
				for(String vipName:vipData.keySet())
				{
					sender.sendMessage("��6���:��e"+vipData.get(vipName).playerName+"��6,��Ա����:��c"
				+vipGroups.get(vipData.get(vipName).getVipGroup())+"��6,ʣ��ʱ��:��2"+vipData.get(vipName).getLeftTime());
				}
				sender.sendMessage("��6======================================");
				return true;
			}

			 */

			/*
			if(args[0].equalsIgnoreCase("reloadp"))
			{
				if(!sender.isOp())
				{
					sender.sendMessage("��6[��Աϵͳ] ��4��û��Ȩ��");
					return true;
				}
				loadPlayerConfig();
				sender.sendMessage("��6[��Աϵͳ] ��a��������������");
				return true;
			}

			 */
			
			if(args[0].equalsIgnoreCase("reload"))
			{
				if(!sender.isOp())
				{
					sender.sendMessage("��6[��Աϵͳ] ��4��û��Ȩ��");
					return true;
				}
				defaultGroup = configLoader.loadConfig(vipGroups);
				configLoader.loadItems(vipGroups, reward);
				sender.sendMessage("��6[��Աϵͳ] ��a�����������");
				return true;
			}
			
			if(args[0].equalsIgnoreCase("check"))
			{
				if(!sender.isOp())
				{
					sender.sendMessage("��6[��Աϵͳ] ��4��û��Ȩ��");
					return true;
				}
					
				if(args.length!=2)
				{
					sender.sendMessage("��a/vip check [�����] ��3�鿴Ŀ����ҵĻ�Ա״̬");
					return true;
				}


				if(Bukkit.getServer().getPlayer(args[1])==null && Bukkit.getServer().getOfflinePlayer(args[1])==null)
				{
					sender.sendMessage("��6[��Աϵͳ] ��c����Ҳ����ڻ�����");
					return true;
				}

				Player p = Bukkit.getServer().getPlayer(args[1]);

				VipPlayer vipPlayer = configLoader.loadPlayerConfig(p.getUniqueId());
				if(vipPlayer==null || vipPlayer.isDeadline())
				{
					sender.sendMessage("��6[��Աϵͳ] ��c����Ҳ��ǻ�Ա���Ա�ѵ���");
					return true;
				}
				
				sender.sendMessage("��6[��Աϵͳ] ��e�����:��c"+args[1]+"��e��Ա����:��c"+vipGroups.get(vipPlayer.getVipGroup())+"��e,ʣ���Աʱ��:��a"+vipPlayer.getLeftDays()+"��,"+vipPlayer.getLeftHours()%24+"Сʱ");
				
				return true;
			}
			
			if(args[0].equalsIgnoreCase("add"))
			{
				if(!sender.isOp())
				{
					sender.sendMessage("��6[��Աϵͳ] ��4��û��Ȩ��");
					return true;
				}
				
				if(args.length!=3)
				{
					sender.sendMessage("��6[��Աϵͳ] ��a/vip add [�����] [��Աʱ��(��)] ��3ǿ������Ŀ����һ�Աʱ��");
					return true;
				}
				
				if(Bukkit.getServer().getPlayer(args[1])==null && Bukkit.getServer().getOfflinePlayer(args[1])==null)
				{
					sender.sendMessage("��6[��Աϵͳ] ��c����Ҳ����ڻ�����");
					return true;
				}

				Player p = Bukkit.getServer().getPlayer(args[1]);

				VipPlayer vipPlayer = configLoader.loadPlayerConfig(p.getUniqueId());
				if(vipPlayer==null || vipPlayer.isDeadline())
				{
					sender.sendMessage("��6[��Աϵͳ] ��c����Ҳ��ǻ�Ա���Ա�ѵ���");
					return true;
				}
				
				if(!args[2].matches("[0-9]*") || Integer.valueOf(args[2])<=0)
				{
					sender.sendMessage("��6[��Աϵͳ] ��c��Ч�Ļ�Աʱ��(��λΪ��3���c)");
					return true;
				}

				LocalDateTime newDeadline = getNewDate(vipPlayer.getDeadline(), Integer.valueOf(args[2]));
				vipPlayer.setDeadline(newDeadline);

				try {
					configLoader.savePlayerConfig(vipPlayer);
				} catch (SQLException e) {
					e.printStackTrace();
				}

				sender.sendMessage("��6[��Աϵͳ] ��e��Ϊ��ҡ�c"+args[1]+"��e�����ˡ�a"+args[2]+"��e���Ա");
				if(Bukkit.getServer().getPlayer(args[1])!=null)
				{
					Bukkit.getServer().getPlayer(args[1]).sendMessage("��6[��Աϵͳ] ��e���ѱ������ˡ�a"+args[2]+"��e���Ա");
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("remove"))
			{
				if(!sender.isOp())
				{
					sender.sendMessage("��6[��Աϵͳ] ��4��û��Ȩ��");
					return true;
				}
				
				if(args.length!=2)
				{
					sender.sendMessage("��6[��Աϵͳ] ��a/vip remove [�����] ��3ǿ�Ƴ���Ŀ����һ�Ա");
					return true;
				}
				
				if(Bukkit.getServer().getPlayer(args[1])==null && Bukkit.getServer().getOfflinePlayer(args[1])==null)
				{
					sender.sendMessage("��6[��Աϵͳ] ��c����Ҳ����ڻ�����");
					return true;
				}

				Player p = Bukkit.getServer().getPlayer(args[1]);

				VipPlayer vipPlayer = configLoader.loadPlayerConfig(p.getUniqueId());
				if(vipPlayer==null || vipPlayer.isDeadline())
				{
					sender.sendMessage("��6[��Աϵͳ] ��c����Ҳ��ǻ�Ա���Ա�ѵ���");
					return true;
				}

				File file=new File(getDataFolder(),"/Data/"+p.getUniqueId().toString()+".yml");
				if(file.exists())
					file.delete();

				LuckPerms lp = LuckPermsProvider.get();
				User lpUser = lp.getUserManager().getUser(p.getUniqueId());
				lpUser.data().toMap().values().forEach(nodeList -> {
					nodeList.forEach(node -> {
						if(node.getKey().equalsIgnoreCase("group." + vipPlayer.getVipGroup())){
							lpUser.data().remove(node);
						}
					});
				});
				lp.getUserManager().saveUser(lpUser);

				sender.sendMessage("��6[��Աϵͳ] ��e���Ƴ���ҡ�c"+args[1]+"��e�Ļ�Ա");
				if(Bukkit.getServer().getPlayer(args[1])!=null)
				{
					Bukkit.getServer().getPlayer(args[1]).sendMessage("��6[��Աϵͳ] ��e��Ļ�Ա�ѱ��Ƴ�");
				}

				return true;
			}
			
			if(args[0].equalsIgnoreCase("my"))
			{
				if(sender instanceof Player)
				{
					Player p = (Player)sender;
					if(configLoader.loadPlayerConfig(p.getUniqueId())==null)
					{
						sender.sendMessage("��6[��Աϵͳ] ��c�㻹���ǻ�Ա");
						return true;
					}
					VipPlayer vip = configLoader.loadPlayerConfig(p.getUniqueId());
					p.sendMessage("��6[��Աϵͳ] ��e��Ա����:��c"+vipGroups.get(vip.getVipGroup())+"��e,ʣ���Աʱ��:��a"+vip.getLeftDays()+"��,"+vip.getLeftHours()%24+"Сʱ");
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("give"))
			{
				if(!sender.isOp())
				{
					sender.sendMessage("��6[��Աϵͳ] ��4��û��Ȩ��");
					return true;
				}
				
				if(args.length!=4)
				{
					sender.sendMessage("��c/vip give [�����] [��Ա��] [��Աʱ��(��)] ��3����Ŀ������ض������Ļ�Ա");
					return true;
				}
				if(Bukkit.getServer().getPlayer(args[1])==null)
				{
					sender.sendMessage("��6[��Աϵͳ] ��c����Ҳ����ڻ�����");
					return true;
				}
				if(!vipGroups.containsKey(args[2]))
				{
					sender.sendMessage("��6[��Աϵͳ] ��c�����ڵĻ�Ա��");
					return true;
				}
				if(!args[3].matches("[0-9]*") || Integer.valueOf(args[3])<=0)
				{
					sender.sendMessage("��6[��Աϵͳ] ��c��Ч�Ļ�Աʱ��(��λΪ��3���c)");
					return true;
				}
				int days = Integer.valueOf(args[3]);
				String playerName = args[1];
				String group = args[2];
				Player p = Bukkit.getPlayer(playerName);
				VipPlayer vipPlayer = configLoader.loadPlayerConfig(p.getUniqueId());

				if(vipPlayer!=null && vipPlayer.getVipGroup().equalsIgnoreCase(args[2]))
				{
					LocalDateTime newDeadlineDate = getNewDate(vipPlayer.getDeadline(), days);
					vipPlayer.setDeadline(newDeadlineDate);
					try {
						configLoader.savePlayerConfig(vipPlayer);
					} catch (SQLException e) {
						e.printStackTrace();
					}
					Bukkit.getServer().getPlayer(args[1]).sendMessage("��6[��Աϵͳ] ��e�ɹ����ѡ�c"+vipGroups.get(args[2])+" ��e"+args[3]+"��");
				}
				else
				{
					LocalDateTime recentDate = LocalDateTime.now();
					LocalDateTime deadlineDate = getNewDate(recentDate, days);
					vipPlayer = new VipPlayer(p.getUniqueId(), args[1], recentDate, deadlineDate, args[2]);
					try {
						configLoader.savePlayerConfig(vipPlayer);
					} catch (SQLException e) {
						e.printStackTrace();
					}
					p.sendMessage("��6[��Աϵͳ] ��e�ɹ���ͨ��c"+vipGroups.get(args[2])+" ��e"+args[3]+"��");
					Bukkit.getServer().broadcastMessage("��6��ҡ�a"+args[1]+"��6��Ϊ��c"+vipGroups.get(args[2]));
				}
				
				if(!reward.get(group).getItems().isEmpty() && reward.get(group).money!=0)
				{
					Player whoWantToBeVip = Bukkit.getServer().getPlayer(args[1]);
					for(ItemStack item:reward.get(group).getItems())
					{
						if(whoWantToBeVip.getInventory().firstEmpty()!=-1)
						{
							whoWantToBeVip.getInventory().addItem(item);
						}
						else
						{
							Bukkit.getServer().getWorld(whoWantToBeVip.getWorld().getName()).dropItem(whoWantToBeVip.getLocation(), item);
						}
					}
					economy.depositPlayer(whoWantToBeVip.getName(), reward.get(group).money);
					whoWantToBeVip.sendMessage("��6[��Աϵͳ] ��a��Ʒ�����ѷ��뱳���ڣ�����������ˣ������䵽�����ϣ���ע��ʰȡ��");
				}

				LuckPerms lp = LuckPermsProvider.get();
				User lpUser = lp.getUserManager().getUser(p.getUniqueId());
				lpUser.data().add(Node.builder("group." + group).build());
				lp.getUserManager().saveUser(lpUser);
				
				if(sender instanceof Player)
				{
					if(!p.getName().equalsIgnoreCase(args[1]))
						sender.sendMessage("��6[��Աϵͳ] ��e�Ѹ�����ҡ�c"+args[1]+" ��a"+vipGroups.get(args[2])+" ��c"+args[3]+"��e��");
				}
				else
					sender.sendMessage("��6[��Աϵͳ] ��e�Ѹ�����ҡ�c"+args[1]+" ��a"+vipGroups.get(args[2])+" ��c"+args[3]+"��e��");
				return true;
			}
			return true;
		}
		return false;
		
	}
	
}

