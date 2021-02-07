package com.imamba.boot.persist.jdbc;

import org.springframework.jdbc.datasource.AbstractDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DialectDataSource extends AbstractDataSource {
    private DataSource dataSource;
    private String dialect;

    public DialectDataSource(DataSource dataSource, String dialect) {
        this.dataSource = dataSource;
        this.dialect = dialect;
    }

    public String getDialect() {
        return this.dialect;
    }

    public Connection getConnection() throws SQLException {
        return this.dataSource.getConnection();
    }

    public Connection getConnection(String username, String password) throws SQLException {
        return this.dataSource.getConnection(username, password);
    }
}