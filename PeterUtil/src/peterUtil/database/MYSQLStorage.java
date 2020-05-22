package peterUtil.database;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitTask;
import peterUtil.PeterUtil;
import peterUtil.database.queryBuilder.QueryBuilderFactory;
import peterUtil.database.queryBuilder.QueryBuilderInterface;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.UUID;

public class MYSQLStorage implements StorageInterface{
    private JavaPlugin plugin;
    private Connection connection;
    private Statement statement;

    private String databaseName;
    private String tableName;
    private String userName;
    private String password;
    private String createTableQuery;

    private String prefix = "[" + ChatColor.YELLOW + "PeterUtil" + ChatColor.RESET + "] " + " - ";
    private boolean hasInitDatabase = false;
    private BukkitTask closeConnectionTask;

    public MYSQLStorage(JavaPlugin plugin){
        this.plugin = plugin;
    }

    public void connect(String databaseName, String tableName, String userName, String password, String createTableQuery) {
        this.tableName = tableName;
        this.databaseName = databaseName;
        this.userName = userName;
        this.password = password;
        this.createTableQuery = createTableQuery;

        try{
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/" + databaseName + "?allowPublicKeyRetrieval=true&useSSL=false", userName, password);

            if(PeterUtil.configManager.enableNotification)
                Bukkit.getConsoleSender().sendMessage(prefix + plugin.getName() + " Database connected!");
            if(!hasInitDatabase) {
                initDatabase();
                hasInitDatabase = true;
            }
        } catch(ClassNotFoundException | SQLException c){
            c.printStackTrace();
            throw new IllegalStateException(c);
        }
    }

    public void initDatabase() throws SQLException {
        statement = connection.createStatement();
        statement.executeUpdate(createTableQuery);
        connection.close();
        if(PeterUtil.configManager.enableNotification)
            Bukkit.getConsoleSender().sendMessage(prefix + "Database connection closed!");
    }

    public Connection getConnection(){
        try {
            if(connection==null || connection.isClosed())
                connect(this.databaseName, this.tableName, this.userName, this.password, this.createTableQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this.connection;
    }

    @Override
    public void store(UUID uniqueId, HashMap<String, Object> data) {
        try {
            if(connection==null || connection.isClosed())
                connect(this.databaseName, this.tableName, this.userName, this.password, this.createTableQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        data.put("id", uniqueId.toString());
        String[] keys = data.keySet().toArray(new String[0]);
        Object[] values = new Object[keys.length];
        for(int i=0; i<keys.length; i++){
            values[i] = data.get(keys[i]);
        }

        QueryBuilderInterface insertQueryBuilder = QueryBuilderFactory.getInsertQueryBuilder();
        insertQueryBuilder = insertQueryBuilder.from(tableName)
                .column(keys)
                .where(new String[] {"id"});
        String query = insertQueryBuilder.getQuery();
        //Bukkit.getConsoleSender().sendMessage(query);
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            for(int i=0; i<keys.length; i++){
                statement.setObject(i+1, values[i]);
            }
            // statement.setObject(keys.length + 1, uniqueId.toString());
            statement.execute();
        } catch (SQLException e) {
            data.remove("id");
            keys = data.keySet().toArray(new String[0]);
            values = new Object[keys.length];
            for(int i=0; i<keys.length; i++){
                values[i] = data.get(keys[i]);
            }

            QueryBuilderInterface updateQueryBuilder = QueryBuilderFactory.getUpdateQueryBuilder();
            updateQueryBuilder = updateQueryBuilder.from(tableName).set(keys).where(new String[] {"id"});
            query = updateQueryBuilder.getQuery();
            //Bukkit.getConsoleSender().sendMessage(query);
            PreparedStatement statement;
            try {
                statement = connection.prepareStatement(query);
                for(int i=0; i<keys.length; i++){
                    statement.setObject(i+1, values[i]);
                }
                statement.setString(keys.length+1, uniqueId.toString());
                statement.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        closeConnection();
    }

    @Override
    public HashMap<String, Object> get(UUID uniqueId, String[] keys) {
        try {
            if(connection==null || connection.isClosed())
                connect(this.databaseName, this.tableName, this.userName, this.password, this.createTableQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        QueryBuilderInterface selectQueryBuilder = QueryBuilderFactory.getSelectQueryBuilder();
        selectQueryBuilder = selectQueryBuilder.from(tableName).where(new String[] {"id"});

        String query = selectQueryBuilder.getQuery();
        //Bukkit.getConsoleSender().sendMessage(query);
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setObject(1, uniqueId.toString());

            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                HashMap<String, Object> result = new HashMap<>();
                for(int i=0; i<keys.length; i++){
                    result.put(keys[i], resultSet.getObject(i+2));
                }
                closeConnection();
                return result;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        closeConnection();

        return null;
    }

    @Override
    public HashMap<UUID, HashMap<String, Object>> getAll() {
        try {
            if(connection==null || connection.isClosed())
                connect(this.databaseName, this.tableName, this.userName, this.password, this.createTableQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        QueryBuilderInterface selectQueryBuilder = QueryBuilderFactory.getSelectQueryBuilder();
        selectQueryBuilder = selectQueryBuilder.from(tableName);

        String query = selectQueryBuilder.getQuery();
        HashMap<UUID, HashMap<String, Object>> result = new HashMap<>();
        try {
            PreparedStatement statement = connection.prepareStatement(query);

            ResultSet resultSet = statement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int numColumn = metaData.getColumnCount();

            while(resultSet.next()){
                UUID uuid = null;
                HashMap<String, Object> obj = new HashMap<>();
                for(int i=0; i<numColumn; i++) {
                    String columnName = metaData.getColumnName(i+1);

                    //Bukkit.getConsoleSender().sendMessage("Column Name " + columnName);
                    Object data = resultSet.getObject(i+1);
                    //Bukkit.getConsoleSender().sendMessage("Data " + data);
                    if(columnName.equalsIgnoreCase("id")) {
                        uuid = UUID.fromString((String) data);
                        //Bukkit.getConsoleSender().sendMessage("UUID is " + uuid.toString());
                    }
                    obj.put(columnName, data);
                }
                if(uuid!=null)
                    result.put(uuid, obj);
            }

            closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void closeConnection() {
        if(plugin.isEnabled()) {
            if(closeConnectionTask!=null)
                closeConnectionTask.cancel();
            closeConnectionTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
                try {
                    connection.close();
                    if(PeterUtil.configManager.enableNotification)
                        Bukkit.getConsoleSender().sendMessage(prefix + plugin.getName() + "Database connection closed!");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }, 20*1800);
        }
    }

    @Override
    public void close() {
        try {
            if(closeConnectionTask!=null)
                closeConnectionTask.cancel();
            connection.close();
            if(PeterUtil.configManager.enableNotification)
                Bukkit.getConsoleSender().sendMessage(prefix + plugin.getName() + "Database connection closed!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
