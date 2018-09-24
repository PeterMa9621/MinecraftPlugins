package moreBackpack;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

public class MoreBackpack extends JavaPlugin
{
	private Economy economy;
	private boolean isEco=false;
	private int money;
	private ItemStack item;
	private boolean setupEconomy()
	{
		RegisteredServiceProvider<Economy> economyProvider = getServer().
				getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null)
		{
			economy = economyProvider.getProvider();
		}
		return (economy!=null);
	}
	
	public void onEnable() 
	{
		saveDefaultConfig();
		loadRecipe();
		money=getConfig().getInt("Money");
		if(Bukkit.getPluginManager().getPlugin("Vault")!=null)
		{
			isEco=setupEconomy();
		}
		if(!new File(getDataFolder(),"backpacks").exists()) 
		{
			new File(getDataFolder(),"backpacks").mkdir();
		}
        getLogger().info("Finish loading");
	}

	private void loadRecipe() 
	{
		int id=getConfig().getInt("ID");
		int Dur=getConfig().getInt("Dur");
		int Amount=getConfig().getInt("Amount");
		item = new ItemStack(id,Amount,(short)Dur);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("Backpack");
		item.setItemMeta(meta);
		ShapedRecipe recipe=new ShapedRecipe(item);
		String[] recipes=getConfig().getString("Recipes.Recipe").split(",");
		getLogger().info(String.valueOf(recipes));
		recipe.shape(recipes);
		for(String a:recipes)
		{
			for(int x=0;x<3;x++)
			{
				int Id=getConfig().getInt("Recipes."+a.charAt(x));
				recipe.setIngredient(a.charAt(x), new ItemStack(Id).getType());
			}
		}
		getServer().addRecipe(recipe);
	}

	public void onDisable() 
	{
		getLogger().info("Unload");
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
		if (label.equalsIgnoreCase("mb"))
		{
			if (args.length==0)
			{
				sender.sendMessage("��a=========[HELP]=========");
				sender.sendMessage("��a/mb jh �����");
				if(sender.isOp())
					sender.sendMessage("��a/mb give [���] <���> �������һ������");
				else
					sender.sendMessage("��a/mb give [���] ����"+money+"����һ������");
				return true;
			}
			
			if (args[0].equalsIgnoreCase("jh"))
			{
				if(sender instanceof Player)
				{
					Player player = (Player)sender;
					ItemStack item=player.getItemInHand();
					if(item==null || item.getType()==Material.AIR)
					{
						player.sendMessage("��a[MoreBackpack]���ϵ���Ʒ����Ϊ��");
						return true;
					}
					if(item.getTypeId()!=getConfig().getInt("ID"))
					{
						player.sendMessage("��a[MoreBackpack]����Ʒ����һ������");
						return true;
					}
					if(item.getDurability()!=(short)getConfig().getInt("Dur"))
					{
						player.sendMessage("��a[MoreBackpack]����Ʒ����һ������");
						return true;
					}
					if(item.getAmount()!=getConfig().getInt("Amount"))
					{
						int amount=getConfig().getInt("Amount");
						player.sendMessage("��a[MoreBackpack]�ñ�������������Ϊ"+amount+"���ܼ���");
						return true;
					}
					jh(player);
				}
				else
				{
					sender.sendMessage("����̨������");
					return true;
				}
			}
			
			if (args[0].equalsIgnoreCase("give"))
			{
				if(args.length==2)
				{
					if(sender instanceof Player)
					{
						if(!args[1].matches("[0-9]*"))
						{
							sender.sendMessage("��a[MoreBackpack]���ֻ��Ϊ����");
							return true;
						}
						int num=Integer.parseInt(args[1]);
						if(num<=0)
						{
							sender.sendMessage("��a[MoreBackpack]��Ų���С�ڻ����0");
							return true;
						}
						Player player = (Player)sender;
						File file=new File(getDataFolder(),"backpacks/"+player.getName()+".yml");
						FileConfiguration config=load(file);
						if(num>config.getInt("BackpackAmount"))
						{
							sender.sendMessage("��a[MoreBackpack]�ñ�ŵı���������");
							return true;
						}
						giveBackpack(player, num, true);
						return true;
					}
					else
					{
						sender.sendMessage("��a[MoreBackpack]����̨������");
						return true;
					}
				}
				else
				{
					if(sender.isOp())
						sender.sendMessage("��a/mb give [���] <���> �������һ������");
					else
						sender.sendMessage("��a/mb give [���] ����"+money+"����һ������");
				}
				if(args.length==3)
				{
					if(!sender.isOp())
					{
						sender.sendMessage("��a[MoreBackpack]��û��Ȩ��ʹ�ø����");
						return true;
					}
					if(!args[1].matches("[0-9]*"))
					{
						sender.sendMessage("��a[MoreBackpack]���ֻ��Ϊ����");
						return true;
					}
					int num=Integer.parseInt(args[1]);
					if(num<=0)
					{
						sender.sendMessage("��a[MoreBackpack]��Ų���С�ڻ����0");
						return true;
					}
					Player player=getServer().getPlayer(args[2]);
					if(player==null)
					{
						sender.sendMessage("��a[MoreBackpack]��Ҳ����ڻ��߲�����");
						return true;
					}
					giveBackpack(player, num, false);
					return true;
				}
			}
			return true;
		}

		return false;
		
	}
	
	public boolean hasOwner(String name1, String name2)
	{
		if(name1.equals(name2))
			return true;
		return getConfig().getBoolean("Open");
	}
	
	private void giveBackpack(Player player, int num, boolean b) {
		if(!getConfig().getBoolean("EnableGive"))
		{
			if(b)
			{
				if(!player.isOp())
				{
					player.sendMessage("��a[MoreBackpack]�Ѿ��ر�give���ܣ���ȡʧ��");
					return;
				}
			}
		}
		ItemStack i=item;
		ItemMeta meta=i.getItemMeta();
		ArrayList<String> lore=new ArrayList<String>();
		lore.add("��9Owner:"+player.getName()+"-"+num);
		meta.setLore(lore);
		i.setItemMeta(meta);
		player.getInventory().addItem(i);
		if(b)
		{
			if(isEco)
			{
				if(!player.isOp())
				{
					if(economy.bankHas(player.getName(), money).type==EconomyResponse.ResponseType.SUCCESS)
					{
						economy.bankWithdraw(player.getName(), money);
						player.sendMessage("��a[MoreBackpack]������ָ����ŵı�����������"+money+"Ԫ");
						return;
					}
				}
			}
		}
		player.sendMessage("��a[MoreBackpack]������ָ����ŵı���");
	}

	private void jh(Player player) 
	{
		File file=new File(getDataFolder(),"backpacks/"+player.getName()+".yml");
		FileConfiguration config=load(file);
		ItemMeta meta = player.getItemInHand().getItemMeta();
		ItemStack i = player.getItemInHand();
		if(!config.contains("BackpackAmount"))
		{
			config.set("BackpackAmount", 0);
			try {
				config.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(meta.hasLore())
		{
			for(String lore:meta.getLore())
			{
				if(lore.contains(":"))
				{
					if(lore.contains("-"))
					{
						player.sendMessage("��a[MoreBackpack]����ʧ��");
						return;
					}
				}
			}
		}
		ArrayList<String> lore=new ArrayList<String>();
		int num=config.getInt("BackpackAmount")+1;
		config.set("BackpackAmount", num);
		config.set("Items."+num+"Item", null);
		lore.add("��9Owner:"+player.getName()+"-"+num);
		meta.setLore(lore);
		i.setItemMeta(meta);
		player.setItemInHand(i);
		player.sendMessage("��a[MoreBackpack]����ɹ�");
		
	}
	
	
}

