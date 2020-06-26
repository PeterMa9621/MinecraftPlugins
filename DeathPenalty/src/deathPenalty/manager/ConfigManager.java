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
            config.set("deathMessageTitle", "&4�����ͷ�");
            config.set("deathMessage", "&6��Ϊ���������{0}");
            config.set("loseMoneyMessage", "&6������ʧ{0}����");
            config.set("loseItemMessage", "&6������ʧ{0}����Ʒ");
            config.set("loseExpMessage", "&6������ʧ{0}�㾭��");
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
        { //�����ļ�������
            try   //��׽�쳣����Ϊ�п��ܴ������ɹ�
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
