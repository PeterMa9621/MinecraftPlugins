package betterWeapon.manager;

import betterWeapon.BetterWeapon;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

public class IntensifyManager {
    private BetterWeapon plugin;
    private ItemStack itemForIntensify;
    private ArrayList<Integer> possibilityList = new ArrayList<Integer>();
    private HashMap<String, String> rule = new HashMap<>();
    private int price;
    public IntensifyManager(BetterWeapon plugin) {
        this.plugin = plugin;
    }

    public void setItem(ItemStack itemForIntensify) {
        this.itemForIntensify = itemForIntensify;
    }

    public ItemStack getItem() {
        return itemForIntensify;
    }

    public void addPossibility(int possibility) {
        possibilityList.add(possibility);
    }

    public int getPossibility(int level) {
        return possibilityList.get(level);
    }

    public ArrayList<Integer> getPossibilityList() {
        return possibilityList;
    }

    public void addRule(String equipId, String enchantId) {
        rule.put(equipId.toUpperCase(), enchantId.toLowerCase());
    }

    public Enchantment getEnchant(String equipId) {
        String enchantId = rule.get(equipId.toUpperCase());
        return Enchantment.getByKey(NamespacedKey.minecraft(enchantId));
    }

    public void clear() {
        if(possibilityList!=null)
            possibilityList.clear();
        if(rule!=null)
            rule.clear();
    }

    public HashMap<String, String> getRule() {
        return rule;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
