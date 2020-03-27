package peterUtil.database;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;

public class ConfigStructureBuilder {

    private ConfigStructure root;
    private FileConfiguration config;

    public ConfigStructureBuilder(FileConfiguration config, ConfigStructure root){
        this.config = config;
        this.root = root;
    }

    public void build() {
        HashMap<String, Object> data = root.getData();
        for(String key:data.keySet()){
            config.set(key, data.get(key));
        }
    }
}
