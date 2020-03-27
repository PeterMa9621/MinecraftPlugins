package checkInSystem.database.queryBuilder;

public class QueryBuilderFactory {
    public static SelectQueryBuilder getSelectQueryBuilder() {
        return new SelectQueryBuilder();
    }

    public static UpdateQueryBuilder getUpdateQueryBuilder() {
        return new UpdateQueryBuilder();
    }

    public static InsertQueryBuilder getInsertQueryBuilder() {
        return new InsertQueryBuilder();
    }

    public static DeleteQueryBuilder getDeleteQueryBuilder() {
        return new DeleteQueryBuilder();
    }
}
