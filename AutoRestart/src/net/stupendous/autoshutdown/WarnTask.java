package net.stupendous.autoshutdown;

import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import net.stupendous.autoshutdown.misc.Log;
import net.stupendous.autoshutdown.misc.Util;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitScheduler;

public class WarnTask extends TimerTask {
	protected final AutoShutdownPlugin plugin;
	protected final Log log;
	protected long seconds = 0L;

	public WarnTask(AutoShutdownPlugin plugin, long seconds) {
		this.plugin = plugin;
		this.log = plugin.log;
		this.seconds = seconds;
	}

	public void run() {
		this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
			public void run() {
				/*
				if (TimeUnit.SECONDS.toMinutes(WarnTask.this.seconds) > 0L) {
					if (TimeUnit.SECONDS.toMinutes(WarnTask.this.seconds) == 1L) {
						if (WarnTask.this.seconds
								- TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(WarnTask.this.seconds)) == 0L)
							Util.broadcast(WarnTask.this.plugin.settings.config.getString("messages.shutdownmessage")
									+ " in 1 " + WarnTask.this.plugin.settings.config.getString("messages.minute")
									+ "...");
						else {
							Util.broadcast(WarnTask.this.plugin.settings.config.getString("messages.shutdownmessage")
									+ " in 1 " + WarnTask.this.plugin.settings.config.getString("messages.minute")
									+ " %d " + WarnTask.this.plugin.settings.config.getString("messages.second")
									+ "s ...",
									new Object[] { Long.valueOf(WarnTask.this.seconds - TimeUnit.MINUTES
											.toSeconds(TimeUnit.SECONDS.toMinutes(WarnTask.this.seconds))) });
						}
					} else if (WarnTask.this.seconds
							- TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(WarnTask.this.seconds)) == 0L)
						Util.broadcast(
								WarnTask.this.plugin.settings.config.getString("messages.shutdownmessage") + " in %d "
										+ WarnTask.this.plugin.settings.config.getString("messages.minute") + "s ...",
								new Object[] { Long.valueOf(TimeUnit.SECONDS.toMinutes(WarnTask.this.seconds)) });
					else {
						Util.broadcast(
								WarnTask.this.plugin.settings.config.getString("messages.shutdownmessage") + " in %d "
										+ WarnTask.this.plugin.settings.config.getString("messages.minute") + "s %d "
										+ WarnTask.this.plugin.settings.config.getString("messages.second") + "s ...",
								new Object[] { Long.valueOf(TimeUnit.SECONDS.toMinutes(WarnTask.this.seconds)),
										Long.valueOf(WarnTask.this.seconds - TimeUnit.MINUTES
												.toSeconds(TimeUnit.SECONDS.toMinutes(WarnTask.this.seconds))) });
					}
				} else if (TimeUnit.SECONDS.toSeconds(WarnTask.this.seconds) == 1L)
					Util.broadcast(
							WarnTask.this.plugin.settings.config.getString("messages.shutdownmessage") + " NOW!");
				else
					Util.broadcast(
							WarnTask.this.plugin.settings.config.getString("messages.shutdownmessage") + " in %d "
									+ WarnTask.this.plugin.settings.config.getString("messages.second") + "s ...",
							new Object[] { Long.valueOf(WarnTask.this.seconds) });
			*/
			}
			
		});
		
	}
	
}