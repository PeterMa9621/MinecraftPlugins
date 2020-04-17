package dailyQuest.config;

import dailyQuest.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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
import java.util.*;

public class ConfigManager {
    private DailyQuest plugin;

    ArrayList<Integer> npcID = new ArrayList<Integer>();
    ArrayList<QuestInfo> quests = new ArrayList<QuestInfo>();
    List<String> rewards;
    HashMap<String, Integer> group = new HashMap<String, Integer>();

    HashMap<String, MobQuest> mobQuest = new HashMap<String, MobQuest>();

    SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");

    private static DatabaseType databaseType = DatabaseType.YML;
    private static StorageInterface database;

    int defaultQuantity = 0;

    int randomItemMaxQuantity = 1;

    int additionMoney = 10;

    int extraRewarItemQuantity = 1;

    int getQuestNPCId = 0;

    boolean enableCommandGetQuest = false;

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
        if(plugin.questPlayerManager.containPlayer(player))
            return plugin.questPlayerManager.getQuestPlayer(player);

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

            config.set("DefaultQuantity", 20);

            config.set("Group", group);

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
        }

        config = load(file);

        defaultQuantity = config.getInt("DefaultQuantity");
        randomItemMaxQuantity = config.getInt("RandomItemMaxQuantity");

        additionMoney = config.getInt("SeriesFinish.AdditionMoney");

        extraRewarItemQuantity = config.getInt("SeriesFinish.ExtraRewardItemQuantity");

        getQuestNPCId = config.getInt("GetQuestNPCId");

        enableCommandGetQuest = config.getBoolean("EnableCommandGetQuest");

        databaseType = DatabaseType.valueOf(config.getString("Database", "YML").toUpperCase());

        List<String> group = config.getStringList("Group");

        for(String g:group) {
            this.group.put(g.split(":")[0], Integer.valueOf(g.split(":")[1]));
        }
    }

    public void saveConfig()
    {
        File file=new File(plugin.getDataFolder(),"config.yml");
        FileConfiguration config;

        config = load(file);

        config.set("GetQuestNPCId", getQuestNPCId);

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
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
            config.set("Quest.1.Item.Enchantment.ID", "FORTUNE");
            config.set("Quest.1.Item.Enchantment.Level", 1);
            config.set("Quest.1.Mob.ID", "ZOMBIE");
            config.set("Quest.1.Mob.Amount", 2);
            config.set("Quest.1.Mob.CustomName", "§e§l狩猎者");
            config.set("Quest.1.Reward.Money", 50);

            for(int i=1; i<10; i++) {
                config.set("Quest."+(i+1)+".Type", "item");
                config.set("Quest."+(i+1)+".Describe", "§a请前往坐标(-344,100,256)附近上交石头10个");
                config.set("Quest."+(i+1)+".RewardMessage", "§7你完成了任务");
                config.set("Quest."+(i+1)+".NPCID", 9);
                config.set("Quest."+(i+1)+".Item.ID", "DIAMOND");
                config.set("Quest."+(i+1)+".Item.Model", 1);
                config.set("Quest."+(i+1)+".Item.Amount", 10);
                config.set("Quest."+(i+1)+".Item.Enchantment.ID", -1);
                config.set("Quest."+(i+1)+".Item.Enchantment.Level", 0);
                config.set("Quest."+(i+1)+".Reward.Money", 100);
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

        npcID.clear();
        quests.clear();

        config = load(file);

        for(int i=0; config.contains("Quest."+(i+1)); i++)
        {
            String type = config.getString("Quest."+(i+1)+".Type");
            String describe = config.getString("Quest."+(i+1)+".Describe");
            String rewardMessage = config.getString("Quest."+(i+1)+".RewardMessage");
            int NPCID = config.getInt("Quest."+(i+1)+".NPCID");
            String itemID = config.getString("Quest."+(i+1)+".Item.ID");
            int customModelId = config.getInt("Quest."+(i+1)+".Item.Model", 0);

            int amount = config.getInt("Quest."+(i+1)+".Item.Amount", 1);
            String name = config.getString("Quest."+(i+1)+".Item.Name");
            List<String> lore = config.getStringList("Quest."+(i+1)+".Item.Lore");
            String enchantID = config.getString("Quest."+(i+1)+".Item.Enchantment.ID");
            int enchantLevel = config.getInt("Quest."+(i+1)+".Item.Enchantment.Level");
            String mobID = config.getString("Quest."+(i+1)+".Mob.ID");
            int mobAmount = config.getInt("Quest."+(i+1)+".Mob.Amount");
            int money = config.getInt("Quest."+(i+1)+".Reward.Money");
            String mobName = config.getString("Quest."+(i+1)+".Mob.CustomName");

            // Get the npcID
            if(!npcID.contains(NPCID))
                npcID.add(NPCID);

            // Get the quest item
            ItemStack item = createItem(itemID, amount, name, lore, customModelId);

            if(enchantID!=null && enchantLevel>0)
                item.addUnsafeEnchantment(Enchantment.getByKey(NamespacedKey.minecraft(enchantID)), enchantLevel);

            // Save data in Quest class
            Quest quest;
            if(mobID!=null && mobName==null)
                quest = new Quest(type, mobID, mobAmount);
            else if(mobID != null)
                quest = new Quest(type, mobID, mobAmount, mobName);
            else
                quest = new Quest(type, item);

            // Save data in QuestInfo class
            if(rewardMessage==null)
                rewardMessage = "";
            QuestInfo questInfo = new QuestInfo(quest, NPCID, money, describe, rewardMessage);
            quests.add(questInfo);
        }
    }

    public int random(int range) {
        Random random = new Random(Calendar.getInstance().getTimeInMillis());
        return random.nextInt(range);
    }

    public ItemStack createItem(String ID, int quantity, String displayName, List<String> lore, int customModelId)
    {
        ItemStack item = new ItemStack(Material.getMaterial(ID.toUpperCase()), quantity);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        if(displayName!=null) {
            meta.setDisplayName(displayName);
        }
        if(customModelId>0) {
            meta.setCustomModelData(customModelId);
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
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
