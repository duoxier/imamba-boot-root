package com.imamba.boot.persist.hbase;

import org.apache.hadoop.hbase.client.*;
import org.springframework.dao.DataAccessException;
import org.springframework.util.Assert;

import java.util.List;

public class HBaseTemplate extends HBaseAccessor implements HBaseOperations {
    private boolean autoFlush = true;

    public HBaseTemplate() {
    }

    @Override
    public <T> T execute(String tableName, TableCallback<T> action) {
        Assert.notNull(action, "Callback object must not be null");
        Assert.notNull(tableName, "No table specified");

        Table table = getTable(tableName);

        try {
            T result = action.doInTable(table);
            return result;
        } catch (Throwable th) {
            if (th instanceof Error) {
                throw ((Error) th);
            }
            if (th instanceof RuntimeException) {
                throw ((RuntimeException) th);
            }
            throw convertHBaseAccessException((Exception) th);
        } finally {
            releaseTable(table);
        }
    }

    private Table getTable(String tableName) {
        return HBaseUtils.getHTable(tableName, this.getConnection());
    }

    private void releaseTable(Table table) {
        HBaseUtils.releaseTable(table);
    }

    public DataAccessException convertHBaseAccessException(Exception ex) {
        return HBaseUtils.convertHBaseException(ex);
    }

    public <T> T find(String tableName, String family, ResultsExtractor<T> action) {
        Scan scan = new Scan();
        scan.addFamily(family.getBytes(this.getCharset()));
        return this.find(tableName, scan, action);
    }

    public <T> T find(String tableName, String family, String qualifier, ResultsExtractor<T> action) {
        Scan scan = new Scan();
        scan.addColumn(family.getBytes(this.getCharset()), qualifier.getBytes(this.getCharset()));
        return this.find(tableName, scan, action);
    }

    @Override
    public <T> T find(String tableName, final Scan scan, final ResultsExtractor<T> action) {
        return execute(tableName, new TableCallback<T>() {
            @Override
            public T doInTable(Table table) throws Throwable {
                ResultScanner scanner = table.getScanner(scan);
                try {
                    return action.extractData(scanner);
                } finally {
                    scanner.close();
                }
            }
        });
    }

    public <T> List<T> find(String tableName, String family, RowMapper<T> action) {
        Scan scan = new Scan();
        scan.addFamily(family.getBytes(this.getCharset()));
        return this.find(tableName, scan, action);
    }

    public <T> List<T> find(String tableName, String family, String qualifier, RowMapper<T> action) {
        Scan scan = new Scan();
        scan.addColumn(family.getBytes(this.getCharset()), qualifier.getBytes(this.getCharset()));
        return this.find(tableName, scan, action);
    }

    public <T> List<T> find(String tableName, Scan scan, RowMapper<T> action) {
        return (List)this.find(tableName, (Scan)scan, (ResultsExtractor)(new RowMapperResultsExtractor(action)));
    }

    public <T> T get(String tableName, String rowName, RowMapper<T> mapper) {
        return this.get(tableName, rowName, (String)null, (String)null, mapper);
    }

    public <T> T get(String tableName, String rowName, String familyName, RowMapper<T> mapper) {
        return this.get(tableName, rowName, familyName, (String)null, mapper);
    }

    public <T> T get(String tableName, final String rowName, final String familyName, final String qualifier, final RowMapper<T> mapper) {
        return this.execute(tableName, new TableCallback<T>() {
            public T doInTable(Table table) throws Throwable {
                Get get = new Get(rowName.getBytes(HBaseTemplate.this.getCharset()));
                if (familyName != null) {
                    byte[] family = familyName.getBytes(HBaseTemplate.this.getCharset());
                    if (qualifier != null) {
                        get.addColumn(family, qualifier.getBytes(HBaseTemplate.this.getCharset()));
                    } else {
                        get.addFamily(family);
                    }
                }

                Result result = table.get(get);
                return mapper.mapRow(result, 0);
            }
        });
    }

    public <T> T get(String tableName, final Get get, final RowMapper<T> mapper) {
        return this.execute(tableName, new TableCallback<T>() {
            public T doInTable(Table table) throws Throwable {
                Result result = table.get(get);
                return mapper.mapRow(result, 0);
            }
        });
    }

    public void put(String tableName, final String rowName, final String familyName, final String qualifier, final byte[] value) {
        Assert.hasLength(rowName, "rowName is empty");
        Assert.hasLength(familyName, "familyName is empty");
        Assert.hasLength(qualifier, "qualifier is empty");
        Assert.notNull(value, "value is null");
        this.execute(tableName, new TableCallback<Object>() {
            public Object doInTable(Table table) throws Throwable {
                Put put = (new Put(rowName.getBytes(HBaseTemplate.this.getCharset()))).addColumn(familyName.getBytes(HBaseTemplate.this.getCharset()), qualifier.getBytes(HBaseTemplate.this.getCharset()), value);
                table.put(put);
                return null;
            }
        });
    }

    public void delete(String tableName, String rowName, String familyName) {
        this.delete(tableName, rowName, familyName, (String)null);
    }

    public void delete(String tableName, final String rowName, final String familyName, final String qualifier) {
        Assert.hasLength(rowName, "rowName is empty");
        Assert.hasLength(familyName, "family is empty");
        this.execute(tableName, new TableCallback<Object>() {
            public Object doInTable(Table table) throws Throwable {
                Delete delete = new Delete(rowName.getBytes(HBaseTemplate.this.getCharset()));
                byte[] family = familyName.getBytes(HBaseTemplate.this.getCharset());
                if (qualifier != null) {
                    delete.addColumn(family, qualifier.getBytes(HBaseTemplate.this.getCharset()));
                } else {
                    delete.addFamily(family);
                }

                table.delete(delete);
                return null;
            }
        });
    }

    public void setAutoFlush(boolean autoFlush) {
        this.autoFlush = autoFlush;
    }
}

