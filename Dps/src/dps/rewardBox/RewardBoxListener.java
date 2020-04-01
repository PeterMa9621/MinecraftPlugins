package dps.rewardBox;

import dps.Dps;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class RewardBoxListener implements Listener {
    Dps plugin;

    public RewardBoxListener(Dps plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onRewardBoxClick(InventoryClickEvent event) {
        if(event.getView().getTitle().equalsIgnoreCase(RewardBoxManager.rewardBoxTitle)){
            Player player = (Player) event.getWhoClicked();
            int index = event.getSlot();
            if(index == 26){
                RewardBoxManager.removeRewardBoxPlayer(player);
                player.closeInventory();
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if(event.getView().getTitle().equalsIgnoreCase(RewardBoxManager.rewardBoxTitle)){
            Player player = (Player) event.getPlayer();
            if(!RewardBoxManager.canCloseRewardBox(player)){
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.openInventory(event.getInventory());
                    }
                }.runTaskLater(plugin, 1);
            }
        }
    }
}
