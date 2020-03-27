package checkInSystem.database.queryBuilder;

public class InsertQueryBuilder extends QueryBuilderBase {
    private String _insert = "INSERT INTO ";
    private String _column = "(";
    private String _from = "";
    private String _values = "VALUES (";

    public InsertQueryBuilder column(String[] keys){
        int length = keys.length;

        if(length > 0){
            for(int i=0; i<length; i++){
                this._column += keys[i];
                this._values += "?";
                if(i < length - 1){
                    this._column += ", ";
                    this._values += ", ";
                } else {
                    this._column += ") ";
                    this._values += ") ";
                }
            }
        }

        return this;
    }

    public String getQuery(){
        return this._insert + this._from + this._column + this._values;
    }

    @Override
    public QueryBuilderBase from(String tableName) {
        this._from += tableName + " ";
        return this;
    }
}
