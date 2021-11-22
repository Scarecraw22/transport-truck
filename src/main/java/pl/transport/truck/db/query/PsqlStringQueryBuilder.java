package pl.transport.truck.db.query;

/**
 * Example of usage:
 * <code>
 * <pre>
 * stringQueryBuilder
 *      .select("column1", "column2")
 *      .from("schema", "table")
 *      .where("column1 = :columnParam1")
 *      .build()
 * </pre>
 * Code above will return:
 * <code>
 * <pre>
 * "SELECT column1, column2 FROM schema.table WHERE column1 = :columnParam1"
 * </pre>
 * </code>
 */
public class PsqlStringQueryBuilder extends AbstractStringQueryBuilder {

    private static final String RETURNING_ALL = " RETURNING *";

    @Override
    public StringQueryBuilder returningAll() {
        query.append(RETURNING_ALL);
        return this;
    }
}
