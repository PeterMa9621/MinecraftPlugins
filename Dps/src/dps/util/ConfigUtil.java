package dps.util;

import dps.Dps;
import dps.rewardBox.Reward;
import dps.rewardBox.RewardBoxManager;
import dps.rewardBox.RewardTable;
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
import java.util.HashMap;

public class ConfigUtil {
    public static void loadConfig(Dps plugin) {
        File file = new File(plugin.getDataFolder(), "/config.yml");
        FileConfiguration config;
        if(!file.exists()){
            config = load(file);
            for(int i=0; i<3; i++){
                config.set("test"+(i+1)+".bonusRewardProb", 0.005);
                for(int j=0; j<4; j++){
                    config.set("test"+(i+1)+"."+(j+1)+".cmd", "give %player% diamond " + j);
                    config.set("test"+(i+1)+"."+(j+1)+".chance", 0.25);
                    config.set("test"+(i+1)+"."+(j+1)+".icon.id", Material.DIAMOND_SWORD.toString());
                    config.set("test"+(i+1)+"."+(j+1)+".icon.displayName", "神剑");
                    config.set("test"+(i+1)+"."+(j+1)+".icon.model", 1);
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
        RewardBoxManager.rewards.clear();
        int numDungeon = 0;
        int numReward = 0;
        for(String dungeonName:config.getKeys(false)){
            numDungeon ++;
            Double bonusRewardProb = config.getDouble(dungeonName+".bonusRewardProb", 0.005);
            ArrayList<Reward> rewards = new ArrayList<>();
            for(int i=0; config.contains(dungeonName+"."+(i+1)); i++){
                numReward ++;
                String cmd = config.getString(dungeonName+"."+(i+1)+".cmd", "");
                double chance = config.getDouble(dungeonName+"."+(i+1)+".chance", 0d);
                Material material = Material.getMaterial(config.getString(dungeonName+"."+(i+1)+".icon.id", Material.WHITE_WOOL.toString()));
                String displayName = config.getString(dungeonName+"."+(i+1)+".icon.displayName", "§e已领取奖励");
                int customModelData = config.getInt(dungeonName+"."+(i+1)+".icon.model", 1);
                ItemStack itemStack = new ItemStack(material);
                ItemMeta meta = itemStack.getItemMeta();
                meta.setDisplayName(displayName.replaceAll("&", "§"));
                meta.setCustomModelData(customModelData);
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                itemStack.setItemMeta(meta);
                Reward reward = new Reward(chance, itemStack, cmd);
                rewards.add(reward);
            }
            RewardTable rewardTable = new RewardTable(dungeonName, rewards);
            rewardTable.setBonusRewardProb(bonusRewardProb);
            RewardBoxManager.rewards.put(dungeonName, rewardTable);
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
