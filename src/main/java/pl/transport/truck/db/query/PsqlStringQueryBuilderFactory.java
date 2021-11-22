package pl.transport.truck.db.query;

public class PsqlStringQueryBuilderFactory implements StringQueryBuilderFactory {

    @Override
    public StringQueryBuilder create() {
        return new PsqlStringQueryBuilder();
    }
}
