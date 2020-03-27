package checkInSystem.database.queryBuilder;

public class DeleteQueryBuilder extends QueryBuilderBase {
    private String _delete = "DELETE ";

    public String getQuery(){
        if(this.hasWhere)
            return this._delete + this._from + this._where;
        else
            return this._delete + this._from;
    }
}
