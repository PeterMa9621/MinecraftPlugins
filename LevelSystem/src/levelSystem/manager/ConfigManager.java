package levelSystem.manager;

import levelSystem.LevelSystem;
import levelSystem.callback.LevelPlayerCallback;
import levelSystem.model.ExpFormula;
import levelSystem.model.LevelPlayer;
import levelSystem.model.LevelReward;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import peterUtil.database.Database;
import peterUtil.database.DatabaseType;
import peterUtil.database.StorageInterface;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ConfigManager
{
	private LevelSystem plugin;
	public static int maxLevel;
	public static boolean useChatPrefix;

	private StorageInterface database;
	private String databaseName;

	public DatabaseType databaseType = DatabaseType.MYSQL;
	
	public ConfigManager(LevelSystem plugin) {
		this.plugin=plugin;
	}

	private void initDatabase() {
		database = Database.getInstance(databaseType, plugin);
		String createTableQuery = "create table if not exists level_system(id varchar(100), name varchar(100), current_exp int, level int, primary key(id));";
		database.connect(databaseName, "level_system" , "root", "mjy159357", createTableQuery);
	}

	public void loadConfig()
	{
		File file=new File(plugin.getDataFolder(),"config.yml");
		FileConfiguration config;
		if (!file.exists())
		{
			config = load(file);

			config.set("databaseName", "minecraft");

			config.set("maxLevel", 30);
			config.set("chatPrefix", true);

			// y = c1*x^p1 + c2*x^p2 + c3*x^p3
			// y = 10*x^2 + 10*x^1.5 + 80*x
			Double[] coefficients = new Double[] {10d,10d,80d};
			config.set("formula.coefficient", coefficients);

			Double[] powers = new Double[] {2d,1.5d,1d};
			config.set("formula.power", powers);

			ArrayList<String> commands = new ArrayList<String>() {{
				add("eco give %player% 100");
				add("give %player% diamond 16");
			}};
			for(int i=0; i<10; i++) {
				config.set("reward." + (i+2) + ".msg", "");
				config.set("reward." + (i+2) + ".command", commands.toArray());
			}
			
			try {
				config.save(file);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			loadConfig();
			return;
		}
		config = load(file);

		databaseName = config.getString("databaseName", "minecraft");

		maxLevel = config.getInt("maxLevel", 1);
		useChatPrefix = config.getBoolean("chatPrefix", true);

		List<Double> coefficients = config.getDoubleList("formula.coefficient");
		List<Double> powers = config.getDoubleList("formula.power");

		ExpFormula expFormula = new ExpFormula(coefficients, powers);
		ExpManager.setExpFormula(expFormula);

		ConfigurationSection section = config.getConfigurationSection("reward");
		if(section!=null) {
			for(String levelString:section.getKeys(false)) {
				int level = Integer.parseInt(levelString);
				String msg = section.getString(levelString+".msg", "").replace("&", "§");
				List<String> commands = section.getStringList(levelString+".command");
				LevelReward levelReward = new LevelReward(level, commands, msg);
				plugin.rewardManager.addReward(level, levelReward);
			}
		}

		initDatabase();
	}

	public int getMaxLevel() {
		return maxLevel;
	}

	public void loadPlayerConfig(Player player, LevelPlayerCallback callback) {
		/*
		if(plugin.players.containsKey(uniqueId)) {
			LevelPlayer levelPlayer = plugin.players.get(uniqueId);
			levelPlayer.setPlayer(player);
			return levelPlayer;
		}

		 */
		Bukkit.getScheduler().runTaskLater(plugin, () -> {
			HashMap<String, Object> result = database.get(player.getUniqueId(), new String[] {"name", "current_exp", "level"});

			int level = 1;
			int current_exp = 0;
			if(result!=null){
				level = (int) result.get("level");
				current_exp = (int) result.get("current_exp");
			}
			LevelPlayer levelPlayer = new LevelPlayer(player, level, current_exp);
			plugin.levelPlayerManager.addLevelPlayer(levelPlayer);

			if(callback!=null)
				callback.run(levelPlayer);
		}, 20);
	}

	public void savePlayerConfig(Player player) throws IOException {
		LevelPlayer levelPlayer = plugin.levelPlayerManager.getLevelPlayer(player);
		if(levelPlayer==null)
			return;
		HashMap<String, Object> data = new HashMap<String, Object>() {{
			put("name", player.getName());
			put("current_exp", levelPlayer.getCurrentExp());
			put("level", levelPlayer.getLevel());
		}};

		database.store(player.getUniqueId(), data);
	}

	public void closeDatabase() {
		database.close();
	}

	private FileConfiguration load(File file)
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
	private FileConfiguration load(String path)
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

}