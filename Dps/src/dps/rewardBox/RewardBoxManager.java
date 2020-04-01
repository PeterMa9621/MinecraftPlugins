package dps.rewardBox;

import dps.model.DpsPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.UUID;

public class RewardBoxManager {
    public static HashMap<UUID, DpsPlayer> rewardBoxPlayers = new HashMap<>();
    public static String rewardBoxTitle = "§5获取奖励";

    public static void showRewardBox(DpsPlayer dpsPlayer) {
        if(!rewardBoxPlayers.containsKey(dpsPlayer.getPlayer().getUniqueId()))
            rewardBoxPlayers.put(dpsPlayer.getPlayer().getUniqueId(), dpsPlayer);

        Player player = dpsPlayer.getPlayer();
        Inventory inventory = Bukkit.createInventory(null, 27, rewardBoxTitle);
        ItemStack itemStack = new ItemStack(Material.ACACIA_DOOR);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName("§6退出");
        itemStack.setItemMeta(itemMeta);
        inventory.setItem(26, itemStack);

        int numReward = getNumReward(dpsPlayer);
        for(int i=0; i<numReward; i++){
            itemStack = new ItemStack(Material.ORANGE_WOOL);
            itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName("§a点击领取奖励");
            itemStack.setItemMeta(itemMeta);
            inventory.setItem(i, itemStack);
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

        if(groupSize == 1){
            return 1;
        } else {
            int num = (int) (groupSize / rank / 1.5);
            if(num <= 0)
                return 1;
            else
                return Math.min(4, num);
        }
    }
}
