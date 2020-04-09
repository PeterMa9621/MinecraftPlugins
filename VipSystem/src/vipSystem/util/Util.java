package vipSystem.util;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import vipSystem.ConfigLoader;
import vipSystem.VipPlayer;
import vipSystem.VipSystem;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Util {
    static public FileConfiguration load(File file)
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

    static public FileConfiguration load(String path)
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

    public static LocalDateTime getNewDate(LocalDateTime recentDate, int days)
    {
        return recentDate.plusDays(days);
    }

    public static ItemStack createItem(String ID, int quantity, int durability, String displayName, String lore)
    {
        ItemStack item = new ItemStack(Material.getMaterial(ID), quantity, (short)durability);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        ArrayList<String> loreList = new ArrayList<String>();
        for(String l:lore.split("%"))
        {
            loreList.add(l);
        }
        meta.setLore(loreList);
        item.setItemMeta(meta);

        return item;
    }

    public static ItemStack createItem(String ID, int quantity, int durability, String displayName)
    {
        ItemStack item = new ItemStack(Material.getMaterial(ID), quantity, (short)durability);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        item.setItemMeta(meta);

        return item;
    }

    public static void removeVip(VipPlayer vipPlayer, ConfigLoader configLoader, HashMap<UUID, VipPlayer> players) throws SQLException {
        LuckPerms lp = LuckPermsProvider.get();
        User lpUser = lp.getUserManager().loadUser(vipPlayer.getUniqueId()).join();

        lpUser.data().toMap().values().forEach(nodeList -> {
            nodeList.forEach(node -> {
                if(node.getKey().equalsIgnoreCase("group." + vipPlayer.getVipGroup())){
                    lpUser.data().remove(node);
                }
            });
        });
        lpUser.data().add(Node.builder("group." + VipSystem.defaultGroup).build());
        lp.getUserManager().saveUser(lpUser);
        players.remove(vipPlayer.getUniqueId());
        configLoader.deletePlayerConfig(vipPlayer.getUniqueId());
    }

    public static void addVip(VipPlayer vipPlayer, ConfigLoader configLoader, HashMap<UUID, VipPlayer> players) throws SQLException {
        LuckPerms lp = LuckPermsProvider.get();
        User lpUser = lp.getUserManager().loadUser(vipPlayer.getUniqueId()).join();

        lpUser.data().toMap().values().forEach(nodeList -> {
            nodeList.forEach(node -> {
                for(String vipGroup:VipSystem.vipGroups.keySet()){
                    if(node.getKey().equalsIgnoreCase("group." + vipGroup)){
                        lpUser.data().remove(node);
                    }
                }
                if(node.getKey().equalsIgnoreCase("group." + VipSystem.defaultGroup)){
                    lpUser.data().remove(node);
                }
            });
        });
        lpUser.data().add(Node.builder("group." + vipPlayer.getVipGroup()).build());
        lp.getUserManager().saveUser(lpUser);

        players.put(vipPlayer.getUniqueId(), vipPlayer);

        configLoader.savePlayerConfig(vipPlayer);
    }

    public static UUID getPlayerUUID(String playerName) {
        Player onlinePlayer = Bukkit.getServer().getPlayer(playerName);
        if(onlinePlayer == null){
            return Bukkit.getServer().getOfflinePlayer(playerName).getUniqueId();
        } else {
            return onlinePlayer.getUniqueId();
        }
    }
}
