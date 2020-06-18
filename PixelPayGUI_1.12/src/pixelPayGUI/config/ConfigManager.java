package pixelPayGUI.config;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import pixelPayGUI.PixelPayGUI;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ConfigManager {
    private PixelPayGUI plugin;
    public ArrayList<String> kits = new ArrayList<>();
    public ConfigManager(PixelPayGUI plugin) {
        this.plugin = plugin;
    }

    public void loadKitInfo() {
        File kitPath = new File(plugin.getDataFolder().getParent(), "/PixelPay/shop.yml");
        FileConfiguration config = load(kitPath);
        kits.clear();
        kits.addAll(config.getKeys(false));
    }

    private FileConfiguration load(File file) {
        if (!(file.exists())) {
            try {
                file.createNewFile();
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }
        return YamlConfiguration.loadConfiguration(file);
    }
}
