package com.peter.worldBoss.config;

import com.peter.worldBoss.WorldBoss;
import com.peter.worldBoss.manager.BossGroupManager;
import com.peter.worldBoss.model.BossGroup;
import com.peter.worldBoss.model.BossGroupSetting;
import com.peter.worldBoss.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {
    public static ItemStack leaveGroupIcon;
    public static int ipPort;
    public static String destServer;

    public static void loadConfig(WorldBoss plugin) {
        File file = new File(plugin.getDataFolder(), "/config.yml");
        FileConfiguration config;
        if(!file.exists()){
            config = load(file);
            config.set("minuteBefore", 10);
            config.set("leaveGroup.icon.id", "DIAMOND_SWORD");
            config.set("leaveGroup.icon.model", 16);
            for(int i=0; i<3; i++){
                config.set("groups.group"+(i+1) + ".name", "boss" + (i+1));
                config.set("groups.group"+(i+1) + ".displayName", "Boss No." + (i+1));
                config.set("groups.group"+(i+1) + ".icon.id", "DIAMOND_SWORD");
                config.set("groups.group"+(i+1) + ".icon.model", 0);
                config.set("groups.group"+(i+1) + ".startGameCmd", "dxl start boss");
                if(i!=1)
                    config.set("group"+(i+1) + ".day", new int[] {1,2,3,4,5});
                config.set("group"+(i+1) + ".time", "15:50");
            }

            config.set("destServer", "dungeon");
            config.set("bungeecord.address.dungeonServerPort", 25595);

            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            loadConfig(plugin);
            return;
        }

        for(BossGroupSetting setting: BossGroupManager.bossGroupSetting.values()){
            setting.stopTasks();
        }

        BossGroupManager.bossGroupSetting.clear();
        BossGroupManager.bossGroups.clear();
        config = load(file);

        String leaveGroupIconId = config.getString("leaveGroup.icon.id", "DIAMOND_SWORD").toUpperCase();
        int leaveGroupIconModelId = config.getInt("leaveGroup.icon.model", 0);

        if(leaveGroupIconModelId > 0){
            leaveGroupIcon = Util.createItem(Material.getMaterial(leaveGroupIconId), "", leaveGroupIconModelId);
        } else {
            leaveGroupIcon = Util.createItem(Material.getMaterial(leaveGroupIconId), "");
        }

        int minuteBefore = config.getInt("minuteBefore", 10);
        int numGroup = 0;

        ConfigurationSection section = config.getConfigurationSection("groups");
        assert section != null;
        for(String group:section.getKeys(false)){
            String name = section.getString(group + ".name");
            String displayName = section.getString(group + ".displayName", "");
            String startGameCmd = section.getString(group + ".startGameCmd");
            String iconId = section.getString(group + ".icon.id", "DIAMOND_SWORD").toUpperCase();
            int customModelId = section.getInt(group + ".icon.model", 0);
            ItemStack icon;
            if(leaveGroupIconModelId > 0){
                icon = Util.createItem(Material.getMaterial(iconId), "", customModelId);
            } else {
                icon = Util.createItem(Material.getMaterial(iconId), "");
            }

            if(startGameCmd==null)
                throw new NullPointerException();
            List<Integer> day = section.getIntegerList(group + ".day");

            String startTime = section.getString(group + ".time");
            BossGroupSetting bossGroupSetting = new BossGroupSetting(group, startTime, day, startGameCmd, minuteBefore, icon, plugin);
            bossGroupSetting.setDisplayName(displayName);
            BossGroupManager.bossGroupSetting.put(group, bossGroupSetting);
            numGroup ++;
        }

        destServer = config.getString("destServer", "dungeon");
        ipPort = config.getInt("bungeecord.address.dungeonServerPort", 25595);

        Bukkit.getConsoleSender().sendMessage("§a[世界Boss] §e已加载" + numGroup + "个Boss队伍");
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
}
