package peterHelper.config;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import peterHelper.PeterHelper;
import peterHelper.model.CustomItemInfo;
import peterHelper.model.SuiteInfo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class ConfigManager {
    PeterHelper plugin;
    private String path;
    public HashMap<String, CustomItemInfo> customItemHashMap = new HashMap<>();
    public ArrayList<String> itemIds = new ArrayList<>();
    public ConfigManager(PeterHelper plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        File file = new File(plugin.getDataFolder(), "");
        if(!file.exists())
            file.mkdir();
        file = new File(plugin.getDataFolder(), "/config.yml");
        FileConfiguration config;
        if(!file.exists()) {
            config  = load(file);
            config.set("ItemsAdderPath", "ItemsAdder/data/items_packs/mjy");

            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            loadConfig();
            return;
        }
        config = load(file);
        path = config.getString("ItemsAdderPath", "ItemsAdder/data/items_packs/mjy");
        loadCustomItemConfig();
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

    public void loadCustomItemConfig() {
        File file = new File(plugin.getDataFolder().getParent(), "/" + path);
        if(!file.exists()) {
            Bukkit.getConsoleSender().sendMessage("§a[PeterHelper] §c没有找到ItemsAdder的路径!");
            return;
        }
        Bukkit.getConsoleSender().sendMessage("§a[PeterHelper] §e已找到ItemsAdder的路径!");
        customItemHashMap.clear();
        itemIds.clear();
        int count = 0;
        for(File itemFile: Objects.requireNonNull(file.listFiles())) {
            FileConfiguration config = load(itemFile);
            String namespace = config.getString("info.namespace");
            if(namespace==null)
                continue;
            ConfigurationSection section = config.getConfigurationSection("items");
            for(String key:section.getKeys(false)) {
                int level = 1;
                ConfigurationSection itemSection = section.getConfigurationSection(key);
                if(itemSection.contains("level")) {
                    level = itemSection.getInt("level");
                }

                String id = namespace+":"+key;
                CustomItemInfo customItemInfo = new CustomItemInfo(namespace, id, level);
                SuiteInfo suiteInfo = getSuiteInfo(itemSection);
                if(suiteInfo!=null)
                    customItemInfo.setSuiteInfo(suiteInfo);
                customItemHashMap.put(id, customItemInfo);
                itemIds.add(id);
                if(itemFile.getName().contains("sword")) {
                    config.set("recipes.anvil_repair." + key + ".ingredient", "mjy:zishuijing");
                    config.set("recipes.anvil_repair." + key + ".item", "mjy:"+key);
                } else if(itemFile.getName().contains("armors")) {
                    config.set("recipes.anvil_repair." + key + ".ingredient", "mjy:huangshuijing");
                    config.set("recipes.anvil_repair." + key + ".item", "mjy:"+key);
                }
                count++;
            }
            try {
                config.save(itemFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Bukkit.getConsoleSender().sendMessage("§a[PeterHelper] §e共读取§6" + count + "§e个自定义物品!");
    }

    private SuiteInfo getSuiteInfo(ConfigurationSection itemSection) {
        if(!itemSection.contains("suite")) {
            return null;
        }
        ConfigurationSection suiteSection = itemSection.getConfigurationSection("suite");
        String suiteName = suiteSection.getString("name");
        int particleId = suiteSection.getInt("particle.id", 1) - 1;
        double damage = suiteSection.getDouble("damage", 0);
        double armor = suiteSection.getDouble("armor", 0);
        double maxHealth = suiteSection.getDouble("maxHealth", 0);
        return new SuiteInfo(suiteName, particleId, damage, armor, maxHealth);
    }
}
