package com.imamba.boot.persist.hbase;

import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Scan;

import java.util.List;

public interface HBaseOperations {
    <T> T execute(String var1, TableCallback<T> var2);

    <T> T find(String var1, String var2, ResultsExtractor<T> var3);

    <T> T find(String var1, String var2, String var3, ResultsExtractor<T> var4);

    <T> T find(String var1, Scan var2, ResultsExtractor<T> var3);

    <T> List<T> find(String var1, String var2, RowMapper<T> var3);

    <T> List<T> find(String var1, String var2, String var3, RowMapper<T> var4);

    <T> List<T> find(String var1, Scan var2, RowMapper<T> var3);

    <T> T get(String var1, String var2, RowMapper<T> var3);

    <T> T get(String var1, String var2, String var3, RowMapper<T> var4);

    <T> T get(String var1, String var2, String var3, String var4, RowMapper<T> var5);

    <T> T get(String var1, Get var2, RowMapper<T> var3);

    void put(String var1, String var2, String var3, String var4, byte[] var5);

    void delete(String var1, String var2, String var3);

    void delete(String var1, String var2, String var3, String var4);
}