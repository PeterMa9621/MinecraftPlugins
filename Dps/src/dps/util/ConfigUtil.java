package dps.util;

import dps.Dps;
import dps.rewardBox.RewardTable;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class ConfigUtil {
    public static void loadConfig(Dps plugin) {
        File file = new File(plugin.getDataFolder(), "/config.yml");
        FileConfiguration config;
        if(!file.exists()){
            config = load(file);
            for(int i=0; i<3; i++){
                for(int j=0; j<4; j++){
                    config.set("test"+(i+1)+"."+(j+1)+".cmd", "give {{player}} diamond " + j);
                    config.set("test"+(i+1)+"."+(j+1)+".chance", 0.25);
                }
            }

            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            loadConfig(plugin);
            return;
        }

        config = load(file);
        int numDungeon = 0;
        int numReward = 0;
        for(String dungeonName:config.getKeys(false)){
            numDungeon ++;
            HashMap<String, Double> rewards = new HashMap<>();
            for(int i=0; config.contains(dungeonName+"."+(i+1)); i++){
                numReward ++;
                String cmd = config.getString(dungeonName+"."+(i+1)+".cmd", "");
                double chance = config.getDouble(dungeonName+"."+(i+1)+".chance", 0d);
                rewards.put(cmd, chance);
            }
            RewardTable rewardTable = new RewardTable(dungeonName, rewards);
            Dps.rewards.put(dungeonName, rewardTable);
        }
        Bukkit.getConsoleSender().sendMessage("§a[Dps] §e已加载" + numDungeon + "个副本,共" + numReward + "个奖励");
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
