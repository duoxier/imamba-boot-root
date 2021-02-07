package com.imamba.boot.persist.hbase;

import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Table;
import org.springframework.dao.DataAccessException;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.Charset;

public class HBaseUtils {
    public HBaseUtils() {
    }

    public static DataAccessException convertHBaseException(Exception ex) {
        return new HBaseSystemException(ex);
    }

    public static Table getHTable(String tableName, Connection connection) {
        try {
            return connection.getTable(TableName.valueOf(tableName));
        } catch (Exception var3) {
            throw convertHBaseException(var3);
        }
    }

    static Charset getCharset(String encoding) {
        return StringUtils.hasText(encoding) ? Charset.forName(encoding) : Charset.forName("UTF-8");
    }

    public static void releaseTable(Table table) {
        try {
            doReleaseTable(table);
        } catch (IOException var2) {
            throw convertHBaseException(var2);
        }
    }

    private static void doReleaseTable(Table table) throws IOException {
        if (table != null) {
            table.close();
        }
    }
}
