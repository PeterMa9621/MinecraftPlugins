package checkInSystem.database;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class Database {
    private static StorageInterface instance = null;

    private static String databaseName = "minecraft";
    private static String userName = "root";
    private static String password = "mjy159357";
    private static String createTableQuery;

    private Database(DatabaseType databaseType, JavaPlugin plugin) {
        if(databaseType.equals(DatabaseType.MYSQL)){
            Database.instance = new MYSQLStorage(plugin, "check_in_system", databaseName, userName, password, createTableQuery);
        } else if(databaseType.equals(DatabaseType.YML)) {
            Database.instance = new YMLStorage(plugin);
        } else {
            Database.instance = null;
        }
    }

    public static void setConnectionInfo(String databaseName, String userName, String password, String createTableQuery) {
        Database.databaseName = databaseName;
        Database.userName = userName;
        Database.password = password;
        Database.createTableQuery = createTableQuery;
    }

    public static void setCreateTableQuery(String createTableQuery){
        Database.createTableQuery = createTableQuery;
    }

    public static StorageInterface getInstance(DatabaseType databaseType, JavaPlugin plugin) {
        if(Database.instance == null){
            new Database(databaseType, plugin);
        }
        return Database.instance;
    }

}
