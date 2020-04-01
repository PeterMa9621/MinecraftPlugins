package dps.rewardBox;

import dps.Dps;
import dps.model.DpsPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class RewardBoxListener implements Listener {
    Dps plugin;

    public RewardBoxListener(Dps plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onRewardBoxClick(InventoryClickEvent event) {
        if(event.getView().getTitle().equalsIgnoreCase(RewardBoxManager.rewardBoxTitle)){
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            DpsPlayer dpsPlayer = RewardBoxManager.getDpsPlayer(player.getUniqueId());
            int numRewards = dpsPlayer.getNumRewards();
            String dungeonName = dpsPlayer.getDungeonName();
            int index = event.getSlot();

            if(index < numRewards && index >= 0){
                ItemStack clickedItem = event.getCurrentItem();
                ItemMeta itemMeta = clickedItem.getItemMeta();
                if(itemMeta.getDisplayName().equalsIgnoreCase(RewardBoxManager.rewardTitle)){
                    RewardTable rewardTable = RewardBoxManager.getRewardTable(dungeonName);
                    Reward reward = rewardTable.getRandomReward();
                    String cmd = reward.getCmd();
                    cmd = cmd.replaceAll("%player%", player.getName());

                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
                    event.getInventory().setItem(index, reward.getIcon());
                }
            } else if(index == 26){
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

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        RewardBoxManager.removeRewardBoxPlayer(player);
    }
}
