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
            config.set("sample", "wait for update");

            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            loadConfig(plugin);
            return;
        }

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
        Bukkit.getConsoleSender().sendMessage("��a[Dps] ��e�Ѽ���" + numDungeon + "������,��" + numReward + "������");
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
            String displayName = dungeonConfig.getString((i+1)+".icon.displayName", "��e��ȡ����");
            int customModelData = dungeonConfig.getInt((i+1)+".icon.model", 1);
            ItemStack itemStack = new ItemStack(material);
            ItemMeta meta = itemStack.getItemMeta();
            meta.setDisplayName(displayName.replaceAll("&", "��"));
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

    public static FileConfiguration load(File file)
    {
        if (!(file.exists()))
        { //�����ļ�������
            try   //��׽�쳣����Ϊ�п��ܴ������ɹ�
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
