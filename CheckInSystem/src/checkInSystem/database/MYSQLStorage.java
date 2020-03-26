package checkInSystem.database;

import com.healthmarketscience.sqlbuilder.InsertQuery;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.HashMap;
import java.util.UUID;

public class MYSQLStorage implements StorageInterface{
    private Connection connection;

    @Override
    public HashMap<String, Object> get(UUID uniqueId, String[] keys) {
        return null;
    }

    public MYSQLStorage(JavaPlugin plugin, String tableName, String databaseName, String userName, String password, String createTableQuery){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            connection= DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/" + databaseName + "?useSSL=false", userName, password);


            Statement stmt = connection.createStatement();
            stmt.executeUpdate(createTableQuery);
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public Connection getConnection(){
        return this.connection;
    }

    @Override
    public void store(UUID uniqueId, ConfigStructure configStructure) throws IOException {
        HashMap<String, Object> data = configStructure.getData();

        //String insertQuery = new InsertQuery("");

    }

    @Override
    public void store(ConfigStructure configStructure) throws IOException {
        HashMap<String, Object> data = configStructure.getData();

        //String insertQuery = new InsertQuery("")

    }
}
