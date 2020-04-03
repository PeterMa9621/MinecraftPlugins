package com.peter.worldBoss.config;

import com.peter.worldBoss.WorldBoss;
import com.peter.worldBoss.model.BossGroup;
import com.peter.worldBoss.model.BossGroupSetting;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ConfigManager {
    public static void loadConfig(WorldBoss plugin) {
        File file = new File(plugin.getDataFolder(), "/config.yml");
        FileConfiguration config;
        if(!file.exists()){
            config = load(file);
            for(int i=0; i<3; i++){
                config.set("group"+(i+1) + ".name", "boss" + (i+1));
                config.set("group"+(i+1) + ".displayName", "Boss No." + (i+1));
                config.set("group"+(i+1) + ".startGameCmd", "dxl start boss");
                if(i!=1)
                    config.set("group"+(i+1) + ".day", (i+1));
                config.set("group"+(i+1) + ".time", "15:50");
            }

            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            loadConfig(plugin);
            return;
        }

        plugin.bossGroupSetting.clear();
        plugin.bossGroups.clear();
        config = load(file);

        int numGroup = 0;
        for(String group:config.getKeys(false)){
            String name = config.getString(group + ".name");
            String displayName = config.getString(group + ".displayName", "");
            String startGameCmd = config.getString(group + ".startGameCmd");
            if(startGameCmd==null)
                throw new NullPointerException();
            int day = config.getInt(group + ".day", 0);
            if(day > 7){
                day = 7;
            }

            String startTime = config.getString(group + ".time");
            BossGroupSetting bossGroupSetting = new BossGroupSetting(name, startTime, day, startGameCmd);
            bossGroupSetting.setDisplayName(displayName);
            plugin.bossGroupSetting.put(name, bossGroupSetting);
            numGroup ++;
        }
        Bukkit.getConsoleSender().sendMessage("§a[WorldBoss] §e已加载" + numGroup + "个Boss队伍");
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
