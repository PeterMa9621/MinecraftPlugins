package clockGUI.Util;

import clockGUI.model.ClockGuiItem;
import clockGUI.model.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class InventoryUtil {
    public static Inventory initInventory(Player player, String inventoryName,
                                   HashMap<Integer, ClockGuiItem> guiItems, String guiId, HashMap<String, PlayerData> playerData)
    {
        int largest = 0;
        int time = 1;
        for(int i:guiItems.keySet())
        {
            if(i>largest)
            {
                largest=i;
            }
        }
        if(largest%9!=0)
        {
            time = (largest/9)+1;
        }
        else
        {
            time = largest/9;
        }

        Inventory inv = Bukkit.createInventory(player, 9*time, inventoryName);

        for(int i:guiItems.keySet())
        {
            if(guiItems.get(i).isHide())
                continue;

            ClockGuiItem clockGuiItem = guiItems.get(i);
            ItemStack itemStack = clockGuiItem.getItem();
            Util.replacePlaceholder(itemStack, player);
            if(clockGuiItem.getFrequency()>=1)
            {
                if(playerData.containsKey(player.getName()))
                {
                    if(playerData.get(player.getName()).getGuiInfo().containsKey(guiId))
                    {
                        if(playerData.get(player.getName()).getButtonInfo(guiId).containsKey(i))
                        {
                            int playerUsedNumber = playerData.get(player.getName()).getNumber(guiId, i);

                            int frequency = clockGuiItem.getFrequency();
                            if(playerUsedNumber<frequency)
                            {
                                inv.setItem(i, itemStack);
                            }
                        }
                    }
                    else
                    {
                        inv.setItem(i, itemStack);
                    }
                }
                else
                {
                    inv.setItem(i, itemStack);
                }
            }
            else
            {
                inv.setItem(i, itemStack);
            }
        }
        return inv;
    }
}
