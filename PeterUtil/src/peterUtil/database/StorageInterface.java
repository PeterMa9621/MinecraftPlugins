package peterUtil.database;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public interface StorageInterface {
    public void store(UUID uniqueId, ConfigStructure configStructure) throws IOException;

    public void store(ConfigStructure configStructure) throws IOException;

    public HashMap<String, Object> get(UUID uniqueId, String[] keys);
}
