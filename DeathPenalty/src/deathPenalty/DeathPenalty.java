package deathPenalty;

import deathPenalty.listener.DeathListener;
import deathPenalty.manager.ConfigManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class DeathPenalty extends JavaPlugin
{
	public Economy economy;
	public ConfigManager configManager;
	private boolean setupEconomy()
	{
		if(Bukkit.getPluginManager().getPlugin("Vault")!=null) {
			RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServicesManager().getRegistration(Economy.class);
			if (economyProvider != null) {
				economy = economyProvider.getProvider();
			}
			return economy != null;
		}
		return false;
	}

	public void onEnable() {
		if(!getDataFolder().exists())
		{
			getDataFolder().mkdir();
		}

		if(!setupEconomy())
		{
			Bukkit.getConsoleSender().sendMessage("¡ìa[DeathPenalty] ¡ì4ValutÎ´¼ÓÔØ!");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		configManager = new ConfigManager(this);
		try {
			configManager.loadConfig();
		} catch (IOException e) {
			e.printStackTrace();
			Bukkit.getConsoleSender().sendMessage("¡ìa[DeathPenalty] ¡ì4¼ÓÔØÅäÖÃÎÄ¼şÊ±³öÏÖ´íÎó!");
			Bukkit.getPluginManager().disablePlugin(this);
		}
		getServer().getPluginManager().registerEvents(new DeathListener(this), this);
		Bukkit.getConsoleSender().sendMessage("¡ìa[DeathPenalty] ¡ìeËÀÍö³Í·£¼ÓÔØÍê±Ï");
	}

	public void onDisable() {
		Bukkit.getConsoleSender().sendMessage("¡ìa[DeathPenalty] ¡ìeËÀÍö³Í·£Ğ¶ÔØÍê±Ï");
	}
	
	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args)
	{
		if (cmd.getName().equalsIgnoreCase("deathpenalty") && sender.isOp()) {
			if(args.length == 1) {
				if(args[0].equalsIgnoreCase("help")) {
					sender.sendMessage(((Player)sender).getTotalExperience() + "");
					sender.sendMessage("¡ì6=========[¡ìaËÀÍö³Í·£¡ì6]=========");
					sender.sendMessage("¡ì6/deathpenalty help ¡ì3²é¿´°ïÖú");
					sender.sendMessage("¡ì6/deathpenalty reload ¡ì3ÖØ¶ÁÅäÖÃ");
					return true;
				} else if(args[0].equalsIgnoreCase("reload")) {
					try {
						configManager.loadConfig();
						sender.sendMessage("¡ì6[ËÀÍö³Í·£] ¡ìaÖØ¶ÁÅäÖÃ³É¹¦!");
					} catch (IOException e) {
						e.printStackTrace();
					}
					return true;
				}
			}
		}
		return true;
	}
}

