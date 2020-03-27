package peterUtil.database;

import org.bukkit.plugin.java.JavaPlugin;

public class Database {
    private static StorageInterface instance = null;

    private static String databaseName = "minecraft";
    private static String tableName = "";
    private static String userName = "root";
    private static String password = "mjy159357";
    private static String createTableQuery;

    private Database(DatabaseType databaseType, JavaPlugin plugin) {
        if(databaseType.equals(DatabaseType.MYSQL)){
            Database.instance = new MYSQLStorage(plugin, tableName, databaseName, userName, password, createTableQuery);
        } else if(databaseType.equals(DatabaseType.YML)) {
            Database.instance = new YMLStorage(plugin);
        } else {
            Database.instance = null;
        }
    }

    public static void setConnectionInfo(String databaseName, String tableName, String userName, String password, String createTableQuery) {
        Database.databaseName = databaseName;
        Database.tableName = tableName;
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
