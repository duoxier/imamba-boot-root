package com.imamba.boot.persist.hbase;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class RowMapperResultsExtractor<T> implements ResultsExtractor<List<T>> {
    private final RowMapper<T> rowMapper;

    public RowMapperResultsExtractor(RowMapper<T> rowMapper) {
        Assert.notNull(rowMapper, "RowMapper is required");
        this.rowMapper = rowMapper;
    }

    public List<T> extractData(ResultScanner results) throws Exception {
        List<T> rs = new ArrayList();
        int rowNum = 0;
        Iterator var4 = results.iterator();

        while(var4.hasNext()) {
            Result result = (Result)var4.next();
            rs.add(this.rowMapper.mapRow(result, rowNum++));
        }

        return rs;
    }
}