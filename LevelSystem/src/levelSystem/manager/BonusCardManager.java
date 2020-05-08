package levelSystem.manager;

import levelSystem.LevelSystem;
import levelSystem.model.BonusCard;

import java.util.HashMap;

public class BonusCardManager {
    private LevelSystem plugin;
    private HashMap<String, BonusCard> bonusCardHashMap;
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
}
