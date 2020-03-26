package vipSystem;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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

    public ConfigLoader(VipSystem plugin)
    {
        this.plugin=plugin;
    }

    public VipPlayer loadPlayerConfig(UUID uniqueId) throws SQLException {
        if(plugin.players.containsKey(uniqueId)){
            return plugin.players.get(uniqueId);
        }

        if(databaseType.equals(DatabaseType.YML)){
            File file = new File(plugin.getDataFolder(),"/Data/" + uniqueId.toString() + ".yml");
            FileConfiguration config;
            if (file.exists())
            {
                config = Util.load(file);

                String playerName = config.getString("PlayerName");
                String regDate = config.getString( "RegisterDate");
                String deadline = config.getString("DeadlineDate");
                String vipGroup = config.getString("VIPGroup");

                return new VipPlayer(uniqueId, playerName, LocalDateTime.parse(regDate), LocalDateTime.parse(deadline), vipGroup);
            }
        } else {
            Database MySQL = Database.getInstance();
            String selectQuery = "SELECT id, player_name, DATE_FORMAT(register_date, '%Y-%m-%d %k:%i:%s') as register_date, " +
                    "DATE_FORMAT(deadline_date, '%Y-%m-%d %k:%i:%s') as deadline_date, vip_group FROM vip_system where id = ? and is_expired = 0";
            PreparedStatement stmt = MySQL.getConnection().prepareStatement(selectQuery);
            stmt.setString(1, uniqueId.toString());
            ResultSet resultSet = stmt.executeQuery();
            if(resultSet.next()){
                String playerName = resultSet.getString(2);
                String registerDate = resultSet.getString(3);
                String deadlineDate = resultSet.getString(4);
                String vipGroup = resultSet.getString(5);
                DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                VipPlayer vipPlayer = new VipPlayer(uniqueId, playerName, LocalDateTime.parse(registerDate, format), LocalDateTime.parse(deadlineDate, format), vipGroup);
                plugin.players.put(uniqueId, vipPlayer);
                return vipPlayer;
            }
        }
        return null;
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
            Bukkit.getConsoleSender().sendMessage(String.valueOf(resultSet.getRow()));
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
            config.set("VIP1.Items.1.ID", "diamond");
            config.set("VIP1.Items.1.Amount", 32);
            config.set("VIP1.Items.1.DisplayName", "钻石");
            config.set("VIP1.Items.1.Lore", "神奇钻石");
            config.set("VIP1.Items.1.Enchant.ID", "fortune");
            config.set("VIP1.Items.1.Enchant.Level", 1);
            config.set("VIP1.Items.1.HideEnchant", true);

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
            ArrayList<ItemStack> items = new ArrayList<ItemStack>();
            int money = config.getInt(vipGroup + ".Money");
            for(int i=0; config.contains(vipGroup + ".Items."+(i+1)); i++)
            {
                String id = "";
                boolean hide = config.getBoolean(vipGroup + ".Items."+(i+1)+".HideEnchant");
                if(config.getString(vipGroup + ".Items."+(i+1)+".ID").contains(":")) {
                    id = config.getString(vipGroup + ".Items."+(i+1)+".ID").split(":")[0];
                }
                else {
                    id = config.getString(vipGroup + ".Items."+(i+1)+".ID");
                }
                int amount = config.getInt(vipGroup + ".Items."+(i+1)+".Amount");
                String name = config.getString(vipGroup + ".Items."+(i+1)+".DisplayName");
                String lore = config.getString(vipGroup + ".Items."+(i+1)+".Lore");
                String enchantID = config.getString(vipGroup + ".Items."+(i+1)+".Enchant.ID");
                int level = config.getInt(vipGroup + ".Items."+(i+1)+".Enchant.Level");

                ItemStack item = new ItemStack(Material.getMaterial(id.toUpperCase()), amount);
                ItemMeta meta = item.getItemMeta();
                if(name!=null)
                    meta.setDisplayName(name);
                if(lore!=null)
                {
                    ArrayList<String> itemLore = new ArrayList<String>();
                    for(String l:lore.split("%"))
                    {
                        itemLore.add(l);
                    }
                    meta.setLore(itemLore);
                }
                if(hide==true)
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                item.setItemMeta(meta);

                if(!enchantID.equalsIgnoreCase("") && level>0)
                {
                    item.addUnsafeEnchantment(EnchantmentWrapper.getByKey(NamespacedKey.minecraft(enchantID)), level);
                }
                items.add(item);
            }
            VipReward vipReward = new VipReward(items, money);
            reward.put(vipGroup, vipReward);
        }

        return;
    }
}
