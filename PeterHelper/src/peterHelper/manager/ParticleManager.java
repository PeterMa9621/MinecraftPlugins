package peterHelper.manager;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class ParticleManager {

    public void particle1(Plugin plugin, Player player) {
        Bukkit.getScheduler().runTaskTimer(plugin, new BukkitRunnable() {
            Location location = player.getLocation();
            double t = 0;
            double r = 1;

            @Override
            public void run() {
                t = t + Math.PI / 8;
                double x = r * cos(t);
                double y = t;
                double z = r * sin(t);
                location.add(x, y ,z);
                Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(0, 127, 255), 1);
                player.spawnParticle(Particle.REDSTONE, location, 50, dustOptions);
                if(t>Math.PI * 4) {
                    t = 0;
                }
            }
        }, 0, 2);
    }




    public void particle2(Plugin plugin, Player player) {
        Bukkit.getScheduler().runTaskTimer(plugin, new BukkitRunnable() {
            double phi = 0;
            @Override
            public void run() {
                Location location = player.getLocation();
                phi += Math.PI/10;
                for(double theta=0; theta<=2*Math.PI; theta+=Math.PI/20) {
                    double r = 1.5;
                    double x = r*cos(theta)*sin(phi);
                    double y = r*cos(phi) + 1.5;
                    double z = r*sin(theta)*sin(phi);
                    location.add(x, y, z);
                    player.spawnParticle(Particle.FLAME, location, 0, 0, 0, 0, 1);
                    location.subtract(x, y, z);
                }
                if(phi>Math.PI) {
                    this.cancel();
                }
            }
        }, 0, 2);
    }
}
