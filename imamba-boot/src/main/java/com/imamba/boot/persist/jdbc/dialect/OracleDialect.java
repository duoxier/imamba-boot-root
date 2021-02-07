package com.imamba.boot.persist.jdbc.dialect;

import com.imamba.boot.common.exception.MError;
import com.imamba.boot.common.exception.MException;
import com.imamba.boot.persist.jdbc.Dialect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OracleDialect implements Dialect {
    private static final Logger logger = LoggerFactory.getLogger(OracleDialect.class);

    public OracleDialect() {
    }

    public String getPageString(String sql, Long offset, Long limit) {
        sql = sql.trim();
        boolean isForUpdate = false;
        if (sql.toLowerCase().endsWith(" for update")) {
            sql = sql.substring(0, sql.length() - 11);
            isForUpdate = true;
        }

        StringBuffer pagingSelect = new StringBuffer(sql.length() + 100);
        pagingSelect.append("select * from ( select row_.*, rownum rownum_ from ( ");
        pagingSelect.append(sql);
        pagingSelect.append(" ) row_ ) where rownum_ > " + offset + " and rownum_ <= " + (offset + limit));
        if (isForUpdate) {
            pagingSelect.append(" for update");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Generated Pager Sql is " + pagingSelect.toString().replaceAll("\r|\n", ""));
        }

        return pagingSelect.toString();
    }

    public String getLimitString(Long offset, Long limit) {
        throw new MException(MError.NOT_SUPPORT, " oracle get limit sql is not support");
    }

    public String getCountString(String sql) {
        StringBuffer countSql = new StringBuffer(sql.length() + 100);
        countSql.append("select count(1) from (").append(sql).append(") t");
        if (logger.isDebugEnabled()) {
            logger.debug("Generated Count Sql is " + countSql.toString().replaceAll("\r|\n", ""));
        }

        return countSql.toString();
    }

    public String getIdentityString() {
        throw new MException(MError.NOT_SUPPORT, " oracle identity is not support");
    }
}