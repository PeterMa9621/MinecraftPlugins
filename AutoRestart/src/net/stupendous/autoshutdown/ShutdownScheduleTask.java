package net.stupendous.autoshutdown;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import net.stupendous.autoshutdown.misc.Util;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitScheduler;

public class ShutdownScheduleTask extends TimerTask {
	protected AutoShutdownPlugin plugin = null;

	ShutdownScheduleTask(AutoShutdownPlugin instance) {
		this.plugin = instance;
	}

	public void run() {
		this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
			public void run() {
				ShutdownScheduleTask.this.runTask();
			}
		});
	}

	private void runTask() {
		if (this.plugin.shutdownImminent) {
			return;
		}
		Calendar now = Calendar.getInstance();

		long firstWarning = ((Integer) this.plugin.warnTimes.get(0)).intValue() * 1000;

		for (Calendar cal : this.plugin.shutdownTimes)
			if (cal.getTimeInMillis() - now.getTimeInMillis() <= firstWarning) {
				this.plugin.shutdownImminent = true;
				this.plugin.shutdownTimer = new Timer();

				for (Integer warnTime : this.plugin.warnTimes) {
					long longWarnTime = warnTime.longValue() * 1000L;

					if (longWarnTime <= cal.getTimeInMillis() - now.getTimeInMillis()) {
						this.plugin.shutdownTimer.schedule(new WarnTask(this.plugin, warnTime.longValue()),
								cal.getTimeInMillis() - now.getTimeInMillis() - longWarnTime);
					}

				}

				this.plugin.shutdownTimer.schedule(new ShutdownTask(this.plugin), cal.getTime());

				//Util.broadcast(this.plugin.settings.config.getString("messages.shutdownmessage") + " ÓÚ %s",
				//		new Object[] { cal.getTime().toString() });

				return;
			}
	}
}