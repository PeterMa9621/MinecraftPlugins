package com.peter.dungeonManager.config;

import com.peter.dungeonManager.DungeonManager;
import com.peter.dungeonManager.model.DungeonSetting;
import com.peter.dungeonManager.util.DataManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigManager {
    public static int maxInstancePerDungeon;
    public static int startGameDelay = 30;

    public static void loadConfig(DungeonManager plugin) {
        File file = new File(plugin.getDataFolder(), "/config.yml");
        FileConfiguration config;
        if(!file.exists()){
            config = load(file);

            config.set("maxInstancePerDungeon", 2);
            config.set("startGameDelay", 30);
            config.set("dungeons.lieyanwang.displayName", "&c烈焰王");
            config.set("dungeons.lieyanwang.minPlayers", 2);
            config.set("dungeons.lieyanwang.maxPlayers", 4);

            config.set("dungeons.anjinshushi.displayName", "&c暗金术士");
            config.set("dungeons.anjinshushi.minPlayers", 2);
            config.set("dungeons.anjinshushi.maxPlayers", 4);

            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            loadConfig(plugin);
            return;
        }

        DataManager.dungeonGroupSetting.clear();
        DataManager.getDungeonGroups().clear();
        config = load(file);
        maxInstancePerDungeon = config.getInt("maxInstancePerDungeon", 2);
        startGameDelay = config.getInt("startGameDelay", 30);
        int numDungeon = 0;
        ConfigurationSection dungeonConfig = config.getConfigurationSection("dungeons");
        if(dungeonConfig!=null){
            for(String dungeonName:dungeonConfig.getKeys(false)){
                String displayName = dungeonConfig.getString(dungeonName + ".displayName").replace("&", "§");
                int minPlayers = dungeonConfig.getInt(dungeonName + ".minPlayers", 1);
                int maxPlayers = dungeonConfig.getInt(dungeonName + ".maxPlayers", 4);

                DungeonSetting dungeonSetting = new DungeonSetting(dungeonName, displayName, minPlayers, maxPlayers);

                DataManager.dungeonGroupSetting.put(dungeonName, dungeonSetting);
                numDungeon ++;
            }
        }

        Bukkit.getConsoleSender().sendMessage("§a[DungeonManager] §e已加载" + numDungeon + "个副本");
    }

    public static FileConfiguration load(File file)
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
    public static FileConfiguration load(String path)
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
