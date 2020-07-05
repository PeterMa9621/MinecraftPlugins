package dps.rewardBox;

import dps.model.DpsPlayer;
import dps.model.DpsPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class RewardBoxManager {
    /**
     *  Key is player's UUID, value is the DpsPlayer class
     */
    public static HashMap<UUID, DpsPlayer> rewardBoxPlayers = new HashMap<>();

    /**
     *  Key is dungeon's name, value is the RewardTable class
     */
    public static HashMap<String, RewardTable> rewards = new HashMap<>();

    /**
     *  This is the array that stores all normal rewards
     */
    public static ArrayList<Reward> normalRewards = new ArrayList<>();
    public static double normalRewardProb = 0.3;

    /**
     *  The maximum number of rewards that a player can get
     */
    public static Integer maxBonusRewards = 9;
    public static String rewardBoxTitle = "§5获取奖励";
    public static String rewardTitle = "§a点击领取奖励";
    public static String bonusRewardTitle = "§a点击领取额外奖励";

    public static String expCommand;

    public static void showRewardBox(DpsPlayer dpsPlayer) {
        rewardBoxPlayers.put(dpsPlayer.getPlayer().getUniqueId(), dpsPlayer);

        Player player = dpsPlayer.getPlayer();
        Inventory inventory = Bukkit.createInventory(null, 27, rewardBoxTitle);
        ItemStack itemStack = new ItemStack(Material.IRON_DOOR);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName("§6退出");
        //itemMeta.setCustomModelData(13);
        itemStack.setItemMeta(itemMeta);
        inventory.setItem(26, itemStack);

        // Add normal rewards to the reward box
        int numReward = getNumReward(dpsPlayer);
        for(int i=0; i<numReward; i++){
            itemStack = new ItemStack(Material.CHEST);
            itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(rewardTitle);
            //itemMeta.setCustomModelData(42);
            itemStack.setItemMeta(itemMeta);
            inventory.setItem(i, itemStack);
        }
        // Add extra bonus rewards to the reward box
        for(int i=0; i<dpsPlayer.getNumBonusReward(); i++) {
            itemStack = new ItemStack(Material.ENDER_CHEST);
            itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(bonusRewardTitle);
            //itemMeta.setCustomModelData(45);
            itemStack.setItemMeta(itemMeta);
            inventory.setItem(i+numReward, itemStack);
        }

        player.openInventory(inventory);
    }

    public static void removeRewardBoxPlayer(Player player) {
        rewardBoxPlayers.remove(player.getUniqueId());
    }

    public static Boolean canCloseRewardBox(Player player) {
        return !RewardBoxManager.rewardBoxPlayers.containsKey(player.getUniqueId());
    }

    public static DpsPlayer getDpsPlayer(UUID uuid) {
        return RewardBoxManager.rewardBoxPlayers.get(uuid);
    }

    public static Integer getNumReward(DpsPlayer dpsPlayer) {
        Integer groupSize = dpsPlayer.getGroupSize();
        Integer rank = dpsPlayer.getRank();

        int numRewards = 1;
        if(groupSize == 1){
            dpsPlayer.setNumRewards(numRewards);
        } else {
            numRewards = (int) Math.round(groupSize / ((double)rank) / 1.2);
            if(numRewards <= 0){
                numRewards = 1;
            }
            else {
                numRewards = Math.min(4, numRewards);
            }
            dpsPlayer.setNumRewards(numRewards);
        }
        return numRewards;
    }

    public static RewardTable getRewardTable(String dungeonName) {
        return rewards.get(dungeonName);
    }

    public static void updateDpsPlayer(UUID uuid, DpsPlayer dpsPlayer) {
        UUID worldId = dpsPlayer.getDungeonWorldId();
        DpsPlayerManager.dpsData.get(worldId).put(uuid, dpsPlayer);
    }
}
