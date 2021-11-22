package pl.transport.truck.db.query;

import java.util.List;

public interface StringQueryBuilder {

    StringQueryBuilder select(String col1);

    StringQueryBuilder select(String col1, String col2);

    StringQueryBuilder select(String col1, String col2, String col3);

    StringQueryBuilder select(String col1, String col2, String col3, String col4);

    StringQueryBuilder select(String col1, String col2, String col3, String col4, String col5);

    StringQueryBuilder select(String col1, String col2, String col3, String col4, String col5, String col6);

    StringQueryBuilder select(String col1, String col2, String col3, String col4, String col5, String col6, String col7);

    StringQueryBuilder select(List<String> cols);

    StringQueryBuilder selectAll();

    StringQueryBuilder from(String table);

    StringQueryBuilder from(String schema, String table);

    StringQueryBuilder from(String schema, String table, String alias);

    StringQueryBuilder leftJoin(String table);

    StringQueryBuilder on(String rawCondition);

    StringQueryBuilder where(String rawCondition);

    StringQueryBuilder and(String rawCondition);

    StringQueryBuilder or(String rawCondition);

    StringQueryBuilder insertInto(String table, List<Object> cols);

    StringQueryBuilder insertInto(String schema, String table, List<Object> cols);

    StringQueryBuilder values(Object col1);

    StringQueryBuilder values(Object col1, Object col2);

    StringQueryBuilder values(Object col1, Object col2, Object col3);

    StringQueryBuilder values(Object col1, Object col2, Object col3, Object col4);

    StringQueryBuilder values(Object col1, Object col2, Object col3, Object col4, Object col5);

    StringQueryBuilder values(Object col1, Object col2, Object col3, Object col4, Object col5, Object col6);

    StringQueryBuilder values(Object col1, Object col2, Object col3, Object col4, Object col5, Object col6, Object col7);

    StringQueryBuilder values(List<Object> cols);

    StringQueryBuilder returningAll();

    StringQueryBuilder deleteFrom(String table);

    StringQueryBuilder deleteFrom(String schema, String table);

    String build();
}
