package clockGUI.manager;

import clockGUI.*;
import clockGUI.Util.Util;
import clockGUI.model.ClockGuiItem;
import clockGUI.model.Function;
import clockGUI.model.Money;
import clockGUI.model.PlayerData;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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

    public ItemStack clock = new ItemStack(Material.CLOCK);

    public HashMap<Integer, String> guiNameList = new HashMap<Integer, String>();

    public HashMap<Integer, HashMap<Integer, ClockGuiItem>> list = new HashMap<Integer, HashMap<Integer, ClockGuiItem>>();


    public List<String> enableWorlds;

    public boolean autoGetClock = false;
    public int dungeonOpenGui = -1;

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
                    playerData.setNumber(Integer.parseInt(guiNumber), Integer.parseInt(position), usedNumber);
                }
            }
            dataManager.getPlayerData().put(playerName, playerData);
        }
    }

    public void savePlayerConfig()
    {
        File file=new File(dataFolder,"/Data/player.yml");
        FileConfiguration config;
        config = load(file);

        HashMap<String, PlayerData> playerData = dataManager.getPlayerData();
        for(String playerName:playerData.keySet())
        {
            for(int guiNumber:playerData.get(playerName).getGuiInfo().keySet())
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
            config.set("Clock.Name", "§a钟表菜单");
            config.set("Clock.Lore", "§1我的世界钟表菜单%§2右键我打开菜单");
            config.set("Clock.World", new ArrayList<String>() {{
                add("world");
            }}.toArray());
            config.set("Clock.DungeonOpen", 4);

            config.set("GUI.0.Name", "§1我的世界钟表菜单");
            config.set("GUI.0.Item.1.Position", 1);
            config.set("GUI.0.Item.1.ItemID", "CLOCK");
            config.set("GUI.0.Item.1.Model", 0);
            config.set("GUI.0.Item.1.Name", "示例1");
            config.set("GUI.0.Item.1.HideItem", false);
            config.set("GUI.0.Item.1.Lore", "这是第一行%这是第二行%这是第三行");
            config.set("GUI.0.Item.1.Enchantment.ID", "fortune");
            config.set("GUI.0.Item.1.Enchantment.Level", 1);
            //config.set("GUI.0.Item.1.HideEnchant", true);
            config.set("GUI.0.Item.1.Cost.Type", "Money");
            config.set("GUI.0.Item.1.Cost.Price", 1000);
            config.set("GUI.0.Item.1.BanInDungeon", true);
            config.set("GUI.0.Item.1.Message", "§a你已按下这个按钮%§c测试");
            config.set("GUI.0.Item.1.Frequency", 1);
            config.set("GUI.0.Item.1.Function.OpenAnotherGUI.Use", true);
            config.set("GUI.0.Item.1.Function.OpenAnotherGUI.Number", 1);
            config.set("GUI.0.Item.1.Function.Command.Use", false);
            config.set("GUI.0.Item.1.Function.Command.Content", "/say 钟表菜单");
            config.set("GUI.0.Item.2.Position", 10);
            config.set("GUI.0.Item.2.ItemID", "CLOCK");
            config.set("GUI.0.Item.2.Name", "示例2");
            config.set("GUI.0.Item.2.Lore", "这是第一行%这是第二行%这是第三行");
            config.set("GUI.0.Item.2.Cost.Type", "PlayerPoints");
            config.set("GUI.0.Item.2.Cost.Price", 500);
            config.set("GUI.0.Item.2.Function.Command.Use", true);
            config.set("GUI.0.Item.2.Function.Command.CloseGui", true);
            config.set("GUI.0.Item.2.Function.Command.Content", "/say 钟表菜单%/eco set {player} 10000{ignoreOP}");
            config.set("GUI.0.Item.2.Function.OpenAnotherGUI.Use", false);
            config.set("GUI.0.Item.2.Function.OpenAnotherGUI.Number", 2);
            config.set("GUI.0.Item.2.Function.OpenAnotherGUI.Number", 2);

            // GUI Settings
            config.set("GUI.1.Name", "第一个GUI");
            config.set("GUI.1.Item.1.Position", 3);
            config.set("GUI.1.Item.1.ItemID", "diamond");
            config.set("GUI.1.Item.1.Name", "示例3");
            config.set("GUI.1.Item.1.Lore", "这是第一行%这是第二行%这是第三行");
            config.set("GUI.1.Item.1.Function.Command.Use", true);
            config.set("GUI.1.Item.1.Function.Command.Content", "/say 另一个GUI");
            config.set("GUI.1.Item.2.Position", 16);
            config.set("GUI.1.Item.2.ItemID", "iron_pickaxe");
            config.set("GUI.1.Item.2.Name", "示例4");
            config.set("GUI.1.Item.2.Lore", "这是第一行%这是第二行%这是第三行");
            config.set("GUI.1.Item.2.Function.Command.Use", true);
            config.set("GUI.1.Item.2.Function.Command.Content", "/say 钟表菜单");


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

        String clockName = config.getString("Clock.Name");
        ArrayList<String> clockLore = new ArrayList<String>();
        for(String i:config.getString("Clock.Lore").split("%"))
        {
            clockLore.add(i);
        }
        Util.setItem(clock, clockName, clockLore, "CLOCK");
        dungeonOpenGui = config.getInt("Clock.DungeonOpen", -1);
        enableWorlds = config.getStringList("Clock.World");

        for(int i=0; config.contains("GUI."+i); i++)
        {
            String GUIName = config.getString("GUI."+i+".Name");

            HashMap<Integer, ClockGuiItem> guiList = new HashMap<Integer, ClockGuiItem>();

            for(int x=0; config.contains("GUI."+i+".Item."+(x+1)); x++)
            {
                String enchantID = config.getString("GUI."+i+".Item."+(x+1)+".Enchantment.ID");
                int enchantLevel = config.getInt("GUI."+i+".Item."+(x+1)+".Enchantment.Level");
                boolean hide = config.getBoolean("GUI."+i+".Item."+(x+1)+".HideEnchant");

                int position = config.getInt("GUI."+i+".Item."+(x+1)+".Position");
                String itemID = config.getString("GUI."+i+".Item."+(x+1)+".ItemID");
                int customModelId = config.getInt("GUI."+i+".Item."+(x+1)+".Model", 0);
                boolean hideItem = config.getBoolean("GUI."+i+".Item."+(x+1)+".HideItem", false);
                String itemName = config.getString("GUI."+i+".Item."+(x+1)+".Name");
                String itemLore = config.getString("GUI."+i+".Item."+(x+1)+".Lore", "");
                boolean openGUI = config.getBoolean("GUI."+i+".Item."+(x+1)+".Function.OpenAnotherGUI.Use");
                int guiNumber = config.getInt("GUI."+i+".Item."+(x+1)+".Function.OpenAnotherGUI.Number");
                boolean command = config.getBoolean("GUI."+i+".Item."+(x+1)+".Function.Command.Use");
                boolean shouldCloseGui = config.getBoolean("GUI."+i+".Item."+(x+1)+".Function.Command.CloseGui", false);
                String commandContent = config.getString("GUI."+i+".Item."+(x+1)+".Function.Command.Content");
                String costType = config.getString("GUI."+i+".Item."+(x+1)+".Cost.Type");
                int price = config.getInt("GUI."+i+".Item."+(x+1)+".Cost.Price");
                String message = config.getString("GUI."+i+".Item."+(x+1)+".Message");
                int frequency = config.getInt("GUI."+i+".Item."+(x+1)+".Frequency");
                boolean banInDungeon = config.getBoolean("GUI."+i+".Item."+(x+1)+".BanInDungeon", false);

                ArrayList<String> commandList = new ArrayList<String>();
                ItemStack item = null;

                item = Util.createItem(itemID, 1, customModelId, itemName, itemLore);


                if(enchantID!=null && enchantLevel>0)
                    item.addUnsafeEnchantment(Enchantment.getByKey(NamespacedKey.minecraft(enchantID)), enchantLevel);

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
                if(commandContent==null)
                    commandContent="say 空命令";
                for(String everyCommand:commandContent.split("%"))
                {
                    commandList.add(everyCommand);
                }

                if(command==true && openGUI==false)
                {
                    function = new Function("command", commandList, shouldCloseGui);
                }
                else if(command==false && openGUI==true)
                {
                    function = new Function("gui", guiNumber);
                }
                else if(command==true && openGUI==true)
                {
                    function = new Function("guiAndCommand", guiNumber, commandList);
                }
                else if(command==false && openGUI==false)
                {
                    function = new Function("none", null, shouldCloseGui);
                }

                ClockGuiItem guiItem = new ClockGuiItem(item, function, money, message, frequency, hideItem);

                guiList.put(position, guiItem);

            }
            guiNameList.put(i, GUIName);
            list.put(i, guiList);
        }

        loadPlayerConfig();
    }

    public void deletePlayerData(int guiNumber, int position) {
        File file=new File(dataFolder,"/Data/player.yml");
        FileConfiguration config;
        config = load(file);

        for(String playerName:config.getKeys(false))
        {
            config.set(playerName+"."+guiNumber+"."+position, null);
        }

        HashMap<String, PlayerData> playerData = dataManager.getPlayerData();
        for(String playerName:playerData.keySet())
        {
            playerData.get(playerName).setNumber(guiNumber, position, 0);
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
