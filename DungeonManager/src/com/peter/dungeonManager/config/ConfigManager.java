package com.peter.dungeonManager.config;

import com.mysql.fabric.xmlrpc.base.Array;
import com.peter.dungeonManager.DungeonManager;
import com.peter.dungeonManager.model.DungeonSetting;
import com.peter.dungeonManager.util.DataManager;
import com.peter.dungeonManager.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ConfigManager {
    public static int maxInstancePerDungeon;
    public static int startGameDelay = 30;
    public static ArrayList<Material> itemForbid = new ArrayList<>();
    private static String lockIconId;
    private static int lockIconCustomModelId;

    private DungeonManager plugin;
    public ConfigManager(DungeonManager plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        File file = new File(plugin.getDataFolder(), "/config.yml");
        FileConfiguration config;
        if(!file.exists()){
            config = load(file);

            //config.set("maxInstancePerDungeon", 2);
            config.set("startGameDelay", 30);

            config.set("lockDungeonIcon.id", "STICK");
            config.set("lockDungeonIcon.model", 71);

            config.set("itemForbid", new ArrayList<String >() {{
                add("ender_pearl".toUpperCase());
            }});

            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            loadConfig();
            return;
        }

        plugin.dataManager.dungeonGroupSetting.clear();
        plugin.dataManager.getDungeonGroups().clear();
        config = load(file);
        maxInstancePerDungeon = config.getInt("maxInstancePerDungeon", 2);
        startGameDelay = config.getInt("startGameDelay", 30);

        lockIconId = config.getString("lockDungeonIcon.id", "STICK").toUpperCase();
        lockIconCustomModelId = config.getInt("lockDungeonIcon.model", 71);


        List<String> itemIds = config.getStringList("itemForbid");
        for(String itemId:itemIds) {
            itemForbid.add(Material.getMaterial(itemId.toUpperCase()));
        }

        File dungeonsXLMapDir = new File(plugin.getDataFolder().getParentFile().getAbsolutePath() + "/DungeonsXL/maps");
        int numDungeon = 0;
        for(File mapDir:dungeonsXLMapDir.listFiles()){
            if(mapDir.getName().equalsIgnoreCase(".raw"))
                continue;
            if(loadMapConfig(mapDir))
                numDungeon++;
        }

        Bukkit.getConsoleSender().sendMessage("§a[DungeonManager] §e已加载" + numDungeon + "个副本");
    }

    private Boolean loadMapConfig(File file) {
        File configFile = new File(file, "/config.yml");

        if(!configFile.exists())
            return false;
        FileConfiguration config = load(configFile);
        String dungeonName = file.getName();
        String displayName = config.getString("title.title", "§6副本").replace("&", "§");

        int playInterval = config.getInt("timeToNextPlayAfterFinish", 2);

        ConfigurationSection dungeonConfig = config.getConfigurationSection("dungeonManager");
        if(dungeonConfig!=null){
            int minPlayers = dungeonConfig.getInt("minPlayers", 1);
            int maxPlayers = dungeonConfig.getInt("maxPlayers", 4);
            int minLevel = dungeonConfig.getInt("minLevel", 1);

            DungeonSetting dungeonSetting = new DungeonSetting(dungeonName, displayName, minPlayers, maxPlayers, minLevel);
            dungeonSetting.setPlayInterval(playInterval);
            if(dungeonConfig.contains("icon")) {
                String itemId = dungeonConfig.getString("icon.id");
                int customModelId = dungeonConfig.getInt("icon.model", 0);
                ItemStack icon = Util.createItem(Material.getMaterial(itemId.toUpperCase()), "§f创建" + displayName + "§f的队伍", customModelId);
                dungeonSetting.setIcon(icon);
            }

            plugin.dataManager.dungeonGroupSetting.put(dungeonName, dungeonSetting);
            return true;
        }
        return false;
    }

    public HashMap<String, Long> loadPlayerDungeonTime(Player player) {
        UUID uniqueId = player.getUniqueId();
        File dungeonsXLPlayerData = new File(plugin.getDataFolder().getParentFile().getAbsolutePath() + "/DungeonsXL/players/" + uniqueId.toString() + ".yml");
        HashMap<String, Long> timestamps = new HashMap<>();
        if(dungeonsXLPlayerData.exists()) {
            FileConfiguration config = load(dungeonsXLPlayerData);
            ConfigurationSection section = config.getConfigurationSection("stats.timeLastFinished");
            if(section!=null) {
                for(String dungeonName:section.getKeys(false)) {
                    timestamps.put(dungeonName, section.getLong(dungeonName));
                }
            }
        }
        return timestamps;
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

    public ItemStack getLockIcon(String dungeonName) {
        return Util.createItem(Material.getMaterial(lockIconId), dungeonName, lockIconCustomModelId);
    }
}
