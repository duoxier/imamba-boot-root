package com.imamba.boot.persist.jdbc;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

public class DialectDataSourceProperties extends DataSourceProperties {
    private String dialect;

    public DialectDataSourceProperties() {
    }

    public String getDialect() {
        return this.dialect;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }
}
