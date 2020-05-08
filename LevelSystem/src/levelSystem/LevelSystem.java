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
		Bukkit.getConsoleSender().sendMessage("��a[LevelSystem] ��e�ȼ�ϵͳ�������");
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
		Bukkit.getConsoleSender().sendMessage("��a[LevelSystem] ��e�ȼ�ϵͳж�����");
	}

	public API getAPI() {
		return api;
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
					sender.sendMessage("��a��ĵ�ǰ�ȼ�Ϊ:��5"+level+"��a,������һ������:��5"+(currentLevelExp-currentExp));
					return true;
				}
			}
			
			if(args[0].equalsIgnoreCase("help")) {
				sender.sendMessage("��a=========[�ȼ�ϵͳ]=========");
				sender.sendMessage("��a/level ��3�鿴��ǰ�ȼ�");
				sender.sendMessage("��a/level help ��3�鿴����");
				if(sender.isOp()) {
					sender.sendMessage("��a/level add [�����] [����] ��3���Ӹ���ҵľ���");
					sender.sendMessage("��a/level give [�����] [���鿨����] [����] ��3������Ҿ��鿨");
					sender.sendMessage("��a/level clear [�����] ��3��ո���ҵȼ��;���");
					sender.sendMessage("��a/level set [�����] [����] ��3������ҵȼ�");
					sender.sendMessage("��a/level reload ��3�ض�����");
				}
				return true;
			}

			if(args[0].equalsIgnoreCase("give") && sender.isOp()) {
				if(args.length==4) {
					Player player = Bukkit.getPlayer(args[1]);
					if(bonusCardManager.getBonusCard(args[2])==null) {
						sender.sendMessage("��6[�ȼ�ϵͳ] ��c�����ڵľ��鿨");
						return true;
					}
					if(!args[3].matches("[0-9]*")) {
						sender.sendMessage("��6[�ȼ�ϵͳ] ��c�ȼ�����������");
						return true;
					}
					if(player==null) {
						sender.sendMessage("��6[�ȼ�ϵͳ] ��c��Ҳ����ڻ�����");
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
					sender.sendMessage("��4�÷�����a/level add [�����] [����] ��3���Ӹ���ҵľ���");
				}
				return true;
			}

			if(args[0].equalsIgnoreCase("add") && sender.isOp()) {
				if(args.length==3) {
					Player player = Bukkit.getPlayer(args[1]);
					if(!args[2].matches("[0-9]*")) {
						sender.sendMessage("��6[�ȼ�ϵͳ] ��c�ȼ�����������");
						return true;
					}
					if(player==null) {
						sender.sendMessage("��6[�ȼ�ϵͳ] ��c��Ҳ����ڻ�����");
						return true;
					}

					LevelPlayer levelPlayer = levelPlayerManager.getLevelPlayer(player);
					int exp = Integer.parseInt(args[2]);
					int finalExp = levelPlayer.addExp(exp);
					sender.sendMessage("��6[�ȼ�ϵͳ] ��a��Ϊ��ҡ�5"+args[1]+"��a���ӡ�e" + finalExp + "��a�㾭��");
					String message = "��6��á�e%d��6�㾭���e(����%d,�ӳ�%d)";
					levelPlayer.getPlayer().sendMessage(String.format(message, finalExp, exp, finalExp-exp));
				}
				else {
					sender.sendMessage("��4�÷�����a/level add [�����] [����] ��3���Ӹ���ҵľ���");
				}
				return true;
			}

			if(args[0].equalsIgnoreCase("clear") && sender.isOp()) {
				if(args.length==2) {
					Player player = Bukkit.getPlayer(args[1]);
					if(player!=null) {
						levelPlayerManager.getLevelPlayer(player).clearLevel();
						sender.sendMessage("��6[�ȼ�ϵͳ] ��a�������ҡ�5"+args[1]+"��a���ܾ���");
					}
					else {
						sender.sendMessage("��6[�ȼ�ϵͳ] ��c��Ҳ����ڻ�����");
					}
				}
				else {
					sender.sendMessage("��4�÷�����a/level clear [�����] ��3��ո�����ܾ���");
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("reload") && sender.isOp()) {
				configManager.loadConfig();
				sender.sendMessage("��6[�ȼ�ϵͳ] ��a�ض����óɹ�");
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
								sender.sendMessage("��6[�ȼ�ϵͳ] ��a��������ҡ�5"+args[1]+"��a�ȼ�Ϊ��e"+args[2]);
							}
							else
							{
								sender.sendMessage("��6[�ȼ�ϵͳ] ��c������������Ч�ĵȼ�");
							}
						}
						else
						{
							sender.sendMessage("��6[�ȼ�ϵͳ] ��c�ȼ�����������");
						}
					}
					else
					{
						sender.sendMessage("��6[�ȼ�ϵͳ] ��cĿ����Ҳ����ڻ�����");
					}
				}
				else
				{
					sender.sendMessage("��4�÷�����a/level setLevel [�����] [����] ��3������ҵȼ�");
				}
			}
			return true;
		}
		return false;
	}
}

