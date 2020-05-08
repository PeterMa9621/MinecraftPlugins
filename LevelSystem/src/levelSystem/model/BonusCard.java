package levelSystem.model;

import org.bukkit.inventory.ItemStack;

public class BonusCard {
    private ItemStack itemStack;
    private double times;
    /**
     *  In minutes
     */
    private int duration;
    private String name;
    public BonusCard(ItemStack itemStack, double times, int duration, String name) {
        this.itemStack = itemStack;
        this.times = times;
        this.duration = duration;
        this.name = name;
    }

    public double getTimes() {
        return times;
    }

    public int getDuration() {
        return duration;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public String getName() {
        return name;
    }
}
