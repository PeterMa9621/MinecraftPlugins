package betterWeapon.manager;

import betterWeapon.BetterWeapon;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class AssistantManager {
    private BetterWeapon plugin;
    private ArrayList<ItemStack> assistantsList = new ArrayList<ItemStack>();
    public AssistantManager(BetterWeapon plugin) {
        this.plugin = plugin;
    }

    public void addAssistant(ItemStack assistant) {
        assistantsList.add(assistant);
    }

    public ArrayList<ItemStack> getAssistantsList() {
        return assistantsList;
    }

    public ItemStack getAssistant(int index) {
        return assistantsList.get(index);
    }

    public void clear() {
        if(assistantsList!=null)
            assistantsList.clear();
    }
}
