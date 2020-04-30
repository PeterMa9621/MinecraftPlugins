package com.peter.config;

import com.peter.FestivalReward;
import com.peter.manager.FestivalManager;
import com.peter.manager.FestivalPlayerManager;
import com.peter.model.Festival;
import com.peter.model.FestivalPlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import peterUtil.database.Database;
import peterUtil.database.DatabaseType;
import peterUtil.database.StorageInterface;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ConfigManager {
    private FestivalReward plugin;
    public SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");

    private FestivalManager festivalManager;
    private FestivalPlayerManager festivalPlayerManager;
    private StorageInterface database;
    private DatabaseType databaseType;
    private String databaseName;

    public ConfigManager(FestivalReward plugin) {
        this.plugin = plugin;
        this.festivalManager = plugin.festivalManager;
        this.festivalPlayerManager = plugin.festivalPlayerManager;

        initDatabase();
    }

    public void initDatabase() {
        database = Database.getInstance(databaseType, plugin);
        String createTableQuery = "create table if not exists festival_reward(id varchar(100), receive_date varchar(10), primary key(id));";
        database.connect(databaseName, "festival_reward" , "root", "mjy159357", createTableQuery);
    }

    public void loadFestival() {
        File file = new File(plugin.getDataFolder(), "");
        if(!file.exists())
            file.mkdir();

        file = new File(plugin.getDataFolder(), "/config.yml");
        FileConfiguration config;
        if(!file.exists()) {
            config = load(file);
            config.set("database", "MYSQL");
            config.set("databaseName", "minecraft");
            config.set("test.date", "1900-01-01");
            config.set("test.reward", new ArrayList<String>() {{
                add("eco give %player% 100");
            }});

            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            loadFestival();
            return;
        }
        config = load(file);
        databaseType = DatabaseType.valueOf(config.getString("database", "MYSQL").toUpperCase());
        databaseName = config.getString("databaseName");
        for(String festivalName:config.getKeys(false)) {
            ConfigurationSection festivalSection = config.getConfigurationSection(festivalName);
            if(festivalSection == null)
                continue;
            String festivalDate = festivalSection.getString("date", "1900-01-01");
            List<String> commands = festivalSection.getStringList("reward");
            Festival festival = new Festival(festivalDate, commands);
            this.festivalManager.addFestival(festival);
        }
    }

    public FestivalPlayer loadPlayerData(Player player) {
        UUID uuid = player.getUniqueId();
        if(this.festivalPlayerManager.containFestivalPlayer(uuid)) {
            FestivalPlayer festivalPlayer = festivalPlayerManager.getFestivalPlayer(uuid);
            festivalPlayer.setPlayer(player);
            return festivalPlayer;
        }
        FestivalPlayer festivalPlayer = new FestivalPlayer(player);
        HashMap<String, Object> result = database.get(uuid, new String[] {"receive_date"});
        if(result!=null){
            String receiveDate = (String) result.get("receive_date");
            festivalPlayer.setReceiveDate(receiveDate);
        }
        return festivalPlayer;
    }

    public void savePlayerData(FestivalPlayer festivalPlayer) {
        Player player = festivalPlayer.getPlayer();
        UUID uuid = player.getUniqueId();
        String receiveDate = festivalPlayer.getReceiveDate();
        HashMap<String, Object> paths = new HashMap<String, Object>() {{
            put("receive_date", receiveDate);
        }};

        try {
            database.store(uuid, paths);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private FileConfiguration load(File file) {
        if (!(file.exists())) {
            try {
                file.createNewFile();
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }
        return YamlConfiguration.loadConfiguration(file);
    }
}
