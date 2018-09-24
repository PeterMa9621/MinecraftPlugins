package net.stupendous.autoshutdown;

import java.util.Calendar;
import net.stupendous.autoshutdown.misc.Log;
import net.stupendous.autoshutdown.misc.Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AutoShutdownCommand implements CommandExecutor {
	private final AutoShutdownPlugin plugin;
	private Log log;

	public AutoShutdownCommand(AutoShutdownPlugin plugin) {
		this.plugin = plugin;
		this.log = plugin.log;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if ((sender instanceof Player) && (!(((Player)sender).hasPermission("autoshutdown.admin")))) {
      Util.replyError(sender, "You don't have permission to use that command.");
      return true;
    }

    if (args.length == 0) {
      args = new String[] { "HELP" };
    }

    switch (SubCommand.toSubCommand(args[0].toUpperCase()).ordinal())
    {
    case 0:
      Util.reply(sender, "AutoShutdown plugin help:");
      Util.reply(sender, " /%s help", new Object[] { command.getName() });
      Util.reply(sender, "     Shows this help page");
      Util.reply(sender, " /%s reload", new Object[] { command.getName() });
      Util.reply(sender, "     Reloads the configuration file");
      Util.reply(sender, " /%s cancel", new Object[] { command.getName() });
      Util.reply(sender, "     Cancels the currently executing shutdown");
      Util.reply(sender, " /%s set HH:MM:SS", new Object[] { command.getName() });
      Util.reply(sender, "     Sets a new scheduled shutdown time");
      Util.reply(sender, " /%s set now", new Object[] { command.getName() });
      Util.reply(sender, "     Orders the server to shutdown immediately");
      Util.reply(sender, " /%s list", new Object[] { command.getName() });
      Util.reply(sender, "     lists the currently scheduled shutdowns");
      break;
    case 1:
      Util.reply(sender, "Reloading...");
      this.plugin.settings.reloadConfig();
      this.plugin.scheduleAll();
      Util.reply(sender, "Configuration reloaded.");
      break;
    case 2:
      if (this.plugin.shutdownTimer != null) 
      {
        this.plugin.shutdownTimer.cancel();
        this.plugin.shutdownTimer.purge();
        this.plugin.shutdownTimer = null;
        this.plugin.shutdownImminent = false;

        Util.broadcast("Shutdown was aborted.");
      }
      Util.replyError(sender, "There is no impending shutdown. If you wish to remove");
      Util.replyError(sender, "a scheduled shutdown, remove it from the configuration");
      Util.replyError(sender, "and reload.");

      break;
    case 3:
      if (args.length < 2) {
        Util.replyError(sender, "Usage:");
        Util.replyError(sender, "   /as set <time>");
        Util.replyError(sender, "<time> can be either 'now' or a 24h time in HH:MM format.");
        return true;
      }

      Calendar stopTime = null;
      try {
        stopTime = this.plugin.scheduleShutdownTime(args[1]);
      } catch (Exception e) {
        Util.replyError(sender, "Usage:");
        Util.replyError(sender, "   /as set <time>");
        Util.replyError(sender, "<time> can be either 'now' or a 24h time in HH:MM format.");
      }
      if (stopTime != null) {
        Util.reply(sender, "Shutdown scheduled for %s", new Object[] { stopTime.getTime().toString() });
      }
      String timeString = "";

      for (Calendar shutdownTime : this.plugin.shutdownTimes) {
        if (((Calendar)this.plugin.shutdownTimes.first()).equals(shutdownTime))
          timeString = timeString
            .concat(String.format("%d:%02d", new Object[] { Integer.valueOf(shutdownTime.get(11)), 
            Integer.valueOf(shutdownTime.get(12)) }));
        else {
          timeString = timeString
            .concat(String.format(",%d:%02d", new Object[] { Integer.valueOf(shutdownTime.get(11)), 
            Integer.valueOf(shutdownTime.get(12)) }));
        }
      }

      this.plugin.settings.getConfig().set("times.shutdowntimes", timeString);

      break;
    case 4:
      if (this.plugin.shutdownTimes.size() != 0) 
      {
        Util.reply(sender, "Shutdowns scheduled at");
        for (Calendar shutdownTime : this.plugin.shutdownTimes)
          Util.reply(sender, "   %s", new Object[] { shutdownTime.getTime().toString() });
      }
      Util.replyError(sender, "没有自动重启计划.");

      break;
    case 5:
      Util.replyError(sender, "未知指令. 使用 /as help 来查看帮助.");
    }

    return true; }

	static enum SubCommand {
		HELP, RELOAD, CANCEL, SET, LIST, UNKNOWN;

		private static SubCommand toSubCommand(String str) {
			try {
				return valueOf(str);
			} catch (Exception localException) {
			}
			return HELP;
		}
	}
}