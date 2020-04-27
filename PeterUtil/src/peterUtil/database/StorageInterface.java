package peterUtil.database;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public interface StorageInterface {
    public void store(UUID uniqueId, HashMap<String, Object> data) throws IOException;

    public HashMap<String, Object> get(UUID uniqueId, String[] keys);

    public HashMap<UUID, HashMap<String, Object>> getAll();

    public void connect(String databaseName, String tableName, String userName, String password, String createTableQuery);

    public void close();
}
