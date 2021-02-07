package com.imamba.boot.persist.jdbc;

import javax.sql.DataSource;

public class DialectUtil {
    public DialectUtil() {
    }

    public static Dialect getDialect() {
        DataSource dataSource = DataSourceUtil.getCurrentDataSource();
        return getDialect(dataSource);
    }

    public static Dialect getDialect(DataSource dataSource) {
        if (dataSource instanceof DialectRoutingDatasource) {
            return ((DialectRoutingDatasource)dataSource).getDialect();
        } else if (dataSource instanceof DialectDataSource) {
            String dialect = ((DialectDataSource)dataSource).getDialect();
            return DialectProvider.get(dialect);
        } else {
            return null;
        }
    }
}