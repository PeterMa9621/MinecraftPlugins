package com.peter.config;

import com.peter.FestivalReward;
import com.peter.manager.FestivalManager;
import com.peter.manager.FestivalPlayerManager;
import com.peter.model.Festival;
import com.peter.model.FestivalPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.Hash;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import peterUtil.database.Database;
import peterUtil.database.DatabaseType;
import peterUtil.database.MYSQLStorage;
import peterUtil.database.StorageInterface;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ConfigManager {
    private FestivalReward plugin;
    public SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");

    private FestivalManager festivalManager;
    private FestivalPlayerManager festivalPlayerManager;
    private StorageInterface database;
    private DatabaseType databaseType;
    private String databaseName;
    private MYSQLStorage mysqlStorage;

    private BukkitTask closeConnection;
    private HashMap<String, Boolean> ipStatus = new HashMap<>();

    public ConfigManager(FestivalReward plugin) {
        this.plugin = plugin;
        this.festivalManager = plugin.festivalManager;
        this.festivalPlayerManager = plugin.festivalPlayerManager;
    }

    public void initDatabase() {
        database = Database.getInstance(databaseType, plugin);
        if(databaseType.equals(DatabaseType.MYSQL)) {
            mysqlStorage = (MYSQLStorage) database;
            String createTableQuery = "create table if not exists festival_reward(id varchar(100), receive_date varchar(10), ip varchar(50), primary key(id));";
            mysqlStorage.connect(databaseName, "festival_reward" , "root", "mjy159357", createTableQuery);
        }
    }

    public void loadConfig() {
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
            config.set("test.displayName", "&6示例节日");
            config.set("test.describe", new ArrayList<String>() {{
                add("&f节日描述");
            }});
            config.set("test.numReward", 2);
            config.set("test.reward", new ArrayList<String>() {{
                add("eco give %player% 100");
            }});

            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            loadConfig();
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
            int numReward = festivalSection.getInt("numReward", 1);
            String displayName = festivalSection.getString("displayName", "节日").replace('&', ChatColor.COLOR_CHAR);
            List<String> describe = new ArrayList<>();
            festivalSection.getStringList("describe").forEach(each -> {
                describe.add(each.replace('&', ChatColor.COLOR_CHAR));
            });
            Festival festival = new Festival(festivalDate, commands, displayName, describe, numReward);
            this.festivalManager.addFestival(festival);
        }

        initDatabase();
    }

    public FestivalPlayer loadPlayerData(Player player) {
        UUID uuid = player.getUniqueId();
        if(this.festivalPlayerManager.containFestivalPlayer(uuid)) {
            FestivalPlayer festivalPlayer = festivalPlayerManager.getFestivalPlayer(uuid);
            festivalPlayer.setPlayer(player);
            return festivalPlayer;
        }
        FestivalPlayer festivalPlayer = new FestivalPlayer(player);
        festivalPlayer.setIp(player.getAddress().toString().substring(1).split(":")[0]);
        HashMap<String, Object> result = mysqlStorage.get(uuid, new String[] {"receive_date"});
        if(result!=null){
            String receiveDate = (String) result.get("receive_date");
            String ip = (String) result.get("ip");
            festivalPlayer.setReceiveDate(receiveDate);
            festivalPlayer.setIp(ip);
        }
        festivalPlayerManager.addFestivalPlayer(festivalPlayer);
        return festivalPlayer;
    }

    public boolean hasThisIpReceivedReward(String ip) throws SQLException {
        boolean status = false;
        if(ipStatus.containsKey(ip)) {
            status = ipStatus.get(ip);
        }
        if(status)
            return true;

        Connection connection = mysqlStorage.getConnection();
        String query = "Select * from festival_reward where ip=?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setObject(1, ip);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            String receive_date = resultSet.getString("receive_date");
            if(receive_date.equalsIgnoreCase(date.format(new Date()))) {
                ipStatus.put(ip, true);
                return true;
            }
        }

        if(closeConnection!=null)
            closeConnection.cancel();
        closeConnection = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, 15*20);
        ipStatus.put(ip, false);
        return false;
    }

    public void savePlayerData(FestivalPlayer festivalPlayer) {
        Player player = festivalPlayer.getPlayer();
        UUID uuid = player.getUniqueId();
        String receiveDate = festivalPlayer.getReceiveDate();
        String ip = festivalPlayer.getIp();
        HashMap<String, Object> paths = new HashMap<String, Object>() {{
            put("receive_date", receiveDate);
            put("ip", ip);
        }};

        try {
            mysqlStorage.store(uuid, paths);
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
