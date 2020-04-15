package levelSystem.model;

import levelSystem.event.LevelUpEvent;
import levelSystem.manager.ConfigManager;
import levelSystem.manager.ExpManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class LevelPlayer
{
	private Player player;
	private Integer level = 0;
	private Integer currentExp = 0;
	public LevelPlayer(Player player, Integer level, Integer currentExp) {
		this.player=player;
		this.level=level;
		this.currentExp=currentExp;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public int getLevel() {
		return level;
	}
	
	public int getCurrentExp() {
		return currentExp;
	}
	
	public void setLevel(int newLevel) {
		level = newLevel;
	}
	
	public void setCurrentExp(int newCurrentExp) {
		currentExp = newCurrentExp;
	}

	public Integer getRequiredExp() {
		return ExpManager.getExp(level);
	}

	public void addExp(int exp) {
		int requiredExp = ExpManager.getExp(level);
		if(currentExp + exp < requiredExp) {
			currentExp += exp;
		} else {
			if(level == ConfigManager.maxLevel) {
				currentExp = requiredExp;
			} else {
				int overflowExp = currentExp + exp - requiredExp;
				levelUp(overflowExp);
				LevelUpEvent levelUpEvent = new LevelUpEvent(this);
				Bukkit.getServer().getPluginManager().callEvent(levelUpEvent);
			}
		}
	}

	public void levelUp(int overflowExp) {
		this.currentExp += overflowExp;
		this.level += 1;
	}

	public Boolean canLevelUp() {
		return this.currentExp >= getRequiredExp();
	}

	public void clearLevel() {
		this.level = 1;
		this.currentExp = 0;
	}
}
