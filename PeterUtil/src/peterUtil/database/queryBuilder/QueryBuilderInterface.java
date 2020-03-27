package peterUtil.database.queryBuilder;

public interface QueryBuilderInterface {
    public String getQuery();
    public QueryBuilderInterface column(String[] keys);
    public QueryBuilderInterface where(String[] keys);
    public QueryBuilderInterface from(String tableName);
    public QueryBuilderInterface orderBy(String[] info);
    public QueryBuilderInterface set(String[] keys);
}
