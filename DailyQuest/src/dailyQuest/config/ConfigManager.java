package dailyQuest.config;

import dailyQuest.DailyQuest;
import dailyQuest.model.Quest;
import dailyQuest.model.QuestInfo;
import dailyQuest.model.QuestPlayer;
import dailyQuest.manager.QuestManager;
import dailyQuest.util.IconType;
import dailyQuest.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import peterUtil.database.Database;
import peterUtil.database.DatabaseType;
import peterUtil.database.StorageInterface;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConfigManager {
    private DailyQuest plugin;

    public ArrayList<Integer> npcIds = new ArrayList<Integer>();

    public List<String> rewards;
    public static HashMap<String, Integer> group = new HashMap<String, Integer>();

    public SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");

    private static DatabaseType databaseType = DatabaseType.YML;
    private static StorageInterface database;

    public static int defaultDailyLimit = 0;

    public static double chanceGetReward;

    public int randomItemMaxQuantity = 1;

    public int additionMoney = 10;

    public int extraRewardItemQuantity = 1;

    public int getQuestNPCId = 0;

    public boolean enableCommandGetQuest = false;

    private String[] guiIconType = {"NPC", "GiveUp", "GoBack", "WhatIsDailyQuest", "Accept", "FinishQuest"};

    public HashMap<IconType, ItemStack> guiIcons = new HashMap<>();

    public ConfigManager(DailyQuest plugin) {
        this.plugin = plugin;
    }

    public void initDatabase() {
        if(databaseType.equals(DatabaseType.YML))
            return;
        database = Database.getInstance(databaseType, plugin);
        String createTableQuery = "create table if not exists daily_quest(id varchar(100), name varchar(100), current_quest_number int, current_quest_index int," +
                "total_quest_number int, last_logout varchar(100), primary key(id));";
        database.connect("minecraft", "daily_quest" , "root", "mjy159357", createTableQuery);
    }

    public QuestPlayer loadPlayerConfig(Player player) {
        String playerName = player.getName();
        if(plugin.questPlayerManager.containPlayer(player)) {
            QuestPlayer questPlayer = plugin.questPlayerManager.getQuestPlayer(player);
            questPlayer.setPlayer(player);
            return questPlayer;
        }


        int currentQuestNumber = 0;
        int currentQuestIndex = 0;
        int totalQuestNumber = 0;
        String lastLogout = "0000-00-00";
        if(databaseType.equals(DatabaseType.YML)) {
            File file=new File(plugin.getDataFolder(), "/Data/" + playerName+ ".yml");

            FileConfiguration config;
            if(file.exists()) {
                config = load(file);
                currentQuestNumber = config.getInt("CurrentQuestNumber");
                currentQuestIndex = config.getInt("CurrentQuestIndex");
                totalQuestNumber = config.getInt("TotalQuestNumber");
                lastLogout = config.getString("LastLogout");
            }
        } else {
            HashMap<String, Object> result = database.get(player.getUniqueId(), new String[] {"name", "current_quest_number", "current_quest_index", "total_quest_number", "last_logout"});
            if(result!=null){
                currentQuestNumber = (int) result.get("current_quest_number");
                currentQuestIndex = (int) result.get("current_quest_index");
                totalQuestNumber = (int) result.get("total_quest_number");
                lastLogout = (String) result.get("last_logout");
            }
        }

        QuestPlayer questPlayer = new QuestPlayer(player, currentQuestNumber, currentQuestIndex, totalQuestNumber, lastLogout);
        plugin.questPlayerManager.addPlayer(questPlayer);
        return questPlayer;
    }

    public void savePlayerConfig(QuestPlayer questPlayer) throws IOException {
        Player player = questPlayer.getPlayer();
        if(databaseType.equals(DatabaseType.YML)) {
            String playerName = player.getName();
            File file=new File(plugin.getDataFolder(),"Data/" + playerName+ ".yml");
            FileConfiguration config;

            config = load(file);

            config.set("LastLogout", questPlayer.getLastLogout());
            config.set("CurrentQuestNumber", questPlayer.getCurrentNumber());
            config.set("CurrentQuestIndex", questPlayer.getWhatTheQuestIs());
            config.set("TotalQuestNumber", questPlayer.getTotalQuest());

            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            HashMap<String, Object> data = new HashMap<String, Object>() {{
                put("name", player.getName());
                put("current_quest_number", questPlayer.getCurrentNumber());
                put("current_quest_index", questPlayer.getWhatTheQuestIs());
                put("total_quest_number", questPlayer.getTotalQuest());
                put("last_logout", questPlayer.getLastLogout());
            }};

            database.store(player.getUniqueId(), data);
        }
    }

    public void loadItemConfig()
    {
        File file=new File(plugin.getDataFolder(),"item.yml");
        FileConfiguration config;
        if (!file.exists())
        {
            config = load(file);
            config.set("rewards", new ArrayList<String>() {{
                add("eco give %player% 100");
                add("eco give %player% 200");
            }}.toArray());

            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            loadItemConfig();
        }
        config = load(file);

        rewards = config.getStringList("rewards");
    }

    public void loadConfig()
    {
        File file=new File(plugin.getDataFolder(),"config.yml");
        FileConfiguration config;
        if (!file.exists())
        {
            config = load(file);

            String[] group = {"vip1:30","vip2:50","vip3:70"};

            config.set("DefaultDailyLimit", 20);

            config.set("ChanceGetReward", 0.3);

            config.set("Group", group);

            // Gui icon settings
            config.set("Gui.NPC.Id", "PLAYER_HEAD");
            config.set("Gui.NPC.DisplayName", "&3%name%&a对你说:");
            config.set("Gui.NPC.Lore", new ArrayList<String>() {{
                add("&5你找我有什么事吗?");
            }}.toArray());
            config.set("Gui.NPC.Model", 0);

            config.set("Gui.GiveUp.Id", "PLAYER_HEAD");
            config.set("Gui.GiveUp.DisplayName", "&3%name%&a对你说:");
            config.set("Gui.GiveUp.Lore", new ArrayList<String>() {{
                add("&5你找我有什么事吗?");
            }}.toArray());
            config.set("Gui.GiveUp.Model", 0);

            config.set("Gui.GoBack.Id", "PLAYER_HEAD");
            config.set("Gui.GoBack.DisplayName", "&3%name%&a对你说:");
            config.set("Gui.GoBack.Lore", new ArrayList<String>() {{
                add("&5你找我有什么事吗?");
            }}.toArray());
            config.set("Gui.GoBack.Model", 0);

            config.set("Gui.WhatIsDailyQuest.Id", "PLAYER_HEAD");
            config.set("Gui.WhatIsDailyQuest.DisplayName", "&3%name%&a对你说:");
            config.set("Gui.WhatIsDailyQuest.Lore", new ArrayList<String>() {{
                add("&5你找我有什么事吗?");
            }}.toArray());
            config.set("Gui.WhatIsDailyQuest.Model", 0);

            config.set("Gui.Accept.Id", "PLAYER_HEAD");
            config.set("Gui.Accept.DisplayName", "&3%name%&a对你说:");
            config.set("Gui.Accept.Lore", new ArrayList<String>() {{
                add("&5你找我有什么事吗?");
            }}.toArray());
            config.set("Gui.Accept.Model", 0);

            config.set("Gui.FinishQuest.Id", "PLAYER_HEAD");
            config.set("Gui.FinishQuest.DisplayName", "&3%name%&a对你说:");
            config.set("Gui.FinishQuest.Lore", new ArrayList<String>() {{
                add("&5你找我有什么事吗?");
            }}.toArray());
            config.set("Gui.FinishQuest.Model", 0);
            //

            config.set("RandomItemMaxQuantity", 3);
            config.set("SeriesFinish.AdditionMoney", 100);
            config.set("SeriesFinish.ExtraRewardItemQuantity", 1);

            config.set("GetQuestNPCId", 9);

            config.set("EnableCommandGetQuest", false);

            config.set("Database", "MYSQL");
            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            loadConfig();
            return;
        }

        config = load(file);
        ConfigurationSection guiSection = config.getConfigurationSection("Gui");
        if(guiSection!=null) {
            for(String type:guiIconType) {
                ConfigurationSection section = guiSection.getConfigurationSection(type);
                assert section != null;
                String id = section.getString("Id").toUpperCase();
                String displayName = section.getString("DisplayName").replace("&","§");
                ArrayList<String> lore = new ArrayList<String>() {{
                    for(String eachLore:section.getStringList("Lore")){
                        add(eachLore.replace("&","§"));
                    }
                }};

                int customModelId = section.getInt("Model", 0);
                ItemStack icon = new ItemStack(Material.getMaterial(id));
                ItemMeta itemMeta = icon.getItemMeta();
                itemMeta.setDisplayName(displayName);
                itemMeta.setLore(lore);
                itemMeta.setCustomModelData(customModelId);
                icon.setItemMeta(itemMeta);
                guiIcons.put(IconType.valueOf(type), icon);
            }
        }

        defaultDailyLimit = config.getInt("DefaultDailyLimit", 20);

        chanceGetReward = config.getDouble("ChanceGetReward", 0.3);

        randomItemMaxQuantity = config.getInt("RandomItemMaxQuantity");

        additionMoney = config.getInt("SeriesFinish.AdditionMoney");

        extraRewardItemQuantity = config.getInt("SeriesFinish.ExtraRewardItemQuantity");

        getQuestNPCId = config.getInt("GetQuestNPCId");

        enableCommandGetQuest = config.getBoolean("EnableCommandGetQuest");

        databaseType = DatabaseType.valueOf(config.getString("Database", "YML").toUpperCase());

        List<String> group = config.getStringList("Group");

        for(String g:group) {
            ConfigManager.group.put(g.split(":")[0], Integer.valueOf(g.split(":")[1]));
        }
    }

    public void saveConfig()
    {
        File file=new File(plugin.getDataFolder(),"config.yml");
        if(file.exists()) {
            FileConfiguration config;

            config = load(file);

            config.set("GetQuestNPCId", getQuestNPCId);

            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void loadQuestConfig()
    {
        File file=new File(plugin.getDataFolder(),"quest.yml");
        FileConfiguration config;
        if (!file.exists())
        {
            config = load(file);

            config.set("Quest.1.Type", "mob");
            config.set("Quest.1.Describe", "§a请前往坐标(-344,100,256)附近上交石头10个");
            config.set("Quest.1.RewardMessage", "§7你完成了任务");
            config.set("Quest.1.NPCID", 9);
            config.set("Quest.1.Item.ID", "STONE");
            config.set("Quest.1.Item.Amount", 10);
            config.set("Quest.1.Item.Name", "&2AAA");
            config.set("Quest.1.Item.Lore", new ArrayList<String>() {{
                add("&6AAA");
                add("&5BBB");
            }});
            config.set("Quest.1.Item.Enchantment.ID", "fortune");
            config.set("Quest.1.Item.Enchantment.Level", 1);
            config.set("Quest.1.Mob.ID", "ZOMBIE");
            config.set("Quest.1.Mob.Amount", 2);
            config.set("Quest.1.Mob.CustomName", "§e§l狩猎者");
            config.set("Quest.1.Reward.Money", "30:50");

            for(int i=1; i<10; i++) {
                config.set("Quest."+(i+1)+".Type", "item");
                config.set("Quest."+(i+1)+".Describe", "§a请前往坐标(-344,100,256)附近上交石头10个");
                config.set("Quest."+(i+1)+".RewardMessage", "§7你完成了任务");
                config.set("Quest."+(i+1)+".NPCID", 9);
                config.set("Quest."+(i+1)+".Item.ID", "DIAMOND");
                config.set("Quest."+(i+1)+".Item.Model", 1);
                config.set("Quest."+(i+1)+".Item.Amount", 10);
                config.set("Quest."+(i+1)+".Item.Enchantment.ID", "fortune");
                config.set("Quest."+(i+1)+".Item.Enchantment.Level", 0);
                config.set("Quest."+(i+1)+".Reward.Money", "50:100");
            }

            try
            {
                config.save(file);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            loadQuestConfig();
        }

        npcIds.clear();
        QuestManager.quests.clear();

        config = load(file);

        for(int i=0; config.contains("Quest."+(i+1)); i++)
        {
            String type = config.getString("Quest."+(i+1)+".Type");
            String describe = config.getString("Quest."+(i+1)+".Describe");
            String rewardMessage = config.getString("Quest."+(i+1)+".RewardMessage", "");
            String money = config.getString("Quest."+(i+1)+".Reward.Money");

            // Get the npcID
            int NPCID = config.getInt("Quest."+(i+1)+".NPCID");
            if(!npcIds.contains(NPCID))
                npcIds.add(NPCID);

            QuestInfo questInfo = null;
            ConfigurationSection itemSection = config.getConfigurationSection("Quest."+(i+1)+".Item");
            ConfigurationSection mobSection = config.getConfigurationSection("Quest."+(i+1)+".Mob");
            if(itemSection!=null) {
                String itemID = itemSection.getString("ID");
                int customModelId = itemSection.getInt("Model", 0);

                int amount = itemSection.getInt("Amount", 1);
                String name = itemSection.getString("Name");
                List<String> lore = itemSection.getStringList("Lore");
                String enchantID = itemSection.getString("Enchantment.ID", "").toLowerCase();
                int enchantLevel = itemSection.getInt("Enchantment.Level");

                // Get the quest item
                ItemStack item = Util.createItem(itemID, amount, name, lore, customModelId);

                if(!enchantID.equalsIgnoreCase("") && enchantLevel>0)
                    item.addUnsafeEnchantment(Enchantment.getByKey(NamespacedKey.minecraft(enchantID)), enchantLevel);

                questInfo = new QuestInfo(type, item);
            } else if(mobSection!=null) {
                String mobID = mobSection.getString("ID");
                int mobAmount = mobSection.getInt("Amount");
                String mobName = mobSection.getString("CustomName");
                if(mobName==null)
                    questInfo = new QuestInfo(type, mobID, mobAmount);
                else
                    questInfo = new QuestInfo(type, mobID, mobAmount, mobName);
            }

            // Save data in QuestInfo class
            if(questInfo!=null) {
                Quest quest = new Quest(questInfo, NPCID, money, describe, rewardMessage);
                QuestManager.quests.add(quest);
            }
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
