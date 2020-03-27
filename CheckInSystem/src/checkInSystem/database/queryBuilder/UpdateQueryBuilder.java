package checkInSystem.database.queryBuilder;

public class UpdateQueryBuilder extends QueryBuilderBase {
    private String _update = "UPDATE ";
    private String _set = "SET ";
    private String _from = "";

    public UpdateQueryBuilder set(String[] keys){
        int length = keys.length;

        if(length > 0){
            for(int i=0; i<length; i++){
                this._set += keys[i] + " = ? ";
                if(i < length - 1){
                    this._set += ", ";
                } else {
                    this._set += " ";
                }
            }
        }

        return this;
    }

    public String getQuery(){
        if(this.hasWhere)
            return this._update + this._from + this._set + this._where;
        else
            return this._update + this._from + this._set;
    }

    @Override
    public QueryBuilderBase from(String tableName) {
        this._from += tableName + " ";
        return this;
    }
}
