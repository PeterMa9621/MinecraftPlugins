package peterUtil.database;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class YMLStorage implements StorageInterface{
    private File dataFolderPath;

    public YMLStorage(JavaPlugin plugin){
        dataFolderPath = plugin.getDataFolder();
        File file=new File(dataFolderPath, "/data");
        if(!file.exists())
            file.mkdir();
    }

    @Override
    public void connect(String databaseName, String tableName, String userName, String password, String createTableQuery) {

    }

    @Override
    public void close() {

    }

    public void store(UUID uniqueId, HashMap<String, Object> data) throws IOException {
        File file = new File(this.dataFolderPath, "/data/"+uniqueId.toString()+".yml");

        FileConfiguration config = load(file);

        for(String key:data.keySet()){
            config.set(key, data.get(key));
        }

        config.save(file);
    }

    @Override
    public HashMap<String, Object> get(UUID uniqueId, String[] keys) {
        File file = new File(this.dataFolderPath, "/data/"+uniqueId.toString()+".yml");
        if(!file.exists())
            return null;

        FileConfiguration config = load(file);
        HashMap<String, Object> result = new HashMap<>();

        for(String key:keys){
            result.put(key, config.get(key));
        }

        return result;
    }

    @Override
    public HashMap<UUID, HashMap<String, Object>> getAll() {
        return null;
    }

    private FileConfiguration load(File file)
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
    private FileConfiguration load(String path)
    {
        File file=new File(path);
        if(!file.exists())
        {
            try
            {
                file.createNewFile();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
        return YamlConfiguration.loadConfiguration(new File(path));
    }
}
