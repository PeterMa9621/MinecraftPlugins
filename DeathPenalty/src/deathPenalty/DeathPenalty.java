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
			Bukkit.getConsoleSender().sendMessage("��a[DeathPenalty] ��4Valutδ����!");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		configManager = new ConfigManager(this);
		try {
			configManager.loadConfig();
		} catch (IOException e) {
			e.printStackTrace();
			Bukkit.getConsoleSender().sendMessage("��a[DeathPenalty] ��4���������ļ�ʱ���ִ���!");
			Bukkit.getPluginManager().disablePlugin(this);
		}
		getServer().getPluginManager().registerEvents(new DeathListener(this), this);
		Bukkit.getConsoleSender().sendMessage("��a[DeathPenalty] ��e�����ͷ��������");
	}

	public void onDisable() {
		Bukkit.getConsoleSender().sendMessage("��a[DeathPenalty] ��e�����ͷ�ж�����");
	}
	
	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args)
	{
		if (cmd.getName().equalsIgnoreCase("deathpenalty") && sender.isOp()) {
			if(args.length == 1) {
				if(args[0].equalsIgnoreCase("help")) {
					sender.sendMessage(((Player)sender).getTotalExperience() + "");
					sender.sendMessage("��6=========[��a�����ͷ���6]=========");
					sender.sendMessage("��6/deathpenalty help ��3�鿴����");
					sender.sendMessage("��6/deathpenalty reload ��3�ض�����");
					return true;
				} else if(args[0].equalsIgnoreCase("reload")) {
					try {
						configManager.loadConfig();
						sender.sendMessage("��6[�����ͷ�] ��a�ض����óɹ�!");
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

