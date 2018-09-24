/*** Eclipse Class Decompiler plugin, copyright (c) 2016 Chen Chao (cnfree2000@hotmail.com) ***/
package me.kav.bannedwords;

import me.kav.bannedwords.PlayerListener;
import me.kav.bannedwords.denyListener;
import me.kav.bannedwords.hashmaps;
import me.kav.bannedwords.kickListener;
import me.kav.bannedwords.muteListener;
import me.kav.bannedwords.testCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements hashmaps {
	public void onEnable() {
		this.loadConfiguration();
		this.reloadConfig();
		String punishtype = this.getConfig().getString("punishtype").toLowerCase();
		if (punishtype.equals("kickban")) {
			new PlayerListener(this);
		} else if (punishtype.equals("mute")) {
			new muteListener(this);
		} else if (punishtype.equals("deny")) {
			new denyListener(this);
		} else if (punishtype.equals("kick")) {
			new kickListener(this);
		}

		this.getCommand("bw").setExecutor(new testCommand(this));
	}

	public void onDisable() {
	}

	public void loadConfiguration() {
		this.getConfig().options().copyDefaults(true);
		this.saveDefaultConfig();
	}

	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		if (alias.equalsIgnoreCase("hellohello")) {
			Player player = (Player) sender;
			player.sendMessage("success");
			player.sendMessage(this.getConfig().getList("bannedwords").toString());
		}

		return false;
	}
}