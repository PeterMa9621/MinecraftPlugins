package levelSystem;

import levelSystem.expansion.LevelSystemExpansion;
import levelSystem.listener.BonusCardListener;
import levelSystem.listener.LevelSystemListener;
import levelSystem.manager.*;
import levelSystem.model.BonusCard;
import levelSystem.model.LevelPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import peterUtil.database.Database;
import peterUtil.database.DatabaseType;
import peterUtil.database.StorageInterface;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class LevelSystem extends JavaPlugin
{


	public API api;

	public ConfigManager configManager;
	public RewardManager rewardManager;
	public LevelPlayerManager levelPlayerManager;
	public BonusCardManager bonusCardManager;

	public void onEnable() {
		if(!getDataFolder().exists())
		{
			getDataFolder().mkdir();
		}
		if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
			new LevelSystemExpansion(this).register();
		}
		levelPlayerManager = new LevelPlayerManager(this);
		bonusCardManager = new BonusCardManager(this);
		configManager = new ConfigManager(this);
		rewardManager = new RewardManager(this);

		configManager.loadConfig();

		getServer().getPluginManager().registerEvents(new LevelSystemListener(this), this);
		getServer().getPluginManager().registerEvents(new BonusCardListener(this), this);
		api = new API(this);
		Bukkit.getConsoleSender().sendMessage("§a[LevelSystem] §e等级系统加载完毕");
	}

	public void onDisable() {
		for(LevelPlayer levelPlayer:levelPlayerManager.getPlayers()) {
			try {
				configManager.savePlayerConfig(levelPlayer.getPlayer());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		configManager.closeDatabase();
		Bukkit.getConsoleSender().sendMessage("§a[LevelSystem] §e等级系统卸载完毕");
	}

	public API getAPI() {
		return api;
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
	
	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args)  
	{
		if (cmd.getName().equalsIgnoreCase("level")) {
			if(args.length==0) {
				if(sender instanceof Player) {
					Player p = (Player)sender;
					LevelPlayer levelPlayer = levelPlayerManager.getLevelPlayer(p);
					if(levelPlayer==null)
						return true;
					int level = levelPlayer.getLevel();
					int currentLevelExp = ExpManager.getExp(level);
					int currentExp = levelPlayer.getCurrentExp();
					sender.sendMessage("§a你的当前等级为:§5"+level+"§a,距离下一级还需:§5"+(currentLevelExp-currentExp));
					return true;
				}
			}
			
			if(args[0].equalsIgnoreCase("help")) {
				sender.sendMessage("§a=========[等级系统]=========");
				sender.sendMessage("§a/level §3查看当前等级");
				sender.sendMessage("§a/level help §3查看帮助");
				if(sender.isOp()) {
					sender.sendMessage("§a/level add [玩家名] [数量] §3增加该玩家的经验");
					sender.sendMessage("§a/level give [玩家名] [经验卡名字] [数量] §3给予玩家经验卡");
					sender.sendMessage("§a/level clear [玩家名] §3清空该玩家等级和经验");
					sender.sendMessage("§a/level set [玩家名] [数量] §3设置玩家等级");
					sender.sendMessage("§a/level reload §3重读配置");
				}
				return true;
			}

			if(args[0].equalsIgnoreCase("give") && sender.isOp()) {
				if(args.length==4) {
					Player player = Bukkit.getPlayer(args[1]);
					if(bonusCardManager.getBonusCard(args[2])==null) {
						sender.sendMessage("§6[等级系统] §c不存在的经验卡");
						return true;
					}
					if(!args[3].matches("[0-9]*")) {
						sender.sendMessage("§6[等级系统] §c等级必须是数字");
						return true;
					}
					if(player==null) {
						sender.sendMessage("§6[等级系统] §c玩家不存在或不在线");
						return true;
					}

					BonusCard bonusCard = bonusCardManager.getBonusCard(args[2]);
					ItemStack itemStack = bonusCard.getItemStack();
					itemStack.setAmount(Integer.parseInt(args[3]));
					Inventory inventory = player.getInventory();
					if(inventory.firstEmpty()<0) {
						player.getWorld().dropItem(player.getLocation(), itemStack);
					} else {
						inventory.addItem(itemStack);
					}
				}
				else {
					sender.sendMessage("§4用法：§a/level add [玩家名] [数量] §3增加该玩家的经验");
				}
				return true;
			}

			if(args[0].equalsIgnoreCase("add") && sender.isOp()) {
				if(args.length==3) {
					Player player = Bukkit.getPlayer(args[1]);
					if(!args[2].matches("[0-9]*")) {
						sender.sendMessage("§6[等级系统] §c等级必须是数字");
						return true;
					}
					if(player==null) {
						sender.sendMessage("§6[等级系统] §c玩家不存在或不在线");
						return true;
					}

					LevelPlayer levelPlayer = levelPlayerManager.getLevelPlayer(player);
					int exp = Integer.parseInt(args[2]);
					int finalExp = levelPlayer.addExp(exp);
					sender.sendMessage("§6[等级系统] §a已为玩家§5"+args[1]+"§a增加§e" + finalExp + "§a点经验");
					String message = "§6获得§e%d§6点经验§e(基础%d,加成%d)";
					levelPlayer.getPlayer().sendMessage(String.format(message, finalExp, exp, finalExp-exp));
				}
				else {
					sender.sendMessage("§4用法：§a/level add [玩家名] [数量] §3增加该玩家的经验");
				}
				return true;
			}

			if(args[0].equalsIgnoreCase("clear") && sender.isOp()) {
				if(args.length==2) {
					Player player = Bukkit.getPlayer(args[1]);
					if(player!=null) {
						levelPlayerManager.getLevelPlayer(player).clearLevel();
						sender.sendMessage("§6[等级系统] §a已清空玩家§5"+args[1]+"§a的总经验");
					}
					else {
						sender.sendMessage("§6[等级系统] §c玩家不存在或不在线");
					}
				}
				else {
					sender.sendMessage("§4用法：§a/level clear [玩家名] §3清空该玩家总经验");
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("reload") && sender.isOp()) {
				configManager.loadConfig();
				sender.sendMessage("§6[等级系统] §a重读配置成功");
				return true;
			}
			
			if(args[0].equalsIgnoreCase("set") && sender.isOp()) {
				if(args.length==3)
				{
					Player player = Bukkit.getPlayer(args[1]);
					if(player!=null)
					{
						if(args[2].matches("[0-9]*"))
						{
							int level = Integer.parseInt(args[2]);
							if(level<=configManager.getMaxLevel() && level>0)
							{
								LevelPlayer levelPlayer = levelPlayerManager.getLevelPlayer(player);
								levelPlayer.setLevel(level);
								levelPlayer.setCurrentExp(0);
								sender.sendMessage("§6[等级系统] §a已设置玩家§5"+args[1]+"§a等级为§e"+args[2]);
							}
							else
							{
								sender.sendMessage("§6[等级系统] §c数量必须是有效的等级");
							}
						}
						else
						{
							sender.sendMessage("§6[等级系统] §c等级必须是数字");
						}
					}
					else
					{
						sender.sendMessage("§6[等级系统] §c目标玩家不存在或不在线");
					}
				}
				else
				{
					sender.sendMessage("§4用法：§a/level setLevel [玩家名] [数量] §3设置玩家等级");
				}
			}
			return true;
		}
		return false;
	}
}

