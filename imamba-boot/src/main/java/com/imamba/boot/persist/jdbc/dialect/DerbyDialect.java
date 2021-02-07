package com.imamba.boot.persist.jdbc.dialect;

import com.imamba.boot.persist.jdbc.Dialect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DerbyDialect implements Dialect {
    private static final Logger logger = LoggerFactory.getLogger(DerbyDialect.class);

    public DerbyDialect() {
    }

    public String getPageString(String sql, Long offset, Long limit) {
        sql = sql.trim();
        StringBuffer pagingSelect = new StringBuffer(sql.length() + 100);
        pagingSelect.append(sql);
        pagingSelect.append(" offset ").append(offset).append(" rows fetch next ").append(limit).append(" rows only ");
        if (logger.isDebugEnabled()) {
            logger.debug("Generated Pager Sql is " + pagingSelect.toString().replaceAll("\r|\n", ""));
        }

        return pagingSelect.toString();
    }

    public String getLimitString(Long offset, Long limit) {
        StringBuffer limitSql = new StringBuffer();
        limitSql.append(" offset ").append(offset).append(" rows fetch next ").append(limit).append(" rows only ");
        if (logger.isDebugEnabled()) {
            logger.debug("Generated Limit Sql is " + limitSql.toString().replaceAll("\r|\n", ""));
        }

        return limitSql.toString();
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
        return " values identity_val_local() ";
    }
}
