package peterUtil.database;

import org.bukkit.plugin.java.JavaPlugin;

public class Database {

    public static StorageInterface getInstance(DatabaseType databaseType, JavaPlugin plugin) {
        if(databaseType.equals(DatabaseType.MYSQL)){
            return new MYSQLStorage(plugin);
        } else if(databaseType.equals(DatabaseType.YML)) {
            return new YMLStorage(plugin);
        } else {
            return null;
        }
    }

}
