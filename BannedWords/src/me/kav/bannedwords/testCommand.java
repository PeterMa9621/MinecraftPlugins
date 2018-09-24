/*** Eclipse Class Decompiler plugin, copyright (c) 2016 Chen Chao (cnfree2000@hotmail.com) ***/
package me.kav.bannedwords;

import java.util.List;
import me.kav.bannedwords.Main;
import me.kav.bannedwords.hashmaps;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class testCommand implements CommandExecutor, hashmaps {
	Main plugin;

	public testCommand(Main passedPlugin) {
		this.plugin = passedPlugin;
	}

	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		Player player = (Player) sender;
		if (alias.equals("bw")) {
			if (args.length == 0) {
				if (player.hasPermission("bannedwords.help")) {
					player.sendMessage(ChatColor.YELLOW + "Banned" + ChatColor.RED + "Words" + ChatColor.GRAY
							+ " ------------By Kaveen------------");
					player.sendMessage(ChatColor.YELLOW + "Banned" + ChatColor.RED + "Words" + ChatColor.YELLOW
							+ "    /bw reload " + ChatColor.RED);
					player.sendMessage(ChatColor.YELLOW + "Banned" + ChatColor.RED + "Words" + ChatColor.YELLOW
							+ "    /bw addword <banned word> ");
					player.sendMessage(ChatColor.YELLOW + "Banned" + ChatColor.RED + "Words" + ChatColor.YELLOW
							+ "    /bw addline <banned sentence>");
					player.sendMessage(ChatColor.YELLOW + "Banned" + ChatColor.RED + "Words" + ChatColor.YELLOW
							+ "    /bw removeline <banned line> ");
					player.sendMessage(ChatColor.YELLOW + "Banned" + ChatColor.RED + "Words" + ChatColor.YELLOW
							+ "    /bw removeword <banned word> ");
					player.sendMessage(ChatColor.YELLOW + "Banned" + ChatColor.RED + "Words" + ChatColor.YELLOW
							+ "    /bw permissions");
					player.sendMessage(ChatColor.YELLOW + "Banned" + ChatColor.RED + "Words" + ChatColor.YELLOW
							+ "    /bw list  ");
					player.sendMessage(ChatColor.YELLOW + "Banned" + ChatColor.RED + "Words" + ChatColor.YELLOW
							+ "    /bw setmode <mode> ");
					player.sendMessage(ChatColor.YELLOW + "Banned" + ChatColor.RED + "Words" + ChatColor.GRAY
							+ " ------------By Kaveen------------");
				}
			} else if (args.length == 1 && args[0].equals("reload")) {
				if (player.hasPermission("bannedwords.reload")) {
					this.plugin.getServer().getPluginManager().disablePlugin(this.plugin);
					this.plugin.getServer().getPluginManager().enablePlugin(this.plugin);
					System.out.print("[BannedWords] Plugin has been reloaded");
					player.sendMessage(ChatColor.YELLOW + "Banned" + ChatColor.RED + "Words" + ChatColor.GRAY
							+ " ------------By Kaveen------------");
					player.sendMessage(ChatColor.YELLOW + "Banned" + ChatColor.RED + "Words" + ChatColor.RED
							+ "     Plugin has been reloaded");
					player.sendMessage(ChatColor.YELLOW + "Banned" + ChatColor.RED + "Words" + ChatColor.GRAY
							+ " ------------By Kaveen------------");
				}
			} else {
				List list;
				if (args.length == 2 && args[0].equals("addword")) {
					if (player.hasPermission("bannedwords.add")) {
						list = this.plugin.getConfig().getStringList("bannedwords");
						list.add(args[1]);
						this.plugin.getConfig().set("bannedwords", list);
						this.plugin.saveConfig();
						player.sendMessage(ChatColor.YELLOW + "Banned" + ChatColor.RED + "Words" + ChatColor.GRAY
								+ " ------------By Kaveen------------");
						player.sendMessage(ChatColor.YELLOW + "Banned" + ChatColor.RED + "Words" + ChatColor.RED
								+ "     Added line " + args[1]);
						player.sendMessage(ChatColor.YELLOW + "Banned" + ChatColor.RED + "Words" + ChatColor.GRAY
								+ " ------------By Kaveen------------");
					}
				} else {
					String myString;
					int i;
					String arg;
					if (args.length >= 2 && args[0].equals("addline")) {
						if (player.hasPermission("bannedwords.addline")) {
							list = this.plugin.getConfig().getStringList("bannedwords");
							myString = "";

							for (i = 1; i < args.length; ++i) {
								arg = args[i] + " ";
								myString = myString + arg;
							}

							player.sendMessage(ChatColor.YELLOW + "Banned" + ChatColor.RED + "Words" + ChatColor.GRAY
									+ " ------------By Kaveen------------");
							player.sendMessage(ChatColor.YELLOW + "Banned" + ChatColor.RED + "Words" + ChatColor.RED
									+ "     Added line " + myString);
							player.sendMessage(ChatColor.YELLOW + "Banned" + ChatColor.RED + "Words" + ChatColor.GRAY
									+ " ------------By Kaveen------------");
							list.add(myString.trim());
							this.plugin.getConfig().set("bannedwords", list);
							this.plugin.saveConfig();
						}
					} else if (args.length >= 2 && args[0].equals("removeword")) {
						if (player.hasPermission("bannedwords.removeword")) {
							list = this.plugin.getConfig().getStringList("bannedwords");
							myString = args[1];
							if (list.contains(myString)) {
								list.remove(myString);
								this.plugin.getConfig().set("bannedwords", list);
								this.plugin.saveConfig();
								player.sendMessage(ChatColor.YELLOW + "Banned" + ChatColor.RED + "Words"
										+ ChatColor.GRAY + " ------------By Kaveen------------");
								player.sendMessage(ChatColor.YELLOW + "Banned" + ChatColor.RED + "Words" + ChatColor.RED
										+ "     Removed word " + myString);
								player.sendMessage(ChatColor.YELLOW + "Banned" + ChatColor.RED + "Words"
										+ ChatColor.GRAY + " ------------By Kaveen------------");
							} else {
								player.sendMessage(ChatColor.RED + myString + ChatColor.YELLOW
										+ " is not in the list of banned words!");
							}
						}
					} else if (args.length >= 2 && args[0].equals("removeline")) {
						if (player.hasPermission("bannedwords.removeline")) {
							list = this.plugin.getConfig().getStringList("bannedwords");
							myString = "";

							for (i = 1; i < args.length; ++i) {
								arg = args[i] + " ";
								myString = myString + arg;
							}

							if (list.contains(myString.trim())) {
								list.remove(myString.trim());
								this.plugin.getConfig().set("bannedwords", list);
								this.plugin.saveConfig();
								player.sendMessage(ChatColor.YELLOW + "Banned" + ChatColor.RED + "Words"
										+ ChatColor.GRAY + " ------------By Kaveen------------");
								player.sendMessage(ChatColor.YELLOW + "Banned" + ChatColor.RED + "Words" + ChatColor.RED
										+ "     Removed line " + myString);
								player.sendMessage(ChatColor.YELLOW + "Banned" + ChatColor.RED + "Words"
										+ ChatColor.GRAY + " ------------By Kaveen------------");
							} else {
								player.sendMessage(ChatColor.RED + myString + ChatColor.YELLOW
										+ " is not in the list of banned lines!");
							}
						}
					} else if (args.length == 1 && args[0].equals("permissions")) {
						player.sendMessage(ChatColor.YELLOW + "Banned" + ChatColor.RED + "Words" + ChatColor.GRAY
								+ " ------------By Kaveen------------");
						player.sendMessage(ChatColor.RED + "bannedwords.reload");
						player.sendMessage(ChatColor.RED + "bannedwords.addline");
						player.sendMessage(ChatColor.RED + "bannedwords.removeword");
						player.sendMessage(ChatColor.RED + "bannedwords.removeline");
						player.sendMessage(ChatColor.RED + "bannedwords.list");
						player.sendMessage(ChatColor.RED + "bannedwords.exempt - plugin doesn\'t check you");
						player.sendMessage(ChatColor.RED + "bannedwords.setmode");
						player.sendMessage(ChatColor.YELLOW + "Banned" + ChatColor.RED + "Words" + ChatColor.GRAY
								+ " ------------By Kaveen------------");
					} else if (args.length == 1 && args[0].equals("list")) {
						if (player.hasPermission("bannedwords.list")) {
							player.sendMessage(this.plugin.getConfig().getList("bannedwords").toString());
						}
					} else if (args.length == 2 && args[0].equals("setmode")) {
						if (player.hasPermission("bannedwords.setmode")) {
							if (args[1].equals("kickban")) {
								this.plugin.getConfig().set("punishtype", "kickban");
								player.sendMessage(ChatColor.YELLOW + "Banned" + ChatColor.RED + "Words"
										+ ChatColor.GRAY + " Mode has been set to KICKBAN");
								this.plugin.saveConfig();
								this.plugin.getServer().getPluginManager().disablePlugin(this.plugin);
								this.plugin.getServer().getPluginManager().enablePlugin(this.plugin);
							} else if (args[1].equals("kick")) {
								this.plugin.getConfig().set("punishtype", "kick");
								this.plugin.saveConfig();
								player.sendMessage(ChatColor.YELLOW + "Banned" + ChatColor.RED + "Words"
										+ ChatColor.GRAY + " Mode has been set to KICK");
								this.plugin.getServer().getPluginManager().disablePlugin(this.plugin);
								this.plugin.getServer().getPluginManager().enablePlugin(this.plugin);
							} else if (args[1].equals("mute")) {
								this.plugin.getConfig().set("punishtype", "mute");
								this.plugin.saveConfig();
								player.sendMessage(ChatColor.YELLOW + "Banned" + ChatColor.RED + "Words"
										+ ChatColor.GRAY + " Mode has been set to MUTE");
								this.plugin.getServer().getPluginManager().disablePlugin(this.plugin);
								this.plugin.getServer().getPluginManager().enablePlugin(this.plugin);
							} else if (args[1].equals("deny")) {
								this.plugin.getConfig().set("punishtype", "deny");
								this.plugin.saveConfig();
								player.sendMessage(ChatColor.YELLOW + "Banned" + ChatColor.RED + "Words"
										+ ChatColor.GRAY + " Mode has been set to DENY");
								this.plugin.getServer().getPluginManager().disablePlugin(this.plugin);
								this.plugin.getServer().getPluginManager().enablePlugin(this.plugin);
							} else {
								player.sendMessage(ChatColor.YELLOW + "Banned" + ChatColor.RED + "Words"
										+ ChatColor.YELLOW + " Invalid mode! The modes are: kick, kickban, mute, deny");
							}
						}
					} else {
						player.sendMessage(ChatColor.RED + "Invalid syntax! /bw");
					}
				}
			}
		}

		return true;
	}
}