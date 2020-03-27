package peterUtil.database;

import java.util.HashMap;

public class ConfigStructure {
    private HashMap<String, Object> data;

    public ConfigStructure(HashMap<String, Object> data){
        this.data = data;
    }

    public HashMap<String, Object> getData() {
        return data;
    }
}
