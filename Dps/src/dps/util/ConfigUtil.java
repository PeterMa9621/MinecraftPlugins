package dps.util;

import dps.Dps;
import dps.rewardBox.Reward;
import dps.rewardBox.RewardBoxManager;
import dps.rewardBox.RewardTable;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
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
            config.set("normalRewardProb", 0.3);

            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            loadConfig(plugin);
            return;
        }

        config = load(file);
        RewardBoxManager.normalRewardProb = config.getDouble("normalRewardProb", 0.3);

        File dungeonsXLMapDir = new File(plugin.getDataFolder().getParentFile().getAbsolutePath() + "/DungeonsXL/maps");
        int numDungeon = 0;
        int numReward = 0;
        for(File mapDir:dungeonsXLMapDir.listFiles()){
            if(mapDir.getName().equalsIgnoreCase(".raw"))
                continue;
            int num = loadMapConfig(mapDir);
            if(num>=0) {
                numDungeon++;
                numReward+=num;
            }
        }
        Bukkit.getConsoleSender().sendMessage("§a[Dps] §e已加载" + numDungeon + "个副本,共" + numReward + "个奖励");
        loadNormalRewardConfig(plugin);
    }

    private static int loadMapConfig(File file) {
        File configFile = new File(file, "/config.yml");
        if(!configFile.exists())
            return -1;

        String dungeonName = file.getName();
        FileConfiguration config = load(configFile);

        int numReward = 0;
        ConfigurationSection dungeonConfig = config.getConfigurationSection("dps");
        if(dungeonConfig==null)
            return -1;
        String expString = dungeonConfig.getString("exp", "10-100");
        String[] expSplit = expString.split("-");
        int minExp = Integer.parseInt(expSplit[0]);
        int maxExp = Integer.parseInt(expSplit[1]);
        Double bonusRewardProb = dungeonConfig.getDouble("bonusRewardProb", 0.005);
        ArrayList<Reward> rewards = new ArrayList<>();
        for(int i = 0; dungeonConfig.contains(String.valueOf((i+1))); i++){
            String cmd = dungeonConfig.getString((i+1)+".cmd", "");
            double chance = dungeonConfig.getDouble((i+1)+".chance", 0d);
            Material material = Material.getMaterial(dungeonConfig.getString((i+1)+".icon.id", Material.WHITE_WOOL.toString()));
            String displayName = dungeonConfig.getString((i+1)+".icon.displayName", "§e领取奖励");
            int customModelData = dungeonConfig.getInt((i+1)+".icon.model", 1);
            ItemStack itemStack = new ItemStack(material);
            ItemMeta meta = itemStack.getItemMeta();
            meta.setDisplayName(displayName.replaceAll("&", "§"));
            meta.setCustomModelData(customModelData);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            itemStack.setItemMeta(meta);
            Reward reward = new Reward(chance, itemStack, cmd);
            rewards.add(reward);
            numReward ++;
        }
        RewardTable rewardTable = new RewardTable(dungeonName, rewards, minExp, maxExp);
        rewardTable.setBonusRewardProb(bonusRewardProb);
        RewardBoxManager.rewards.put(dungeonName, rewardTable);
        return numReward;
    }

    public static void loadNormalRewardConfig(Dps plugin)
    {
        File file=new File(plugin.getDataFolder(),"item.yml");
        FileConfiguration config;
        if (!file.exists()) {
            Bukkit.getConsoleSender().sendMessage("§6[Dps] §c没有item文件存在，推荐使用DailyQuest生成item文件!");
            config = load(file);
            config.set("rewards", new ArrayList<String>() {{
                add("eco give %player% 100 #DIAMOND");
            }}.toArray());

            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            loadNormalRewardConfig(plugin);
            return;
        }

        RewardBoxManager.normalRewards.clear();
        config = load(file);

        for(String rewardCmd:config.getStringList("rewards")) {
            if(!rewardCmd.contains("#")) {
                Bukkit.getConsoleSender().sendMessage("§6[Dps] §cNormal Rewards Format Error!");
                continue;
            }
            String cmd = rewardCmd.split("#")[0];
            String material = rewardCmd.split("#")[1].toUpperCase();

            Reward reward = new Reward(1, new ItemStack(Material.getMaterial(material)), cmd);
            RewardBoxManager.normalRewards.add(reward);
        }
        Bukkit.getConsoleSender().sendMessage("§6[Dps] §a已加载普通奖励物品§e" + RewardBoxManager.normalRewards.size() + "§a个");
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
