package dailyQuest.model;

import dailyQuest.DailyQuest;
import dailyQuest.config.ConfigManager;
import dailyQuest.manager.QuestManager;
import dailyQuest.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class QuestPlayer
{
	/**
	 *  When currentNumber equals to 0, it means this player does not receive any quests.
	 */
	int currentNumber = 0;
	int whatTheQuestIs = 0;
	int totalQuest = 0;
	String lastLogout = null;
	Player player;

	public QuestPlayer(Player player, int currentNumber, int whatTheQuestIs, int totalQuest, String lastLogout)
	{
		this.player = player;
		this.currentNumber = currentNumber;
		this.whatTheQuestIs = whatTheQuestIs;
		this.totalQuest = totalQuest;
		this.lastLogout = lastLogout;
		restoreMobQuest();
	}

	public QuestPlayer(Player player, int currentNumber, int whatTheQuestIs, int totalQuest)
	{
		this.player = player;
		this.currentNumber = currentNumber;
		this.whatTheQuestIs = whatTheQuestIs;
		this.totalQuest = totalQuest;
		restoreMobQuest();
	}

	private void restoreMobQuest() {
		if(isDoingQuest()) {
			Quest currentQuest = getCurrentQuest();
			if(currentQuest.getQuestInfo().getType().equalsIgnoreCase("mob")) {
				if(currentQuest.getQuestInfo().mobId!=null) {
					initMobQuest(currentQuest);
				}
			}
		}
	}

	public int getCurrentNumber() {
		return currentNumber;
	}

	public int getWhatTheQuestIs()
	{
		return whatTheQuestIs;
	}

	public int getTotalQuest()
	{
		return totalQuest;
	}

	public String getLastLogout()
	{
		return lastLogout;
	}

	public void setCurrentNumber(int newNumber)
	{
		currentNumber = newNumber;
	}

	public void setWhatTheQuestIs(int newWhatTheQuestIs)
	{
		whatTheQuestIs = newWhatTheQuestIs;
	}

	public void setTotalQuest(int newTotalQuest)
	{
		totalQuest = newTotalQuest;
	}

	public void setLastLogout(String newLastLogout)
	{
		lastLogout = newLastLogout;
	}

	public Player getPlayer() {
		return player;
	}

	public Quest getCurrentQuest() {
		return QuestManager.quests.get(whatTheQuestIs);
	}

	/**
	 * Finish a quest.
	 * Return True means can continue with the next quest.
	 * Return False means no more quests.
	 * @param plugin DailyQuest
	 * @return Boolean
	 */
	public Boolean finishQuestAndGetNext(DailyQuest plugin) {
		totalQuest ++;
		// If it is a mob quest, remove it
		QuestManager.mobQuests.remove(player.getUniqueId());
		getQuestReward(plugin);

		return getNextQuest();
	}

	public boolean getNextQuest() {
		if(totalQuest>= getDailyLimit()) {
			currentNumber = 0;
			whatTheQuestIs = -1;
			return false;
		}
		currentNumber ++;
		whatTheQuestIs = QuestManager.getRandomQuest();

		Quest nextQuest = getCurrentQuest();
		if(nextQuest.getQuestInfo().getType().equalsIgnoreCase("mob")) {
			initMobQuest(nextQuest);
		}
		return true;
	}

	private void initMobQuest(Quest nextQuest) {
		int amount = nextQuest.getQuestInfo().getMobAmount();
		String mobID = nextQuest.getQuestInfo().getMobId();
		// If mobId is null meaning the quest is broken, then we need to give a new quest to this player
		if(mobID==null) {
			currentNumber--;
			getNextQuest();
			return;
		}
		String mobName = nextQuest.getQuestInfo().getMobName();
		MobQuest mobQuest;
		if(mobName==null)
			mobQuest = new MobQuest(amount, 0, mobID);
		else
			mobQuest = new MobQuest(amount, 0, mobID, mobName);
		QuestManager.mobQuests.put(player.getUniqueId(), mobQuest);
	}

	public void getQuestReward(DailyQuest plugin) {
		// =======================================================
		// Calculate the final money (with adding random number depends on current quest number)
		Quest quest = getCurrentQuest();
		if(quest.getRewardMoney()>0)
		{
			int money = quest.getRewardMoney();
			money = Util.random((money/2)+1)+(money/2);
			int addition = Util.random((getCurrentNumber()/2)+1)*3;

			if(getCurrentNumber()%10==0) {
				int seriesFinishMoney = plugin.configManager.additionMoney;
				plugin.economy.depositPlayer(player, money+addition+seriesFinishMoney);
				player.sendMessage("§6[日常任务] §7你获得了§a"+(money+addition)+"§7金币作为奖励,并额外获得了§a"+seriesFinishMoney+"§7金币!");
			}
			else {
				plugin.economy.depositPlayer(player, money+addition);
				player.sendMessage("§6[日常任务] §7你获得了§a"+(money+addition)+"§7金币作为奖励!");
			}
		}

		// =======================================================
		// Get the random reward items and how many reward items this player can get and give them to this player
		int maxQuantity = Util.random(plugin.configManager.randomItemMaxQuantity) + 1;
		int extraRewardQuantity = 0;
		if(getCurrentNumber()%10==0) {
			extraRewardQuantity = plugin.configManager.extraRewardItemQuantity;
		}

		List<String> rewards = plugin.configManager.rewards;
		// Normal reward has a chance to get it
		for(int i=0; i<maxQuantity; i++) {
			if(Util.random(100) < (ConfigManager.chanceGetReward*100)) {
				int itemIndex = Util.random(rewards.size());
				String rewardCommand = rewards.get(itemIndex).replace("%player%", player.getName());
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), rewardCommand);
			}
		}

		// Extra reward comes per 10 quests
		if(extraRewardQuantity>0) {
			for(int i=0; i<extraRewardQuantity; i++) {
				int itemIndex = Util.random(rewards.size());
				String rewardCommand = rewards.get(itemIndex).replace("%player%", player.getName());
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), rewardCommand);
			}
			player.sendMessage("§6[日常任务] §7你额外获得了§c"+plugin.configManager.extraRewardItemQuantity +"§7个物品奖励");
		}

		// =======================================================
		// Show message
		if(!quest.getRewardMessage().equals(""))
			player.sendMessage("§6[日常任务] §e"+ quest.getRewardMessage());
	}

	public int getDailyLimit() {
		int questLimit = ConfigManager.defaultDailyLimit;

		for(String permission:ConfigManager.group.keySet()) {
			if(player.hasPermission("group."+permission)) {
				questLimit = ConfigManager.group.get(permission);
				break;
			}
		}
		return questLimit;
	}

	public Boolean canGetQuest() {
		return totalQuest < getDailyLimit();
	}

	public Boolean isDoingQuest() {
		return whatTheQuestIs>=0;
	}

	public void clearQuestData() {
		if(currentNumber!=0)
			currentNumber = 1;
		whatTheQuestIs = -1;
		totalQuest = 0;
	}

	public void giveUpCurrentQuest() {
		currentNumber = 0;
		whatTheQuestIs = -1;
	}
}
