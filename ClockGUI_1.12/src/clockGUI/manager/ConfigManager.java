package clockGUI.manager;

import clockGUI.*;
import clockGUI.Util.Util;
import clockGUI.model.ClockGuiItem;
import clockGUI.model.Function;
import clockGUI.model.Money;
import clockGUI.model.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConfigManager {
    private ClockGUI plugin;
    private File dataFolder;
    private DataManager dataManager;

    public ItemStack clock = new ItemStack(Material.WATCH);

    /**
     *  Key is gui id
     */
    public HashMap<String, String> guiNameList = new HashMap<>();

    /**
     *  Key is gui id
     */
    public HashMap<String, HashMap<Integer, ClockGuiItem>> list = new HashMap<>();

    public List<String> enableWorlds;

    public boolean autoGetClock = false;
    public String mainGuiId;

    public ConfigManager(ClockGUI plugin) {
        this.plugin = plugin;
        dataFolder = plugin.getDataFolder();
        dataManager = plugin.dataManager;
    }

    public void loadPlayerConfig()
    {
        File file=new File(dataFolder,"/Data/player.yml");
        FileConfiguration config;
        if (!file.exists())
        {
            return;
        }

        config = load(file);
        for(String playerName:config.getKeys(false))
        {
            PlayerData playerData = new PlayerData();
            for(String guiNumber:config.getConfigurationSection(playerName).getKeys(false))
            {
                for(String position:config.getConfigurationSection(playerName+"."+guiNumber).getKeys(false))
                {
                    int usedNumber = config.getInt(playerName+"."+guiNumber+"."+position);
                    playerData.setNumber(guiNumber, Integer.parseInt(position), usedNumber);
                }
            }
            dataManager.getPlayerData().put(playerName, playerData);
        }
    }

    public void savePlayerConfig()
    {
        File file = new File(dataFolder, "/Data");
        if(!file.exists())
            file.mkdir();
        file=new File(dataFolder,"/Data/player.yml");
        FileConfiguration config;
        config = load(file);

        HashMap<String, PlayerData> playerData = dataManager.getPlayerData();
        for(String playerName:playerData.keySet())
        {
            for(String guiNumber:playerData.get(playerName).getGuiInfo().keySet())
            {
                for(int position:playerData.get(playerName).getGuiInfo().get(guiNumber).keySet())
                {
                    config.set(playerName+"."+guiNumber+"."+position, playerData.get(playerName).getNumber(guiNumber, position));
                }
            }
        }

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadConfig()
    {
        File file=new File(dataFolder,"config.yml");
        FileConfiguration config;
        if (!file.exists())
        {
            config = load(file);

            // Clock Settings
            config.set("Clock.AutoGetClock", true);
            config.set("Clock.MainGuiId", 0);
            config.set("Clock.Name", "&a钟表菜单");
            config.set("Clock.Lore", new ArrayList<String>() {{
                add("&1我的世界钟表菜单");
                add("&2右键我打开菜单");
            }});
            config.set("Clock.World", new ArrayList<String>() {{
                add("world");
            }}.toArray());

            config.set("GUI.0.Name", "&1我的世界钟表菜单");
            config.set("GUI.0.Item.1.Position", 1);
            config.set("GUI.0.Item.1.ItemID", "CLOCK");
            config.set("GUI.0.Item.1.Model", 0);
            config.set("GUI.0.Item.1.Name", "示例1");
            config.set("GUI.0.Item.1.HideItem", false);
            config.set("GUI.0.Item.1.Lore", new ArrayList<String>() {{
                add("第一行");
                add("第二行");
            }});
            config.set("GUI.0.Item.1.Enchantment.ID", "fortune");
            config.set("GUI.0.Item.1.Enchantment.Level", 1);
            config.set("GUI.0.Item.1.HideEnchant", true);
            config.set("GUI.0.Item.1.HideAttribute", true);
            config.set("GUI.0.Item.1.Cost.Type", "Money");
            config.set("GUI.0.Item.1.Cost.Price", 1000);
            config.set("GUI.0.Item.1.Message", new ArrayList<String>() {{
                add("&a你已按下这个按钮");
                add("&c测试");
            }});
            config.set("GUI.0.Item.1.Frequency", 1);
            config.set("GUI.0.Item.1.Function.OpenAnotherGUI.Use", true);
            config.set("GUI.0.Item.1.Function.OpenAnotherGUI.Id", 1);
            config.set("GUI.0.Item.1.Function.Command.Use", false);
            config.set("GUI.0.Item.1.Function.Command.Content", new ArrayList<String>() {{
                add("say 钟表菜单");
            }});
            config.set("GUI.0.Item.2.Position", 10);
            config.set("GUI.0.Item.2.ItemID", "CLOCK");
            config.set("GUI.0.Item.2.Name", "示例2");
            config.set("GUI.0.Item.2.Lore", new ArrayList<String>() {{
                add("第一行");
                add("第二行");
            }});
            config.set("GUI.0.Item.2.Cost.Type", "PlayerPoints");
            config.set("GUI.0.Item.2.Cost.Price", 500);
            config.set("GUI.0.Item.2.Function.Command.Use", true);
            config.set("GUI.0.Item.2.Function.Command.CloseGui", true);
            config.set("GUI.0.Item.2.Function.Command.Content", new ArrayList<String>() {{
                add("say 钟表菜单");
                add("eco set {player} 10000");
            }});
            config.set("GUI.0.Item.2.Function.Command.RunAsOp", true);
            config.set("GUI.0.Item.2.Function.OpenAnotherGUI.Use", false);
            config.set("GUI.0.Item.2.Function.OpenAnotherGUI.Id", 2);

            // GUI Settings
            config.set("GUI.1.Name", "第一个GUI");
            config.set("GUI.1.Item.1.Position", 3);
            config.set("GUI.1.Item.1.ItemID", "diamond");
            config.set("GUI.1.Item.1.Name", "示例3");
            config.set("GUI.1.Item.1.Lore", new ArrayList<String>() {{
                add("第一行");
                add("第二行");
            }});
            config.set("GUI.1.Item.1.Function.Command.Use", true);
            config.set("GUI.1.Item.1.Function.Command.Content", new ArrayList<String>() {{
                add("say 钟表菜单2");
                add("eco set {player} 10000");
            }});
            config.set("GUI.1.Item.2.Position", 16);
            config.set("GUI.1.Item.2.ItemID", "iron_pickaxe");
            config.set("GUI.1.Item.2.Name", "示例4");
            config.set("GUI.1.Item.2.Lore", new ArrayList<String>() {{
                add("第一行");
                add("第二行");
            }});
            config.set("GUI.1.Item.2.Function.Command.Use", true);
            config.set("GUI.1.Item.2.Function.Command.Content", new ArrayList<String>() {{
                add("say 钟表菜单");
            }});


            try
            {
                config.save(file);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            loadConfig();
            return;
        }

        config = load(file);

        autoGetClock = config.getBoolean("Clock.AutoGetClock");
        mainGuiId = config.getString("Clock.MainGuiId");
        String clockName = config.getString("Clock.Name");
        List<String> clockLore = config.getStringList("Clock.Lore");
        Util.setItem(clock, clockName, clockLore, "WATCH");

        enableWorlds = config.getStringList("Clock.World");

        ConfigurationSection section = config.getConfigurationSection("GUI");
        if(section==null)
            return;
        for(String guiKey:section.getKeys(false)) {
            ConfigurationSection guiSection = section.getConfigurationSection(guiKey);
            String guiName = guiSection.getString("Name");
            if(guiName==null)
                continue;
            guiName = guiName.replace('&', ChatColor.COLOR_CHAR);
            HashMap<Integer, ClockGuiItem> guiList = new HashMap<>();
            ConfigurationSection itemSection = guiSection.getConfigurationSection("Item");
            if(itemSection==null)
                continue;
            for(String itemKey:itemSection.getKeys(false)) {
                ConfigurationSection eachItemSection = itemSection.getConfigurationSection(itemKey);
                String itemID = eachItemSection.getString("ItemID");
                int customModelId = eachItemSection.getInt("Model", 0);
                String itemName = eachItemSection.getString("Name");
                List<String> itemLore = eachItemSection.getStringList("Lore");
                if(itemID==null)
                    continue;
                int enchantID = eachItemSection.getInt("Enchantment.ID", -1);
                int enchantLevel = eachItemSection.getInt("Enchantment.Level");
                boolean hideEnchant = eachItemSection.getBoolean("HideEnchant", true);
                boolean hideAttribute = eachItemSection.getBoolean("HideAttribute", true);
                int position = eachItemSection.getInt("Position", 0);

                boolean hideItem = eachItemSection.getBoolean("HideItem", false);

                boolean openGUI = eachItemSection.getBoolean("Function.OpenAnotherGUI.Use", false);
                String guiId = eachItemSection.getString("Function.OpenAnotherGUI.Id", "0");
                boolean command = eachItemSection.getBoolean("Function.Command.Use", false);
                boolean runAsOp = eachItemSection.getBoolean("Function.Command.RunAsOp", false);
                boolean shouldCloseGui = eachItemSection.getBoolean("Function.Command.CloseGui", false);
                List<String> commandList = eachItemSection.getStringList("Function.Command.Content");
                String costType = eachItemSection.getString("Cost.Type");
                int price = eachItemSection.getInt("Cost.Price");
                List<String> message = eachItemSection.getStringList("Message");
                int frequency = eachItemSection.getInt("Frequency");

                ItemStack item;

                item = Util.createItem(itemID, 1, customModelId, itemName, itemLore, hideEnchant, hideAttribute);

                if(enchantID!=-1 && enchantLevel>0) {
                    item.addUnsafeEnchantment(Enchantment.getById(enchantID), enchantLevel);
                }


                Money money = null;
                if(costType!=null)
                {
                    money = new Money(costType, price);
                }
                else
                {
                    money = new Money("Money", 0);
                }


                Function function = null;

                if(command && !openGUI)
                {
                    function = new Function("command", commandList, shouldCloseGui, runAsOp);
                }
                else if(!command && openGUI)
                {
                    function = new Function("gui", guiId, runAsOp);
                }
                else if(command)
                {
                    function = new Function("guiAndCommand", guiId, commandList, runAsOp);
                }
                else {
                    function = new Function("none", null, shouldCloseGui);
                }

                ClockGuiItem guiItem = new ClockGuiItem(item, function, money, message, frequency, hideItem);

                guiList.put(position, guiItem);

            }
            guiNameList.put(guiKey, guiName);
            list.put(guiKey, guiList);
        }

        loadPlayerConfig();
    }

    public void deletePlayerData(String guiId, int position) {
        File file=new File(dataFolder,"/Data/player.yml");
        FileConfiguration config;
        config = load(file);

        for(String playerName:config.getKeys(false))
        {
            config.set(playerName+"."+guiId+"."+position, null);
        }

        HashMap<String, PlayerData> playerData = dataManager.getPlayerData();
        for(String playerName:playerData.keySet())
        {
            playerData.get(playerName).setNumber(guiId, position, 0);
        }

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
}
