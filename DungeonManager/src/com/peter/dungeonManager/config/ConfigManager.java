package com.peter.dungeonManager.config;

import com.peter.dungeonManager.DungeonManager;
import com.peter.dungeonManager.model.DungeonSetting;
import com.peter.dungeonManager.util.DataManager;
import com.peter.dungeonManager.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {
    public static int maxInstancePerDungeon;
    public static int startGameDelay = 30;
    public static ArrayList<Material> itemForbid = new ArrayList<>();

    public static void loadConfig(DungeonManager plugin) {
        File file = new File(plugin.getDataFolder(), "/config.yml");
        FileConfiguration config;
        if(!file.exists()){
            config = load(file);

            config.set("maxInstancePerDungeon", 2);
            config.set("startGameDelay", 30);

            config.set("itemForbid", new ArrayList<String >() {{
                add("ender_pearl".toUpperCase());
            }});

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

    private static Boolean loadMapConfig(File file) {
        File configFile = new File(file, "/config.yml");

        if(!configFile.exists())
            return false;
        FileConfiguration config = load(configFile);
        String dungeonName = file.getName();
        String displayName = config.getString("title.title", "§6副本").replace("&", "§");
        ConfigurationSection dungeonConfig = config.getConfigurationSection("dungeonManager");
        if(dungeonConfig!=null){
            int minPlayers = dungeonConfig.getInt("minPlayers", 1);
            int maxPlayers = dungeonConfig.getInt("maxPlayers", 4);
            int minLevel = dungeonConfig.getInt("minLevel", 1);

            DungeonSetting dungeonSetting = new DungeonSetting(dungeonName, displayName, minPlayers, maxPlayers, minLevel);

            if(dungeonConfig.contains("icon")) {
                String itemId = dungeonConfig.getString("icon.id");
                int customModelId = dungeonConfig.getInt("icon.model", 0);
                ItemStack icon = Util.createItem(Material.getMaterial(itemId.toUpperCase()), "§f创建" + displayName + "§f的队伍", customModelId);
                dungeonSetting.setIcon(icon);
            }

            DataManager.dungeonGroupSetting.put(dungeonName, dungeonSetting);
            return true;
        }
        return false;
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
