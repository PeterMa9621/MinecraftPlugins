package deathPenalty.listener;

import deathPenalty.DeathPenalty;
import deathPenalty.manager.ConfigManager;
import deathPenalty.manager.ExperienceManager;
import deathPenalty.manager.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.SplittableRandom;

public class DeathListener implements Listener
{
	private DeathPenalty plugin;
	private ConfigManager configManager;

	public DeathListener(DeathPenalty plugin)
	{
		this.plugin = plugin;
		this.configManager = plugin.configManager;

	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) throws IOException {
		Player player = event.getEntity();
		if(configManager.disabledWorlds.contains(player.getWorld().getName()))
			return;

		double loseMoneyNumber = configManager.loseMoneyNumber;
		int loseItemNumber = configManager.loseItemNumber;
		boolean isLoseMoneyConstant = configManager.isLoseMoneyConstant;
		boolean isLoseExpConstant = configManager.isLoseExpConstant;
		boolean usingTitleForDeathMessage = configManager.usingTitleForDeathMessage;

		double balance = plugin.economy.getBalance(player);
		double loseMoney;
		if(isLoseMoneyConstant)
			loseMoney = loseMoneyNumber;
		else
			loseMoney = balance * loseMoneyNumber;
		loseMoney = Math.round(loseMoney * 100.0) / 100.0;
		if(loseMoney > 0)
			plugin.economy.withdrawPlayer(player, loseMoney);

		if(loseItemNumber > 0)
			removeRandomItem(player);

		int totalExp = ExperienceManager.getTotalExperience(player);
		int loseExpNumber;
		if(isLoseExpConstant)
			loseExpNumber = (int)configManager.loseExpNumber;
		else
			loseExpNumber = (int)(totalExp * configManager.loseExpNumber);
		ExperienceManager.setTotalExperience(player, ExperienceManager.getTotalExperience(player) - loseExpNumber);

		String deathMessage = MessageManager.getDeathMessage(loseItemNumber, loseMoney, loseExpNumber);
		if(!deathMessage.equalsIgnoreCase(""))
			player.sendMessage(deathMessage);

		if(usingTitleForDeathMessage)
			player.sendTitle(MessageManager.getDeathMessageTitle(), deathMessage, 20, 60, 20);
	}

	private void removeRandomItem(Player player) {
		ArrayList<Integer> indexList = getAllItemsIndex(player.getInventory());
		int number = indexList.size();

		SplittableRandom random = new SplittableRandom();
		int removeIndex = indexList.get(random.nextInt(number));
		player.getInventory().setItem(removeIndex, null);
	}

	private ArrayList<Integer> getAllItemsIndex(Inventory inventory) {
		ArrayList<Integer> indexList = new ArrayList<>();
		for(int i=0; i<inventory.getSize(); i++){
			if(inventory.getItem(i)!=null)
				indexList.add(i);
		}
		return indexList;
	}
}
