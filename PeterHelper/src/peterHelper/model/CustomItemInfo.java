package peterHelper.model;

public class CustomItemInfo {
    private String namespace;
    private String id;
    private int levelRequired;
    private SuiteInfo suiteInfo;

    public CustomItemInfo(String namespace, String id, int levelRequired) {
        this.namespace = namespace;
        this.id  = id;
        this.levelRequired = levelRequired;
    }

    public int getLevelRequired() {
        return levelRequired;
    }

    public String getId() {
        return id;
    }

    public String getNamespace() {
        return namespace;
    }

    public boolean hasSuiteInfo() {
        return suiteInfo!=null;
    }

    public SuiteInfo getSuiteInfo() {
        return  suiteInfo;
    }

    public void setSuiteInfo(SuiteInfo suiteInfo) {
        this.suiteInfo = suiteInfo;
    }
}
