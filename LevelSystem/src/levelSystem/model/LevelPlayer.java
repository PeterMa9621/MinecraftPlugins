package levelSystem.model;

import levelSystem.event.LevelUpEvent;
import levelSystem.manager.BonusCardManager;
import levelSystem.manager.ConfigManager;
import levelSystem.manager.ExpManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.Calendar;

public class LevelPlayer
{
	private Player player;
	private Integer level = 0;
	private Integer currentExp = 0;
	private LocalDateTime bonusCardExpiredTime;
	private String bonusName;
	private int expInOneMinute;
	public LevelPlayer(Player player, Integer level, Integer currentExp, LocalDateTime bonusCardExpiredTime, String bonusName) {
		this.player=player;
		this.level=level;
		this.currentExp=currentExp;
		this.bonusCardExpiredTime = bonusCardExpiredTime;
		this.bonusName = bonusName;
	}
	
	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
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

	public int addExp(int exp) {
		if(!isBonusCardExpired()) {
			BonusCard bonusCard = getBonusCard();
			double times = bonusCard.getTimes();
			exp = (int) (exp * times);
		}
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
		return exp;
	}

	public void levelUp(int overflowExp) {
		this.currentExp = overflowExp;
		this.level += 1;
	}

	public Boolean canLevelUp() {
		return this.currentExp >= getRequiredExp();
	}

	public void clearLevel() {
		this.level = 1;
		this.currentExp = 0;
	}

	public LocalDateTime getBonusCardExpiredTime() {
		return bonusCardExpiredTime;
	}

	public void setBonusCardExpiredTime(LocalDateTime bonusCardExpiredTime) {
		this.bonusCardExpiredTime = bonusCardExpiredTime;
	}

	public boolean isBonusCardExpired() {
		if(this.bonusCardExpiredTime==null)
			return true;
		LocalDateTime now = LocalDateTime.now();
		return now.isAfter(this.bonusCardExpiredTime);
	}

	public String getBonusCardName() {
		if(isBonusCardExpired())
			return null;
		return bonusName;
	}

	public BonusCard getBonusCard() {
		return BonusCardManager.bonusCardHashMap.get(bonusName);
	}

	public void setBonusName(String bonusName) {
		this.bonusName = bonusName;
	}

	public int getExpInOneMinute() {
		return expInOneMinute;
	}

	public void addExpInOneMinute(int amount) {
		expInOneMinute += amount;
	}

	public void resetExpInOneMinute() {
		expInOneMinute = 0;
	}

	public boolean canGetExpInThisMinute() {
		return expInOneMinute < ConfigManager.maxExpPerMinute || !isBonusCardExpired();
	}
}
