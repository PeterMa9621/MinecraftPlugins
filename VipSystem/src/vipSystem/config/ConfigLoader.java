package vipSystem.config;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import peterUtil.database.Database;
import peterUtil.database.DatabaseType;
import peterUtil.database.StorageInterface;
import vipSystem.VipPlayer;
import vipSystem.VipReward;
import vipSystem.VipSystem;
import vipSystem.util.Util;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class ConfigLoader {

    private VipSystem plugin;
    private DatabaseType databaseType;
    private String prefix = "[" + ChatColor.YELLOW + "VipSystem" + ChatColor.RESET + "] " + " - ";
    private StorageInterface database;
    private String databaseName;

    public ConfigLoader(VipSystem plugin) {
        this.plugin=plugin;
    }

    private void initDatabase() {
        database = Database.getInstance(databaseType, plugin);
        String createTableQuery = "create table if not exists vip_system(id varchar(100), player_name varchar(100), register_date datetime, deadline_date datetime , vip_group varchar(30), is_expired tinyint, primary key (id));";
        database.connect(databaseName, "vip_system" , "root", "mjy159357", createTableQuery);
    }

    public VipPlayer loadPlayerConfig(UUID uniqueId) {
        if(plugin.players.containsKey(uniqueId)){
            return plugin.players.get(uniqueId);
        }

        VipPlayer vipPlayer;
        Player player = Bukkit.getPlayer(uniqueId);
        String playerName = player.getName();
        String registerDate = "1900-01-01 00:00:00";
        String deadlineDate = "1900-01-01 00:00:00";
        String vipGroup = "";
        boolean isExpired = true;
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if(databaseType.equals(DatabaseType.YML)){
            File file = new File(plugin.getDataFolder(),"/Data/" + uniqueId.toString() + ".yml");
            FileConfiguration config;
            if (file.exists()) {
                config = Util.load(file);

                playerName = config.getString("PlayerName");
                registerDate = config.getString( "RegisterDate");
                deadlineDate = config.getString("DeadlineDate");
                vipGroup = config.getString("VIPGroup");
                isExpired = config.getBoolean("IsExpired");
            }
            vipPlayer = new VipPlayer(uniqueId, playerName, LocalDateTime.parse(registerDate), LocalDateTime.parse(deadlineDate), vipGroup);
        } else {
            HashMap<String, Object> result = database.get(uniqueId, new String[] {"player_name", "register_date", "deadline_date", "vip_group", "is_expired"});
            if(result!=null){
                playerName = (String) result.get("player_name");
                registerDate = ((Timestamp) result.get("register_date")).toLocalDateTime().format(format);
                deadlineDate = ((Timestamp) result.get("deadline_date")).toLocalDateTime().format(format);
                vipGroup = (String) result.get("vip_group");
                isExpired = (Integer) result.get("is_expired")==1;
            }
            vipPlayer = new VipPlayer(uniqueId, playerName, LocalDateTime.parse(registerDate, format), LocalDateTime.parse(deadlineDate, format), vipGroup);
        }
        vipPlayer.setIsExpired(isExpired);
        plugin.players.put(uniqueId, vipPlayer);
        return vipPlayer;
    }

    public void savePlayerConfig(VipPlayer vipPlayer) throws IOException {
        UUID uniqueId = vipPlayer.getUniqueId();
        if(databaseType.equals(DatabaseType.YML)){
            File file=new File(plugin.getDataFolder(),"/Data/"+uniqueId.toString()+".yml");
            FileConfiguration config;

            config = Util.load(file);

            String playerName = vipPlayer.getName();
            LocalDateTime regDate = vipPlayer.getRegDate();
            LocalDateTime deadline = vipPlayer.getDeadline();
            String vipGroup = vipPlayer.getVipGroup();

            config.set("PlayerName", playerName);
            config.set("RegisterDate", regDate.toString());
            config.set("DeadlineDate", deadline.toString());
            config.set("VIPGroup", vipGroup);
            config.set("IsExpired", vipPlayer.checkDeadline());
            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            HashMap<String, Object> data = new HashMap<String, Object>() {{
                put("player_name", vipPlayer.getName());
                put("register_date", vipPlayer.getRegDate());
                put("deadline_date", vipPlayer.getDeadline());
                put("vip_group", vipPlayer.getVipGroup());
                put("is_expired", vipPlayer.checkDeadline());
            }};

            database.store(uniqueId, data);
        }
    }

    public void closeDatabase() {
        if(!databaseType.equals(DatabaseType.MYSQL))
            return;

        database.close();
        Bukkit.getConsoleSender().sendMessage(prefix + plugin.getName() + "Database connection closed!");
    }

    public void deletePlayerConfig(UUID uniqueId) throws IOException {
        if(databaseType.equals(DatabaseType.YML)){
            File file=new File(plugin.getDataFolder(), "/Data/"+uniqueId.toString()+".yml");
            if(file.exists())
                file.delete();
        } else {
            HashMap<String, Object> data = new HashMap<String, Object>() {{
                put("is_expired", true);
            }};

            database.store(uniqueId, data);
        }
    }

    public String loadConfig(HashMap<String, String> vipGroups)
    {
        File file=new File(plugin.getDataFolder(),"config.yml");
        FileConfiguration config;
        vipGroups.clear();

        if(!file.exists())
        {
            config = Util.load(file);
            ArrayList<String> tempVipGroups = new ArrayList<String>();
            tempVipGroups.add("vip1");
            tempVipGroups.add("vip2");
            tempVipGroups.add("vip3");
            tempVipGroups.add("vip4");
            config.set("VipSystem.DefaultGroup", "default");
            config.set("VipSystem.DatabaseType", "yml");
            config.set("VipSystem.DatabaseName", "minecraft");
            config.set("VipSystem.VIPGroups", tempVipGroups);
            ArrayList<String> vipGroupsName = new ArrayList<String>();
            vipGroupsName.add("初级会员");
            vipGroupsName.add("中级会员");
            vipGroupsName.add("高级会员");
            vipGroupsName.add("至尊会员");

            config.set("VipSystem.VIPGroupsName", vipGroupsName);

            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //loadConfig();
        }
        config = Util.load(file);
        int index=0;
        for(String vipGroup:config.getStringList("VipSystem.VIPGroups"))
        {
            vipGroups.put(vipGroup, config.getStringList("VipSystem.VIPGroupsName").get(index));
            index++;
        }
        String type = config.getString("VipSystem.DatabaseType").toUpperCase();
        databaseName = config.getString("VipSystem.DatabaseName", "minecraft");
        databaseType = DatabaseType.valueOf(type.toUpperCase());
        initDatabase();
        return config.getString("VipSystem.DefaultGroup");
    }

    public void loadItems(HashMap<String, String> vipGroups, HashMap<String, VipReward> reward)
    {
        File file=new File(plugin.getDataFolder(), "items.yml");
        FileConfiguration config;
        reward.clear();

        if(!file.exists())
        {
            config = Util.load(file);

            config.set("VIP1.Money", 3000);
            config.set("VIP1.Items.MinInventory", 8);
            config.set("VIP1.Items.1.Command", "give %player% diamond 32");

            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //loadItems(vipGroups);
            //return;
        }

        config = Util.load(file);

        for(String vipGroup:vipGroups.keySet())
        {
            ArrayList<String> commands = new ArrayList<>();
            int money = config.getInt(vipGroup + ".Money", 0);
            int minInventory = config.getInt(vipGroup + ".Items.MinInventory", 1);
            for(int i=0; config.contains(vipGroup + ".Items."+(i+1)); i++)
            {
                String command = config.getString(vipGroup + ".Items."+(i+1) + ".Command");

                commands.add(command);
            }
            VipReward vipReward = new VipReward(commands, money, minInventory);
            reward.put(vipGroup, vipReward);
        }

        return;
    }
}
