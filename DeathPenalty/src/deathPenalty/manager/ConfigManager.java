package deathPenalty.manager;

import deathPenalty.DeathPenalty;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {
    private DeathPenalty plugin;
    public List<String> disabledWorlds;
    public double loseMoneyNumber;
    public boolean isLoseMoneyConstant;
    public boolean isLoseExpConstant;
    public int loseItemNumber;
    public boolean usingTitleForDeathMessage;
    public double loseExpNumber;
    public ConfigManager(DeathPenalty plugin) {
        this.plugin = plugin;
        disabledWorlds = new ArrayList<>();
        loseMoneyNumber = 0;
        isLoseMoneyConstant = true;
        isLoseExpConstant = true;
        loseExpNumber = 0;
    }

    public void loadConfig() throws IOException {
        File file = new File(plugin.getDataFolder(), "config.yml");
        FileConfiguration config;
        if(!file.exists()) {
            config = load(file);
            config.set("disabledWorlds", new ArrayList<String>() {{
                add("world");
            }});
            config.set("loseMoneyNumber", "1%");
            config.set("loseItemNumber", 1);
            config.set("loseExpNumber", "1");
            config.set("usingTitleForDeathMessage", "true");
            config.set("deathMessageTitle", "&4死亡惩罚");
            config.set("deathMessage", "&6因为你的死亡，{0}");
            config.set("loseMoneyMessage", "&6你已损失{0}点金币");
            config.set("loseItemMessage", "&6你已损失{0}个物品");
            config.set("loseExpMessage", "&6你已损失{0}点经验");
            config.save(file);
        }

        config = load(file);
        disabledWorlds = config.getStringList("disabledWorlds");

        String loseMoneyString = config.getString("loseMoneyNumber", "0");
        if(loseMoneyString.endsWith("%")) {
            isLoseMoneyConstant = false;
            this.loseMoneyNumber = Double.parseDouble(loseMoneyString.replace("%", "")) / 100;
        } else {
            this.loseMoneyNumber = Double.parseDouble(loseMoneyString.replace("%", ""));
        }

        String loseExpString = config.getString("loseExpNumber", "0");
        if(loseExpString.endsWith("%")) {
            isLoseExpConstant = false;
            this.loseExpNumber = Double.parseDouble(loseExpString.replace("%", "")) / 100;
        } else {
            this.loseExpNumber = Double.parseDouble(loseExpString.replace("%", ""));
        }

        loseItemNumber = config.getInt("loseItemNumber", 0);
        usingTitleForDeathMessage = config.getBoolean("usingTitleForDeathMessage", false);
        MessageManager.setDeathMessageTitle(config.getString("deathMessageTitle", ""));
        MessageManager.setDeathMessage(config.getString("deathMessage", ""));
        MessageManager.setLoseMoneyMessage(config.getString("loseMoneyMessage", ""));
        MessageManager.setLoseItemMessage(config.getString("loseItemMessage", ""));
        MessageManager.setLoseExpMessage(config.getString("loseExpMessage", ""));
    }

    public FileConfiguration load(File file)
    {
        if (!(file.exists()))
        { //假如文件不存在
            try   //捕捉异常，因为有可能创建不成功
            {
                file.createNewFile();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
        return YamlConfiguration.loadConfiguration(file);
    }
}
