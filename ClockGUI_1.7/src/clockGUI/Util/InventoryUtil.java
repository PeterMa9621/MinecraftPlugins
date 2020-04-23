package clockGUI.Util;

import clockGUI.model.ClockGuiItem;
import clockGUI.model.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;

public class InventoryUtil {
    public static Inventory initInventory(Player player, String inventoryName,
                                   HashMap<Integer, ClockGuiItem> guiItems, int guiNumber, HashMap<String, PlayerData> playerData)
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

            if(guiItems.get(i).getFrequency()>=1)
            {
                if(playerData.containsKey(player.getName()))
                {
                    if(playerData.get(player.getName()).getGuiInfo().containsKey(guiNumber))
                    {
                        if(playerData.get(player.getName()).getButtonInfo(guiNumber).containsKey(i))
                        {
                            int playerUsedNumber = playerData.get(player.getName()).getNumber(guiNumber, i);

                            int frequency = guiItems.get(i).getFrequency();
                            if(playerUsedNumber<frequency)
                            {
                                inv.setItem(i, guiItems.get(i).getItem());
                            }
                        }
                    }
                    else
                    {
                        inv.setItem(i, guiItems.get(i).getItem());
                    }
                }
                else
                {
                    inv.setItem(i, guiItems.get(i).getItem());
                }
            }
            else
            {
                inv.setItem(i, guiItems.get(i).getItem());
            }
        }
        return inv;
    }
}
