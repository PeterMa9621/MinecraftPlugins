package net.stupendous.autoshutdown;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TreeSet;
import net.stupendous.autoshutdown.misc.Log;
import net.stupendous.autoshutdown.misc.Util;
import org.bukkit.Server;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class AutoShutdownPlugin extends JavaPlugin {
	public String pluginName;
	public Log log;
	protected ShutdownScheduleTask task = null;
	protected Timer backgroundTimer = null;
	protected Timer shutdownTimer = null;
	protected BukkitScheduler scheduler = null;
	protected boolean shutdownImminent = false;
	protected TreeSet<Calendar> shutdownTimes = new TreeSet();
	protected ArrayList<Integer> warnTimes = new ArrayList();

	SettingsManager settings = SettingsManager.getInstance();

	public void onDisable() {
		this.shutdownImminent = false;

		if (this.backgroundTimer != null) {
			this.backgroundTimer.cancel();
			this.backgroundTimer.purge();
			this.backgroundTimer = null;
		}

		if (this.shutdownTimer != null) {
			this.shutdownTimer.cancel();
			this.shutdownTimer.purge();
			this.shutdownTimer = null;
		}

		this.log.info("%s disabled.", new Object[] { this.settings.getDesc().getFullName() });
	}

	public void onEnable() {
		this.pluginName = getDescription().getName();
		this.log = new Log(this.pluginName);

		this.settings.setup(this);

		this.scheduler = getServer().getScheduler();
		this.shutdownImminent = false;
		this.shutdownTimes.clear();

		CommandExecutor autoShutdownCommandExecutor = new AutoShutdownCommand(this);
		getCommand("autoshutdown").setExecutor(autoShutdownCommandExecutor);
		getCommand("as").setExecutor(autoShutdownCommandExecutor);

		scheduleAll();

		Util.init(this, this.log);

		if (this.backgroundTimer != null) {
			this.backgroundTimer.cancel();
			this.backgroundTimer.purge();
			this.backgroundTimer = null;
		}

		this.backgroundTimer = new Timer();

		if (this.shutdownTimer != null) {
			this.shutdownTimer.cancel();
			this.shutdownTimer.purge();
			this.shutdownTimer = null;
		}

		Calendar now = Calendar.getInstance();
		now.set(13, 0);
		now.add(12, 1);

		now.add(14, 50);
		try {
			this.backgroundTimer.scheduleAtFixedRate(new ShutdownScheduleTask(this), now.getTime(), 60000L);
		} catch (Exception e) {
			this.log.severe("Failed to schedule AutoShutdownTask: %s", new Object[] { e.getMessage() });
		}

		this.log.info(this.pluginName + " enabled!");
	}

	protected void scheduleAll() {
		this.shutdownTimes.clear();
		this.warnTimes.clear();

		String[] shutdownTimeStrings = null;
		try {
			shutdownTimeStrings = this.settings.getConfig().getString("times.shutdowntimes").split(",");
		} catch (Exception e) {
			shutdownTimeStrings[0] = this.settings.getConfig().getString("times.shutdowntimes");
		}
		try {
			Object localObject;
			for (String timeString : shutdownTimeStrings) {
				localObject = scheduleShutdownTime(timeString);
			}

			String[] strings = getConfig().getString("times.warntimes").split(",");
			for (String warnTime : strings)
				this.warnTimes.add(Integer.decode(warnTime));
		} catch (Exception e) {
			this.log.severe("Unable to configure Auto Shutdown using the configuration file.");
			this.log.severe("Is the format of shutdowntimes correct? It should be only HH:MM.");
			this.log.severe("Error: %s", new Object[] { e.getMessage() });
		}
	}

	protected Calendar scheduleShutdownTime(String timeSpec) throws Exception {
		if (timeSpec == null) {
			return null;
		}
		if (timeSpec.matches("^now$")) {
			Calendar now = Calendar.getInstance();
			int secondsToWait = getConfig().getInt("times.gracetime", 20);
			now.add(13, secondsToWait);

			this.shutdownImminent = true;
			this.shutdownTimer = new Timer();

			for (Integer warnTime : this.warnTimes) {
				long longWarnTime = warnTime.longValue() * 1000L;

				if (longWarnTime <= secondsToWait * 1000) {
					this.shutdownTimer.schedule(new WarnTask(this, warnTime.longValue()),
							secondsToWait * 1000 - longWarnTime);
				}

			}

			this.shutdownTimer.schedule(new ShutdownTask(this), now.getTime());
			Util.broadcast("The server has been scheduled for immediate shutdown.");

			return now;
		}

		if (!(timeSpec.matches("^[0-9]{1,2}:[0-9]{2}$"))) {
			throw new Exception("Incorrect time specification. The format is HH:MM in 24h time.");
		}

		Calendar now = Calendar.getInstance();
		Calendar shutdownTime = Calendar.getInstance();

		String[] timecomponent = timeSpec.split(":");
		shutdownTime.set(11, Integer.valueOf(timecomponent[0]).intValue());
		shutdownTime.set(12, Integer.valueOf(timecomponent[1]).intValue());
		shutdownTime.set(13, 0);
		shutdownTime.set(14, 0);

		if (now.compareTo(shutdownTime) >= 0) {
			shutdownTime.add(5, 1);
		}

		this.shutdownTimes.add(shutdownTime);

		return shutdownTime;
	}

	public void kickAll() {
		if (!(getConfig().getBoolean("kickonshutdown", true))) {
			return;
		}

		this.log.info("Kicking all players ...");
		this.log.info(this.settings.getConfig().getString("messages.kickreason"));

		for (Player player : getServer().getOnlinePlayers()) {
			this.log.info("Kicking player %s.", new Object[] { player.getName() });
			player.kickPlayer(this.settings.config.getString("messages.kickreason"));
		}
	}

	public SettingsManager getSettings() {
		return this.settings;
	}
}