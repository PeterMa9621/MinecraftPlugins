package dailyQuest.config;

import com.google.gson.JsonObject;
import dailyQuest.DailyQuest;
import dailyQuest.model.Quest;
import dailyQuest.model.QuestInfo;
import dailyQuest.model.QuestPlayer;
import dailyQuest.manager.QuestManager;
import dailyQuest.util.IconType;
import dailyQuest.util.Util;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import peterUtil.database.Database;
import peterUtil.database.DatabaseType;
import peterUtil.database.StorageInterface;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

public class ConfigManager {
    private DailyQuest plugin;

    public ArrayList<Integer> npcIds = new ArrayList<Integer>();

    public List<String> rewards = new ArrayList<>();
    public static HashMap<String, Integer> group = new HashMap<String, Integer>();

    public SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");

    private static DatabaseType databaseType = DatabaseType.YML;
    private static StorageInterface database;
    private String databaseName;

    public static int defaultDailyLimit = 0;

    public static double chanceGetReward;

    public int randomItemMaxQuantity = 1;

    public int additionMoney = 10;

    public int extraRewardItemQuantity = 1;

    public int getQuestNPCId = 0;
    private List<Integer> availableNPC;

    private List<Material> itemBlackList = new ArrayList<>();
    private List<String> itemWildcardBlackList = new ArrayList<>();
    private List<EntityType> mobBlackList = new ArrayList<>();
    private List<String> mobWildcardBlackList = new ArrayList<>();

    public boolean enableCommandGetQuest = false;

    private JSONObject language;

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
        database.connect(databaseName, "daily_quest" , "root", "mjy159357", createTableQuery);
    }

    public QuestPlayer loadPlayerConfig(Player player) {
        String playerName = player.getName();
        if(plugin.questPlayerManager.containPlayer(player)) {
            QuestPlayer questPlayer = plugin.questPlayerManager.getQuestPlayer(player);
            questPlayer.setPlayer(player);
            questPlayer.setUpBossBar();
            return questPlayer;
        }

        int currentQuestNumber = 0;
        int currentQuestIndex = -1;
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
                lastLogout = (String) result.get("last_logout");
                currentQuestIndex = (int) result.get("current_quest_index");
                if(lastLogout!=null && lastLogout.equalsIgnoreCase(date.format(new Date()))){
                    currentQuestNumber = (int) result.get("current_quest_number");
                    totalQuestNumber = (int) result.get("total_quest_number");
                } else {
                    // If the player's last logout date is not today, and if the player is still doing a quest,
                    // then reset the current number of player's quest to 1
                    // Otherwise, set to 0
                    if(currentQuestIndex!=-1) {
                        currentQuestNumber = 1;
                    }
                }
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
        if (!file.exists()) {
            generateItems();
            return;
        }
        config = load(file);

        rewards.clear();

        for(String reward:config.getStringList("rewards")) {
            rewards.add(reward.split("#")[0]);
        }
        Bukkit.getConsoleSender().sendMessage("§6[日常任务] §a已加载物品§e" + rewards.size() + "§a个");
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

            config.set("AvailableNPC", new int[] {9,10,11,12,13,14});

            config.set("ItemBlackList", new ArrayList<String>() {{
                add("ARMOR_STAND");
                add("BAT_SPAWN_EGG");
            }}.toArray());

            config.set("ItemWildcardBlackList", new ArrayList<String>() {{
                add("SPAWN_EGG");
                add("COMMAND_BLOCK");
            }}.toArray());

            config.set("MobWildcardBlackList", new ArrayList<String>() {{
            }}.toArray());

            config.set("EnableCommandGetQuest", false);

            config.set("Database", "MYSQL");
            config.set("DatabaseName", "minecraft");
            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            loadConfig();
            return;
        }

        itemBlackList.clear();
        mobBlackList.clear();

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

        availableNPC = config.getIntegerList("AvailableNPC");

        List<String> itemMaterialList = config.getStringList("ItemBlackList");
        itemMaterialList.forEach(str -> {
            itemBlackList.add(Material.getMaterial(str.toUpperCase()));
        });
        List<String> mobEntityTypeList = config.getStringList("MobBlackList");
        mobEntityTypeList.forEach(str -> {
            mobBlackList.add(EntityType.valueOf(str));
        });

        itemWildcardBlackList = config.getStringList("ItemWildcardBlackList");
        mobWildcardBlackList = config.getStringList("MobWildcardBlackList");

        enableCommandGetQuest = config.getBoolean("EnableCommandGetQuest");

        databaseType = DatabaseType.valueOf(config.getString("Database", "YML").toUpperCase());
        databaseName = config.getString("DatabaseName", "minecraft");

        List<String> group = config.getStringList("Group");

        for(String g:group) {
            ConfigManager.group.put(g.split(":")[0], Integer.valueOf(g.split(":")[1]));
        }
        language = loadLanguageFile();
        loadItemConfig();
        loadQuestConfig();
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

    public JSONObject loadLanguageFile() {
        InputStream inputStream = plugin.getResource("chinese");
        InputStreamReader isReader = new InputStreamReader(inputStream);
        //Creating a BufferedReader object
        BufferedReader reader = new BufferedReader(isReader);
        StringBuffer sb = new StringBuffer();
        String str;
        while(true){
            try {
                if ((str = reader.readLine()) == null)
                    break;
                sb.append(str);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        JSONParser parser = new JSONParser();
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) parser.parse(sb.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(jsonObject!=null)
            Bukkit.getConsoleSender().sendMessage("§6[日常任务] §a语言文件加载完毕");
        return jsonObject;
    }

    private boolean canGenerateQuest(Material material, JSONObject language) {
        if(!material.isItem())
            return false;
        String materialName = language.get("item.minecraft."+material.name().toLowerCase()) + "";
        if(materialName.equalsIgnoreCase("null"))
            return false;

        if(itemBlackList.contains(material))
            return false;

        for(String wildcard:itemWildcardBlackList) {
            if(material.name().toLowerCase().contains(wildcard.toLowerCase()))
                return false;
        }
        return true;
    }

    private boolean canGenerateQuest(EntityType entityType, JSONObject language) {
        if(!entityType.isAlive() || !entityType.isSpawnable())
            return false;
        String mobName = language.get("entity.minecraft."+entityType.name().toLowerCase()) + "";
        if(mobName.equalsIgnoreCase("null"))
            return false;
        if(mobBlackList.contains(entityType))
            return false;

        for(String wildcard:mobWildcardBlackList) {
            if(entityType.name().toLowerCase().contains(wildcard.toLowerCase()))
                return false;
        }
        return true;
    }

    private NPC getNPC() {
        int randomIndex = 0;
        if(availableNPC.size()>0)
            randomIndex = Util.random(availableNPC.size());
        int npcId = availableNPC.get(randomIndex);
        //Bukkit.getConsoleSender().sendMessage(randomIndex +",  " + npcId);
        return CitizensAPI.getNPCRegistry().getById(npcId);
    }

    public void loadQuestConfig()
    {
        File itemFile=new File(plugin.getDataFolder(),"quest_item.yml");
        File mobFile=new File(plugin.getDataFolder(),"quest_mob.yml");

        npcIds.clear();
        QuestManager.quests.clear();
        String[] types = new String[] {"物品任务", "怪物任务"};
        int i = 0;
        FileConfiguration config;
        for(File file:new File[] {itemFile, mobFile}) {
            config = load(file);
            int numQuest = 0;
            for(String key:config.getKeys(false))
            {
                String type = config.getString(key+".Type");
                String describe = config.getString(key+".Describe");
                String rewardMessage = config.getString(key+".RewardMessage", "");
                String money = config.getString(key+".Reward.Money");

                // Get the npcID
                int NPCID = config.getInt(key+".NPCID");
                if(!npcIds.contains(NPCID))
                    npcIds.add(NPCID);

                QuestInfo questInfo = null;
                ConfigurationSection itemSection = config.getConfigurationSection(key+".Item");
                ConfigurationSection mobSection = config.getConfigurationSection(key+".Mob");
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
                    numQuest++;
                }
            }
            Bukkit.getConsoleSender().sendMessage("§6[日常任务] §a已加载§e" + numQuest + "§a个§e" + types[i]);
            i++;
        }
        Bukkit.getConsoleSender().sendMessage("§6[日常任务] §a一共加载§e" + QuestManager.quests.size() + "§a个任务");
    }

    public void generateQuests() {
        File itemFile=new File(plugin.getDataFolder(),"quest_item.yml");
        FileConfiguration config;

        config = load(itemFile);

        Material[] materials = Material.values();
        for(Material material:materials) {
            if(!canGenerateQuest(material, language))
                continue;
            NPC npc = getNPC();
            String npcDisplayName = npc.getFullName();
            String describe = "§a%s§f需要§e%d§f个§e%s§f，你快去帮帮他吧！";
            String materialName = language.get("item.minecraft."+material.name().toLowerCase()) + "";
            int numItem = Util.random(2) + 1;
            if(!Util.canItemStack(material))
                numItem = 1;
            config.set(material.name().toUpperCase() + ".Type", "item");
            config.set(material.name().toUpperCase() + ".Describe", String.format(describe, npcDisplayName, numItem, materialName));
            config.set(material.name().toUpperCase() + ".RewardMessage", "§7你完成了任务");

            config.set(material.name().toUpperCase() + ".NPCID", npc.getId());
            config.set(material.name().toUpperCase() + ".Item.ID", material.name().toUpperCase());
            config.set(material.name().toUpperCase() + ".Item.Model", 0);
            config.set(material.name().toUpperCase() + ".Item.Amount", numItem);
            //section.set("Item.Enchantment.ID", "fortune");
            //section.set("Item.Enchantment.Level", 0);
            config.set(material.name().toUpperCase() + ".Reward.Money", "30:100");
        }

        try
        {
            config.save(itemFile);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        Bukkit.getConsoleSender().sendMessage("§6[日常任务] §a已生成物品任务");

        File mobFile=new File(plugin.getDataFolder(),"quest_mob.yml");

        config = load(mobFile);

        config.set("1.Mob.ID", "ZOMBIE");
        config.set("1.Mob.Amount", 2);
        config.set("1.Mob.CustomName", "§e§l狩猎者");
        config.set("1.Reward.Money", "30:50");

        EntityType[] entityTypes = EntityType.values();
        for(EntityType entityType:entityTypes) {
            if(!canGenerateQuest(entityType, language))
                continue;
            NPC npc = getNPC();
            String mobName = language.get("entity.minecraft."+entityType.name().toLowerCase()) + "";
            String npcDisplayName = npc.getFullName();
            String describe = "§a%s§f希望你能去帮他解决§e%d§f个§e%s§f！";
            int numMob = Util.random(4) + 2;
            config.set(entityType.name().toUpperCase() + ".Type", "mob");
            config.set(entityType.name().toUpperCase() + ".NPCID", npc.getId());
            config.set(entityType.name().toUpperCase() + ".Describe", String.format(describe, npcDisplayName, numMob, mobName));
            config.set(entityType.name().toUpperCase() + ".RewardMessage", "§7你完成了任务");
            config.set(entityType.name().toUpperCase() + ".Mob.ID", entityType.name().toUpperCase());

            config.set(entityType.name().toUpperCase() + ".Mob.Amount", numMob);
            //config.set(entityType.name().toUpperCase() + ".Mob.CustomName", "§e§l狩猎者");
            config.set(entityType.name().toUpperCase() + ".Reward.Money", "30:100");
        }

        try
        {
            config.save(mobFile);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        Bukkit.getConsoleSender().sendMessage("§6[日常任务] §a已生成怪物任务");
        loadQuestConfig();
    }

    public void generateItems() {
        File file = new File(plugin.getDataFolder(),"item.yml");
        FileConfiguration config = load(file);
        Material[] materials = Material.values();
        ArrayList<String> cmds = new ArrayList<>();
        int count = 0;
        for(Material material:materials) {
            if(!canGenerateQuest(material, language))
                continue;
            int numItem = Util.random(5) + 1;
            if(!Util.canItemStack(material))
                numItem = 1;
            String itemName = "#" + material.name().toUpperCase() + "#" + language.get("item.minecraft."+material.name().toLowerCase());
            String format = "%-45s %s";
            String cmd = "ph give %player% " + material.toString() + " " + numItem;
            cmds.add(String.format(format, cmd, itemName));
            count++;
        }
        config.set("rewards", cmds.toArray());
        Bukkit.getConsoleSender().sendMessage("§6[日常任务] §a已生成物品§e" + count + "§a个");

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        loadItemConfig();
    }

    public void closeDatabase() {
        database.close();
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
