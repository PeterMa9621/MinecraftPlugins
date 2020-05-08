package levelSystem.manager;

import levelSystem.LevelSystem;
import levelSystem.callback.LevelPlayerCallback;
import levelSystem.model.BonusCard;
import levelSystem.model.ExpFormula;
import levelSystem.model.LevelPlayer;
import levelSystem.model.LevelReward;
import levelSystem.util.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import peterUtil.database.Database;
import peterUtil.database.DatabaseType;
import peterUtil.database.StorageInterface;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
		String createTableQuery = "create table if not exists level_system(id varchar(100), name varchar(100), current_exp int, level int, bonus_card_expired_time datetime, bonus_name varchar(50), primary key(id));";
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

			config.set("bonusExpCard.normal.itemId", "STICK");
			config.set("bonusExpCard.normal.customModelId", 34);
			config.set("bonusExpCard.normal.displayName", "&f2倍经验卡");
			config.set("bonusExpCard.normal.lore", new ArrayList<String>() {{
				add("&6使用后所有获得历练经验 × 2");
				add("&6有效时长: &22小时");
				add("&5右键使用");
			}});
			config.set("bonusExpCard.normal.times", 2);
			config.set("bonusExpCard.normal.duration", 120);

			config.set("bonusExpCard.advance.itemId", "STICK");
			config.set("bonusExpCard.advance.customModelId", 35);
			config.set("bonusExpCard.advance.displayName", "&f3倍经验卡");
			config.set("bonusExpCard.advance.lore", new ArrayList<String>() {{
				add("&6使用后所有获得历练经验 × 3");
				add("&6有效时长: &22小时");
				add("&5右键使用");
			}});
			config.set("bonusExpCard.advance.times", 3);
			config.set("bonusExpCard.advance.duration", 120);


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

		plugin.rewardManager.clear();
		plugin.bonusCardManager.clear();

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

		section = config.getConfigurationSection("bonusExpCard");
		if(section!=null) {
			for(String cardName:section.getKeys(false)) {
				ConfigurationSection cardSection = section.getConfigurationSection(cardName);
				String itemId = cardSection.getString("itemId", "PAPER");
				int customModelId = cardSection.getInt("customModelId", 70);
				String displayName = cardSection.getString("displayName", "&f2倍经验卡");
				List<String> lore = cardSection.getStringList("lore");
				ItemStack bonusExpCard = ItemUtil.createItem(itemId, displayName, lore, customModelId);
				int duration = cardSection.getInt("duration", 60);
				double times = cardSection.getDouble("times", 2.0);
				BonusCard bonusCard = new BonusCard(bonusExpCard, times, duration, cardName);
				plugin.bonusCardManager.addBonusCard(cardName, bonusCard);
			}
		}

		initDatabase();
	}

	public int getMaxLevel() {
		return maxLevel;
	}

	public void loadPlayerConfig(Player player, LevelPlayerCallback callback) {
		final LevelPlayer[] levelPlayer = {null};
		if(plugin.levelPlayerManager.containsLevelPlayer(player)) {
			levelPlayer[0] = plugin.levelPlayerManager.getLevelPlayer(player);
		}

		Bukkit.getScheduler().runTaskLater(plugin, () -> {
			HashMap<String, Object> result = database.get(player.getUniqueId(), new String[] {"name", "current_exp", "level", "bonus_card_expired_time", "bonus_name"});

			int level = 1;
			int current_exp = 0;
			LocalDateTime bonus_card_expired_time = null;
			String bonusName = null;
			if(result!=null){
				level = (int) result.get("level");
				current_exp = (int) result.get("current_exp");
				Object obj = result.get("bonus_card_expired_time");
				if(obj!=null)
					bonus_card_expired_time = ((Timestamp) obj).toLocalDateTime();
				obj = result.get("bonus_name");
				if(obj!=null)
					bonusName = (String) obj;
			}
			if(levelPlayer[0] == null)
				levelPlayer[0] = new LevelPlayer(player, level, current_exp, bonus_card_expired_time, bonusName);
			else {
				levelPlayer[0].setLevel(level);
				levelPlayer[0].setCurrentExp(current_exp);
				levelPlayer[0].setBonusCardExpiredTime(bonus_card_expired_time);
				levelPlayer[0].setBonusName(bonusName);
			}

			plugin.levelPlayerManager.addLevelPlayer(levelPlayer[0]);

			if(callback!=null)
				callback.run(levelPlayer[0]);
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
			put("bonus_card_expired_time", levelPlayer.getBonusCardExpiredTime());
			put("bonus_name", levelPlayer.getBonusCardName());
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