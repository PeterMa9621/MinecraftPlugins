package vipSystem;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;
import vipSystem.mysql.Database;
import vipSystem.util.Util;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class ConfigLoader {

    private VipSystem plugin;
    private DatabaseType databaseType = DatabaseType.YML;
    private BukkitTask closeConnectionTask;
    private String prefix = "[" + ChatColor.YELLOW + "VipSystem" + ChatColor.RESET + "] " + " - ";

    public ConfigLoader(VipSystem plugin)
    {
        this.plugin=plugin;
    }

    public VipPlayer loadPlayerConfig(UUID uniqueId) throws SQLException {
        Bukkit.getConsoleSender().sendMessage("" + plugin.players.containsKey(uniqueId));
        if(plugin.players.containsKey(uniqueId)){

            return plugin.players.get(uniqueId);
        }

        VipPlayer vipPlayer;
        Player player = Bukkit.getPlayer(uniqueId);
        String playerName = player.getName();
        String registerDate = "1900-01-01 00:00:00";
        String deadlineDate = "1900-01-01 00:00:00";
        String vipGroup = "";
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
            }
            vipPlayer = new VipPlayer(uniqueId, playerName, LocalDateTime.parse(registerDate), LocalDateTime.parse(deadlineDate), vipGroup);
        } else {
            Database MySQL = Database.getInstance();
            String selectQuery = "SELECT id, player_name, DATE_FORMAT(register_date, '%Y-%m-%d %H:%i:%s') as register_date, " +
                    "DATE_FORMAT(deadline_date, '%Y-%m-%d %H:%i:%s') as deadline_date, vip_group FROM vip_system where id = ? and is_expired = 0";
            PreparedStatement stmt = MySQL.getConnection().prepareStatement(selectQuery);
            stmt.setString(1, uniqueId.toString());
            ResultSet resultSet = stmt.executeQuery();
            Bukkit.getConsoleSender().sendMessage("AAAAA");
            if(resultSet.next()){
                playerName = resultSet.getString(2);
                registerDate = resultSet.getString(3);
                deadlineDate = resultSet.getString(4);
                vipGroup = resultSet.getString(5);
            }
            vipPlayer = new VipPlayer(uniqueId, playerName, LocalDateTime.parse(registerDate, format), LocalDateTime.parse(deadlineDate, format), vipGroup);
        }
        plugin.players.put(uniqueId, vipPlayer);
        closeConnection();
        return vipPlayer;
    }

    public void savePlayerConfig(VipPlayer vipPlayer) throws SQLException {
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

            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Database MySQL = Database.getInstance();
            String selectQuery = "Select * from vip_system where id = ?";
            PreparedStatement stmt = MySQL.getConnection().prepareStatement(selectQuery);
            stmt.setString(1, uniqueId.toString());
            ResultSet resultSet = stmt.executeQuery();

            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String registerDate = vipPlayer.getRegDate().format(format);
            String deadlineDate = vipPlayer.getDeadline().format(format);

            if(resultSet.next()){
                String updateQuery = "Update vip_system set player_name = ?, register_date = ?, deadline_date = ?, vip_group = ?, is_expired = ? where id = ?";
                stmt = MySQL.getConnection().prepareStatement(updateQuery);
                stmt.setString(1, vipPlayer.getName());
                stmt.setString(2, registerDate);
                stmt.setString(3, deadlineDate);
                stmt.setString(4, vipPlayer.getVipGroup());
                stmt.setBoolean(5, false);
                stmt.setString(6, uniqueId.toString());
                stmt.executeUpdate();
            } else {
                String insertQuery = "Insert into vip_system (id, player_name, register_date, deadline_date, vip_group, is_expired) values (?,?,?,?,?,?)";
                stmt = MySQL.getConnection().prepareStatement(insertQuery);
                stmt.setString(1, uniqueId.toString());
                stmt.setString(2, vipPlayer.getName());
                stmt.setString(3, registerDate);
                stmt.setString(4, deadlineDate);
                stmt.setString(5, vipPlayer.getVipGroup());
                stmt.setBoolean(6, false);
                stmt.execute();
            }
            closeConnection();
        }
    }

    private void closeConnection() {
        Bukkit.getConsoleSender().sendMessage("BBBBB");
        if(plugin.isEnabled() && databaseType.equals(DatabaseType.MYSQL)) {
            if(closeConnectionTask!=null)
                closeConnectionTask.cancel();
            closeConnectionTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
                try {
                    Bukkit.getConsoleSender().sendMessage("CCCCC");
                    Database MySQL = Database.getInstance();
                    MySQL.getConnection().close();
                    Bukkit.getConsoleSender().sendMessage(prefix + plugin.getName() + "Database connection closed!");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }, 20*15);
        }
    }

    public void closeDatabase() {
        if(!databaseType.equals(DatabaseType.MYSQL))
            return;
        if(closeConnectionTask!=null)
            closeConnectionTask.cancel();

        try {
            Database MySQL = Database.getInstance();
            MySQL.getConnection().close();
            Bukkit.getConsoleSender().sendMessage(prefix + plugin.getName() + "Database connection closed!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deletePlayerConfig(UUID uniqueId) throws SQLException {
        if(databaseType.equals(DatabaseType.YML)){
            File file=new File(plugin.getDataFolder(), "/Data/"+uniqueId.toString()+".yml");
            if(file.exists())
                file.delete();
        } else {
            Database MySQL = Database.getInstance();
            String deleteQuery = "Update vip_system set is_expired = 1 where id = ?;";
            PreparedStatement stmt = MySQL.getConnection().prepareStatement(deleteQuery);
            stmt.setString(1, uniqueId.toString());
            stmt.executeUpdate();
            closeConnection();
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
        Database.databaseName = config.getString("VipSystem.DatabaseName", "minecraft");
        databaseType = DatabaseType.valueOf(type);
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
