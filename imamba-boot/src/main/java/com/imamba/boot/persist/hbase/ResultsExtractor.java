package com.imamba.boot.persist.hbase;

import org.apache.hadoop.hbase.client.ResultScanner;

public interface ResultsExtractor<T> {
    T extractData(ResultScanner var1) throws Exception;
}
