package levelSystem.listener;

import levelSystem.LevelSystem;
import levelSystem.model.BonusCard;
import levelSystem.model.LevelPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;

public class BonusCardListener implements Listener {
    private LevelSystem plugin;
    public BonusCardListener(LevelSystem plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerUseBonusCard(PlayerInteractEvent event) {
        Action action = event.getAction();
        if(action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
            ItemStack itemStack = event.getItem();
            if(itemStack!=null && itemStack.hasItemMeta() && itemStack.getItemMeta().hasLore()) {
                BonusCard bonusCard = plugin.bonusCardManager.getBonusCard(itemStack);
                if(bonusCard!=null) {
                    Player player = event.getPlayer();
                    LevelPlayer levelPlayer = plugin.levelPlayerManager.getLevelPlayer(player);
                    if(!levelPlayer.isBonusCardExpired()) {
                        player.sendMessage("��6[�ȼ�ϵͳ] ��f�㵱ǰ������ʹ�õľ��鿨�����ھ��鿨ʱ���������ʹ�ã�");
                        return;
                    }
                    levelPlayer.setBonusName(bonusCard.getName());
                    levelPlayer.setBonusCardExpiredTime(LocalDateTime.now().plusMinutes(bonusCard.getDuration()));
                    itemStack.setAmount(itemStack.getAmount()-1);
                    String message = "��6[�ȼ�ϵͳ] ��f%.1f�����鿨����Ч������ʱ�䣺%d����";
                    player.sendMessage(String.format(message, bonusCard.getTimes(), bonusCard.getDuration()));
                }
            }
        }
    }
}
