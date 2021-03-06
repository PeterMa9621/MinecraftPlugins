package vipSystem;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import vipSystem.util.Util;

import java.io.IOException;
import java.sql.SQLException;

/**
 * This class will automatically register as a placeholder expansion
 * when a jar including this class is added to the directory
 * {@code /plugins/PlaceholderAPI/expansions} on your server.
 * <br>
 * <br>If you create such a class inside your own plugin, you have to
 * register it manually in your plugins {@code onEnable()} by using
 * {@code new YourExpansionClass().register();}
 */
public class VipSystemExpansion extends PlaceholderExpansion {

    VipSystem plugin;
    public VipSystemExpansion(VipSystem plugin){
        this.plugin = plugin;
    }
    /**
     * This method should always return true unless we
     * have a dependency we need to make sure is on the server
     * for our placeholders to work!
     *
     * @return always true since we do not have any dependencies.
     */
    @Override
    public boolean canRegister(){
        return true;
    }

    /**
     * The name of the person who created this expansion should go here.
     *
     * @return The name of the author as a String.
     */
    @Override
    public String getAuthor(){
        return "Jingyuan Ma";
    }

    /**
     * The placeholder identifier should go here.
     * <br>This is what tells PlaceholderAPI to call our onRequest
     * method to obtain a value if a placeholder starts with our
     * identifier.
     * <br>This must be unique and can not contain % or _
     *
     * @return The identifier in {@code %<identifier>_<value>%} as String.
     */
    @Override
    public String getIdentifier(){
        return "vipSystem";
    }

    /**
     * This is the version of this expansion.
     * <br>You don't have to use numbers, since it is set as a String.
     *
     * @return The version as a String.
     */
    @Override
    public String getVersion(){
        return "1.0.1";
    }

    /**
     * This is the method called when a placeholder with our identifier
     * is found and needs a value.
     * <br>We specify the value identifier in this method.
     * <br>Since version 2.9.1 can you use OfflinePlayers in your requests.
     *
     * @param  player
     *         A {@link Player Player}.
     * @param  identifier
     *         A String containing the identifier/value.
     *
     * @return Possibly-null String of the requested identifier.
     */
    @Override
    public String onPlaceholderRequest(Player player, String identifier){

        // %example_placeholder1%
        if(identifier.equals("vipGroup")){
            VipPlayer vipPlayer = plugin.configLoader.loadPlayerConfig(player.getUniqueId());
            if(vipPlayer!=null) {
                if(!vipPlayer.isExpired && vipPlayer.checkDeadline()) {
                    try {
                        Util.removeVip(vipPlayer, plugin.configLoader);
                        player.sendMessage("§6[会员系统] §e你的会员已到期");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return vipPlayer.getVipGroupDisplayName();
            } else
                return "公民";
        }

        // %example_placeholder2%
        if(identifier.equals("leftTime")){
            VipPlayer vipPlayer = plugin.configLoader.loadPlayerConfig(player.getUniqueId());
            if(vipPlayer!=null)
                return vipPlayer.getLeftTime();
            else
                return "";
        }

        // We return null if an invalid placeholder (f.e. %example_placeholder3%)
        // was provided
        return null;
    }
}