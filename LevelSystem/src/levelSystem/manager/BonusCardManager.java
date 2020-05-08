package levelSystem.manager;

import levelSystem.LevelSystem;
import levelSystem.model.BonusCard;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class BonusCardManager {
    private LevelSystem plugin;
    public static HashMap<String, BonusCard> bonusCardHashMap;
    public BonusCardManager(LevelSystem plugin) {
        this.plugin = plugin;
        bonusCardHashMap = new HashMap<>();
    }

    public void addBonusCard(String cardName, BonusCard bonusCard) {
        bonusCardHashMap.put(cardName, bonusCard);
    }

    public BonusCard getBonusCard(String cardName) {
        return bonusCardHashMap.get(cardName);
    }

    public BonusCard getBonusCard(ItemStack itemStack) {
        for(BonusCard bonusCard:bonusCardHashMap.values()) {
            if(bonusCard.getItemStack().isSimilar(itemStack)) {
                return bonusCard;
            }
        }
        return null;
    }

    public void clear() {
        bonusCardHashMap.clear();
    }
}
