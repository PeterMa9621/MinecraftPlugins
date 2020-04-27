package vipSystem.mysql;

import org.bukkit.Bukkit;

import java.sql.*;

public class Database {

    private static Database instance = null;
    private Connection connection;
    public static String databaseName = "minecraft";
    private static String userName = "root";
    private static String password = "mjy159357";

    private Database() {
        try{
            Class.forName("com.mysql.jdbc.Driver");
            connection= DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/" + Database.databaseName + "?useSSL=false", Database.userName, Database.password);

            String createTableQuery = "create table if not exists vip_system(id varchar(100), player_name varchar(100), register_date datetime, deadline_date datetime , vip_group varchar(30), is_expired tinyint, primary key (id));";
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(createTableQuery);
            connection.close();
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void connect() {
        try{
            Class.forName("com.mysql.jdbc.Driver");
            connection= DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/" + Database.databaseName + "?useSSL=false", Database.userName, Database.password);
            Bukkit.getConsoleSender().sendMessage("[VipSystem] VipSystem Database connected!");
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public static void setConnectionInfo(String databaseName, String userName, String password) {
        Database.databaseName = databaseName;
        Database.userName = userName;
        Database.password = password;
    }

    public static Database getInstance() {
        if(Database.instance == null){
            Database.instance = new Database();
        }
        return Database.instance;
    }

    public Connection getConnection(){
        try {
            if(this.connection.isClosed())
                connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this.connection;
    }
}
