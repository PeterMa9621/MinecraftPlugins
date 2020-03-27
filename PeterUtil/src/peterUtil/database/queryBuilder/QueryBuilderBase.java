package peterUtil.database.queryBuilder;

public class QueryBuilderBase implements QueryBuilderInterface {
    protected String _from = "FROM ";
    protected String _where = "WHERE ";

    @Override
    public SelectQueryBuilder orderBy(String[] info) {
        return null;
    }

    @Override
    public UpdateQueryBuilder set(String[] keys) {
        return null;
    }

    @Override
    public InsertQueryBuilder column(String[] keys) {
        return null;
    }

    @Override
    public String getQuery() {
        return null;
    }

    protected Boolean hasWhere = false;

    public QueryBuilderBase where(String[] keys) {
        int length = keys.length;

        if(length>0){
            for(int i=0; i<length; i++){
                this._where += keys[i] + " = ?";
                if(i < length - 1){
                    this._where += " AND ";
                } else {
                    this._where += " ";
                }
            }
            this.hasWhere = true;
        }
        return this;
    }

    public QueryBuilderBase from(String tableName) {
        this._from += tableName + " ";
        return this;
    }
}
