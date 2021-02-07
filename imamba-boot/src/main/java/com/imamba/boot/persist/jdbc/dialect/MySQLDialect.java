package com.imamba.boot.persist.jdbc.dialect;

import com.imamba.boot.persist.jdbc.Dialect;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MySQLDialect implements Dialect {
    private static final Logger logger = LoggerFactory.getLogger(MySQLDialect.class);
    public static final int INDEX_NOT_FOUND = -1;
    private static final String COUNT_SQL_PREFIX = "-- COUNT_START";
    private static final String COUNT_SQL_SUFFIX = "-- COUNT_END";

    public MySQLDialect() {
    }

    public String getPageString(String sql, Long offset, Long limit) {
        sql = sql.trim();
        if (StringUtils.indexOfIgnoreCase(sql, "-- COUNT_START") > 0) {
            StringUtils.remove(sql, "-- COUNT_START");
        }

        if (StringUtils.indexOfIgnoreCase(sql, "-- COUNT_END") > 0) {
            StringUtils.remove(sql, "-- COUNT_END");
        }

        StringBuffer pagingSelect = new StringBuffer(sql.length() + 100);
        pagingSelect.append(sql);
        if (offset > 0L) {
            pagingSelect.append(" limit ").append(offset).append(',').append(limit);
        } else {
            pagingSelect.append(" limit ").append(limit);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Generated Pager Sql is " + pagingSelect.toString().replaceAll("\r|\n", ""));
        }

        return pagingSelect.toString();
    }

    public String getLimitString(Long offset, Long limit) {
        StringBuffer limitSql = new StringBuffer();
        limitSql.append(" limit ").append(offset).append(" , ").append(limit);
        if (logger.isDebugEnabled()) {
            logger.debug("Generated Pager Sql is " + limitSql.toString().replaceAll("\r|\n", ""));
        }

        return limitSql.toString();
    }

    public String getCountString(String sql) {
        StringBuffer countSql = new StringBuffer(sql.length() + 100);
        String prefix = "from";
        String suffix = "order by";
        if (StringUtils.indexOfIgnoreCase(sql, "-- COUNT_START") > 0 && StringUtils.indexOfIgnoreCase(sql, "-- COUNT_END") > 0) {
            prefix = "-- COUNT_START";
            suffix = "-- COUNT_END";
        }

        String subSql = substringBetween(sql, prefix, suffix);
        if (StringUtils.isBlank(subSql)) {
            countSql.append("select count(1) from (").append(sql).append(") t");
        } else {
            countSql.append("select count(1) from ").append(subSql);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Generated Count Sql is " + countSql.toString().replaceAll("\r|\n", ""));
        }

        return countSql.toString();
    }

    public String getIdentityString() {
        return " select last_insert_id() ";
    }

    public static String substringBetween(String str, String open, String close) {
        if (str != null && open != null && close != null) {
            int start = StringUtils.indexOfIgnoreCase(str, open);
            if (start != -1) {
                int end = StringUtils.indexOfIgnoreCase(str, close, start + open.length());
                return end != -1 ? str.substring(start + open.length(), end) : str.substring(start + open.length(), str.length());
            } else {
                return str;
            }
        } else {
            return null;
        }
    }
}