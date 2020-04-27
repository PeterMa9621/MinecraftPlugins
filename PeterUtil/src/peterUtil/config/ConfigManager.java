package peterUtil.config;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import peterUtil.PeterUtil;

import java.io.File;
import java.io.IOException;

public class ConfigManager {
    private PeterUtil plugin;
    public boolean enableNotification;
    public ConfigManager(PeterUtil plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        File folder = new File(plugin.getDataFolder(), "");
        if(!folder.exists())
            folder.mkdir();
        File file = new File(plugin.getDataFolder(), "/config.yml");
        FileConfiguration config;
        if(!file.exists()){
            config = load(file);
            config.set("enableNotification", true);

            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            loadConfig();
            return;
        }

        config = load(file);
        enableNotification = config.getBoolean("enableNotification", true);
    }

    public static FileConfiguration load(File file)
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
