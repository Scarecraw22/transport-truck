package pl.transport.truck.db.query;

import pl.transport.truck.utils.StringConsts;
import pl.transport.truck.utils.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Contains default implementation of {@link StringQueryBuilder} where
 * most of SQL engines should accept.
 *
 * @author Oskar Wąsikowski
 */
public abstract class AbstractStringQueryBuilder implements StringQueryBuilder {

    protected static final String SELECT = " SELECT ";
    protected static final String SELECT_ALL = " SELECT * ";
    protected static final String FROM = " FROM ";
    protected static final String WHERE = " WHERE ";
    protected static final String AND = " AND ";
    protected static final String OR = " OR ";
    protected static final String INSERT_INTO = " INSERT INTO ";
    protected static final String VALUES = " VALUES";
    protected static final String DELETE_FROM = " DELETE FROM ";
    protected static final String LEFT_JOIN = " LEFT JOIN ";
    protected static final String ON = " ON ";

    protected final StringBuilder query;

    AbstractStringQueryBuilder() {
        this.query = new StringBuilder();
    }

    @Override
    public StringQueryBuilder select(String col1) {
        return select(List.of(col1));
    }

    @Override
    public StringQueryBuilder select(String col1, String col2) {
        return select(List.of(col1, col2));
    }

    @Override
    public StringQueryBuilder select(String col1, String col2, String col3) {
        return select(List.of(col1, col2, col3));
    }

    @Override
    public StringQueryBuilder select(String col1, String col2, String col3, String col4) {
        return select(List.of(col1, col2, col3, col4));
    }

    @Override
    public StringQueryBuilder select(String col1, String col2, String col3, String col4, String col5) {
        return select(List.of(col1, col2, col3, col4, col5));
    }

    @Override
    public StringQueryBuilder select(String col1, String col2, String col3, String col4, String col5, String col6) {
        return select(List.of(col1, col2, col3, col4, col5, col6));
    }

    @Override
    public StringQueryBuilder select(String col1, String col2, String col3, String col4, String col5, String col6, String col7) {
        return select(List.of(col1, col2, col3, col4, col5, col7));
    }

    @Override
    public StringQueryBuilder select(List<String> cols) {
        if (cols == null || cols.isEmpty()) {
            throw new IllegalArgumentException("There is no columns provided. If you want to use '*' wildcard use selectAll()");
        }
        cols.forEach(Objects::requireNonNull);

        query.append(SELECT)
                .append(cols.stream().collect(Collectors.joining(StringConsts.COMMA_WITH_SPACE, StringConsts.EMPTY_STRING, StringConsts.EMPTY_STRING)));

        return this;
    }

    @Override
    public StringQueryBuilder selectAll() {
        query.append(SELECT_ALL);

        return this;
    }

    @Override
    public StringQueryBuilder from(String table) {
        return from(null, table);
    }

    @Override
    public StringQueryBuilder from(String schema, String table) {
        return from(schema, table, null);
    }

    @Override
    public StringQueryBuilder from(String schema, String table, String alias) {
        if (StringUtils.isNullOrBlank(table)) {
            throw new IllegalArgumentException("Table is null or blank");
        }
        String fullTableName = StringUtils.isNullOrBlank(schema) ? table : schema + StringConsts.DOT + table;
        fullTableName = StringUtils.isNullOrBlank(alias) ? fullTableName : fullTableName + StringConsts.SPACE + alias;

        query.append(FROM)
                .append(fullTableName);

        return this;
    }

    @Override
    public StringQueryBuilder leftJoin(String table) {
        if (StringUtils.isNullOrBlank(table)) {
            throw new IllegalArgumentException("Table is null or blank");
        }

        query.append(LEFT_JOIN)
                .append(table);
        return this;
    }

    @Override
    public StringQueryBuilder on(String rawCondition) {
        query.append(ON)
                .append(rawCondition);
        return this;
    }

    @Override
    public StringQueryBuilder where(String rawCondition) {
        Objects.requireNonNull(rawCondition);
        query.append(WHERE)
                .append(rawCondition);

        return this;
    }

    @Override
    public StringQueryBuilder and(String rawCondition) {
        Objects.requireNonNull(rawCondition);
        query.append(AND)
                .append(rawCondition);
        return this;
    }

    @Override
    public StringQueryBuilder or(String rawCondition) {
        Objects.requireNonNull(rawCondition);
        query.append(OR)
                .append(rawCondition);
        return this;
    }

    @Override
    public StringQueryBuilder insertInto(String table, List<Object> cols) {
        return insertInto(null, table, cols);
    }

    @Override
    public StringQueryBuilder insertInto(String schema, String table, List<Object> cols) {
        Objects.requireNonNull(cols);
        if (StringUtils.isNullOrBlank(table)) {
            throw new IllegalArgumentException("Table cannot be null or blank");
        }

        String fullTableName = StringUtils.isNullOrBlank(schema) ? table : schema + StringConsts.DOT + table;
        query.append(INSERT_INTO)
                .append(fullTableName);
        if (cols.isEmpty()) {
            return this;
        }

        query.append("(")
                .append(joinWithComa(cols))
                .append(")");
        return this;
    }

    @Override
    public StringQueryBuilder values(Object col1) {
        return values(List.of(col1));
    }

    @Override
    public StringQueryBuilder values(Object col1, Object col2) {
        return values(List.of(col1, col2));
    }

    @Override
    public StringQueryBuilder values(Object col1, Object col2, Object col3) {
        return values(List.of(col1, col2, col3));
    }

    @Override
    public StringQueryBuilder values(Object col1, Object col2, Object col3, Object col4) {
        return values(List.of(col1, col2, col3, col4));
    }

    @Override
    public StringQueryBuilder values(Object col1, Object col2, Object col3, Object col4, Object col5) {
        return values(List.of(col1, col2, col3, col4, col5));
    }

    @Override
    public StringQueryBuilder values(Object col1, Object col2, Object col3, Object col4, Object col5, Object col6) {
        return values(List.of(col1, col2, col3, col4, col5, col6));
    }

    @Override
    public StringQueryBuilder values(Object col1, Object col2, Object col3, Object col4, Object col5, Object col6, Object col7) {
        return values(List.of(col1, col2, col3, col4, col5, col6, col7));
    }

    @Override
    public StringQueryBuilder values(List<Object> cols) {
        if (cols == null || cols.isEmpty()) {
            throw new IllegalArgumentException("There are no columns provided");
        }

        query.append(VALUES)
                .append("(")
                .append(joinWithComa(cols))
                .append(")");
        return this;
    }

    @Override
    public StringQueryBuilder deleteFrom(String table) {
        return deleteFrom(null, table);
    }

    @Override
    public StringQueryBuilder deleteFrom(String schema, String table) {
        if (StringUtils.isNullOrBlank(table)) {
            throw new IllegalArgumentException("Table cannot be null or blank");
        }

        String fullTableName = StringUtils.isNullOrBlank(schema) ? table : schema + StringConsts.DOT + table;
        query.append(DELETE_FROM)
                .append(fullTableName);

        return this;
    }

    @Override
    public String build() {
        return query.toString()
                .trim()
                .replaceAll("\\s{2,}", StringConsts.SPACE);
    }

    private String joinWithComa(List<Object> list) {
        return list.stream().map(String::valueOf).collect(Collectors.joining(StringConsts.COMMA_WITH_SPACE));
    }
}
