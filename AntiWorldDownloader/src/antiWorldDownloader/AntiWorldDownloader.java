package antiWorldDownloader;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class AntiWorldDownloader extends JavaPlugin 
implements Listener, PluginMessageListener
{
	private static String prefix = "§8[§b反地图下载§8] §r";
	private static final String INIT_CHANNEL_NAME = "WDL|INIT";
	private static String punishcmd;
	private static String reloadcfg;
	private static String noperm;
	  
	public void onEnable()
	{
	    getServer().getPluginManager().registerEvents(this, this);
	    getConfig().options().copyDefaults(true);
	    punishcmd = getConfig().getString("Punishment-Command");
	    reloadcfg = getConfig().getString("Messages.reloadConfig").replace('&', '§');
	    noperm = getConfig().getString("Messages.noPermission").replace('&', '§');
	    saveConfig();
	    getServer().getMessenger().registerIncomingPluginChannel(this, "WDL|INIT", this);
	    getServer().getMessenger().registerOutgoingPluginChannel(this, "WDL|CONTROL");
	    Bukkit.getConsoleSender().sendMessage("§a[AntiWorldDownloader] §e反地图下载加载完毕");
	    
	}
	  
	public void onDisable()
	{
	    getServer().getMessenger().unregisterIncomingPluginChannel(this, "WDL|INIT");
	    getServer().getMessenger().unregisterOutgoingPluginChannel(this, "WDL|CONTROL");
	    Bukkit.getConsoleSender().sendMessage("§a[AntiWorldDownloader] §e反地图下载卸载完毕");
	}
	  
	public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args)
	{
	    Player p = (Player)sender;
	    if (cmd.getName().equalsIgnoreCase("antiwdl"))
	    {
	    	if (args.length == 0)
	    	{
	    		if(sender.isOp())
	    		{
		    		p.sendMessage(prefix + "§eAntiWorldDownloader Plugin §fby CrispyBow");
		    		p.sendMessage("§7§l §fVersion§8: §7" + getDescription().getVersion());
		        	p.sendMessage("§7§l §fSkype§8: §7crispybow31");
		        	p.sendMessage("§7§l §fYouTube§8: §7CrispyBow");
		        	p.sendMessage(prefix + "§eCommands§8:");
		        	p.sendMessage("§7§l §f/antiwdl reload §7(重载配置)");
	    		}
	        	return true;
	    	}
	    	if (args[0].equalsIgnoreCase("reload")) 
	    	{
	    		if (p.hasPermission("CrispyAntiWDL.reload"))
	    		{
	    			reloadConfig();
	    			saveConfig();
	    			p.sendMessage(prefix + reloadcfg);
	    		}
	    		else
	    		{
	    			p.sendMessage(prefix + noperm);
	    		}
	    	}
	    	return true;
	    }
	    return false;
	  }
	  
	  public void onPluginMessageReceived(String channel, Player player, byte[] data)
	  {
	    if ((channel.equals("WDL|INIT")) && 
	      (!player.hasPermission("CrispyAntiWDL.admin"))) {
	      Bukkit.dispatchCommand(Bukkit.getConsoleSender(), getConfig().getString("Punishment-Command").replace("%prefix%", "[CrispyAntiWDL]").replace("%player%", player.getName()));
	    }
	  }

}

