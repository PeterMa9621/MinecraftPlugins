package peterHelper;

import levelSystem.API;
import levelSystem.LevelSystem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import peterHelper.command.CommandTabCompleter;
import peterHelper.config.ConfigManager;
import peterHelper.expansion.PeterHelperExpansion;
import peterHelper.listener.LevelListener;
import peterHelper.manager.CustomItemManager;
import peterHelper.util.Util;

public class PeterHelper extends JavaPlugin
{
	public ConfigManager configManager;
	public CustomItemManager customItemManager;
	public API levelSystemApi;
	public void onEnable()
	{
		if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
			new PeterHelperExpansion(this).register();
		}
		if(Bukkit.getPluginManager().getPlugin("LevelSystem") != null){
			LevelSystem levelSystem = ((LevelSystem) Bukkit.getPluginManager().getPlugin("LevelSystem"));
			levelSystemApi = levelSystem.getAPI();
		}
		configManager = new ConfigManager(this);
		customItemManager = new CustomItemManager(this);
		configManager.loadConfig();
		getCommand("ph").setTabCompleter(new CommandTabCompleter(this));
		getServer().getPluginManager().registerEvents(new LevelListener(this), this);
		Bukkit.getConsoleSender().sendMessage("��6[PeterHelper] ��ePeterHelper�������");
	}

	public void onDisable()
	{
		Bukkit.getConsoleSender().sendMessage("��6[PeterHelper] ��ePeterHelperж�����");
	}
	
	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args)  
	{
		if (cmd.getName().equalsIgnoreCase("ph") && sender.isOp()) {
			if (args.length==0) {
				sender.sendMessage("��6=========[PeterHelper]=========");
				sender.sendMessage("��6/ph item ��3�鿴���ϵ���Ʒ��Ϣ");
				sender.sendMessage("��6/ph give [playerName] [itemName] [amount] ��3����ԭ����Ʒ");
				sender.sendMessage("��6/ph giveitem [playerName] [itemName] ��3����ItemAdders�ڵ��Զ�����Ʒ");
				return true;
			}
			
			if(args[0].equalsIgnoreCase("item") && sender instanceof Player) {
				Player player = (Player) sender;
				Util.getItemInfoInMainHand(player);

				return true;
			}

			if(args[0].equalsIgnoreCase("give")){
				if(args.length<3){
					sender.sendMessage("��a/ph give [playerName] [itemName] [amount] ��3����ԭ����Ʒ");
					return true;
				}
				String playerName = args[1];
				String itemName = args[2];
				int amount = Integer.parseInt(args[3]);
				Player player = Bukkit.getPlayer(playerName);
				if(player==null){
					sender.sendMessage("��6[PeterHelper] ��c��Ҳ�����");
					return true;
				}
				Material material = Material.getMaterial(itemName.toUpperCase());
				if (material == null) {
					sender.sendMessage("��6[PeterHelper] ��c��Ʒ������");
					return true;
				}
				ItemStack itemStack = new ItemStack(material, amount);

				if(player.getInventory().firstEmpty()!=-1) {
					player.getInventory().addItem(itemStack);
				}
				else {
					Bukkit.getServer().getWorld(player.getWorld().getName()).dropItem(player.getLocation(), itemStack);
				}
				return true;
			}

			if(args[0].equalsIgnoreCase("giveitem")){
				if(args.length<3){
					sender.sendMessage("��a/ph giveitem [playerName] [itemName] ��3����ItemAdders�ڵ��Զ�����Ʒ");
					return true;
				}

				String playerName = args[1];
				String itemName = args[2];
				Player player = Bukkit.getPlayer(playerName);
				if(player==null){
					sender.sendMessage("��6[PeterHelper] ��c��Ҳ�����");
					return true;
				}
				ItemStack itemStack = customItemManager.getCustomItem(itemName);
				if(itemStack==null){
					sender.sendMessage("��6[PeterHelper] ��c�Զ�����Ʒ������");
					return true;
				}

				if(player.getInventory().firstEmpty()!=-1) {
					player.getInventory().addItem(itemStack);
				}
				else {
					Bukkit.getServer().getWorld(player.getWorld().getName()).dropItem(player.getLocation(), itemStack);
				}
				player.sendMessage(ChatColor.GOLD + "�ѻ��" + ChatColor.WHITE + itemStack.getItemMeta().getDisplayName());
				return true;
			}

			if(args[0].equalsIgnoreCase("reload")) {
				configManager.loadConfig();
				sender.sendMessage("��6[PeterHelper] ��e�������سɹ�");
				return true;
			}
		}
		return false;
	}
}

