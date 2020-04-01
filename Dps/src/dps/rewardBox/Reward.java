package dps.rewardBox;

import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class Reward {
    private double chance;
    private ItemStack icon;
    private String cmd;

    public Reward(double chance, ItemStack icon, String cmd) {
        this.chance = chance;
        this.icon = icon;
        this.cmd = cmd;
    }

    public double getChance() {
        return chance;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public String getCmd() {
        return cmd;
    }
}
