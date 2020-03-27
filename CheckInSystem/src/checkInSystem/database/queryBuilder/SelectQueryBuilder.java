package checkInSystem.database.queryBuilder;

public class SelectQueryBuilder extends QueryBuilderBase {
    private String _select = "SELECT * ";
    private String _orderBy = "ORDER BY ";

    private Boolean hasOrderBy = false;

    public SelectQueryBuilder orderBy(String[] info){
        this._orderBy += info[0] + " " + info[1];
        this.hasOrderBy = true;
        return this;
    }

    public String getQuery(){
        String query = this._select + this._from;
        if(this.hasWhere)
            query += this._where;

        if(this.hasOrderBy)
            query += this._orderBy;
        return query;
    }
}
