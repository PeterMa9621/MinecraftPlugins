package deathPenalty.manager;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MessageManager {
    private static String DeathMessage;
    private static String LoseMoneyMessage;
    private static String LoseItemMessage;
    private static String LoseExpMessage;
    private static String DeathMessageTitle;

    public static String getDeathMessage(int number, double money, int exp) {
        String subMessage = "";
        if(number > 0)
            subMessage += getLoseItemMessage(number);
        if(money > 0)
            subMessage += getLoseMoneyMessage(money);
        if(exp > 0)
            subMessage += getLoseExpMessage(exp);

        return DeathMessage.replace("{0}", subMessage);
    }

    public static String getLoseItemMessage(int number) {
        return LoseItemMessage.replace("{0}", "" + number);
    }

    public static String getLoseMoneyMessage(double money) {
        return LoseMoneyMessage.replace("{0}", "" + money);
    }

    public static String getDeathMessageTitle() {
        return DeathMessageTitle;
    }

    public static String getLoseExpMessage(int exp) {
        return LoseExpMessage.replace("{0}", "" + exp);
    }

    public static void setDeathMessage(String deathMessage) {
        DeathMessage = deathMessage.replace('&', ChatColor.COLOR_CHAR);
    }

    public static void setLoseItemMessage(String loseItemMessage) {
        LoseItemMessage = loseItemMessage.replace('&', ChatColor.COLOR_CHAR);
    }

    public static void setLoseMoneyMessage(String loseMoneyMessage) {
        LoseMoneyMessage = loseMoneyMessage.replace('&', ChatColor.COLOR_CHAR);
    }

    public static void setDeathMessageTitle(String deathMessageTitle) {
        DeathMessageTitle = deathMessageTitle.replace('&', ChatColor.COLOR_CHAR);
    }

    public static void setLoseExpMessage(String loseExpMessage) {
        LoseExpMessage = loseExpMessage.replace('&', ChatColor.COLOR_CHAR);
    }
}
