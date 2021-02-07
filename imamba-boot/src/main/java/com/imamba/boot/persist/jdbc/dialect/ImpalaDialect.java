package com.imamba.boot.persist.jdbc.dialect;

import com.imamba.boot.common.exception.MError;
import com.imamba.boot.common.exception.MException;
import com.imamba.boot.persist.jdbc.Dialect;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImpalaDialect implements Dialect {
    private Logger logger = LoggerFactory.getLogger(ImpalaDialect.class);

    public ImpalaDialect() {
    }

    public String getPageString(String sql, Long offset, Long limit) {
        sql = sql.trim();
        StringBuffer pagingSelect = new StringBuffer(sql.length() + 100);
        pagingSelect.append(sql);
        pagingSelect.append(" limit ").append(limit).append(" offset ").append(offset);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Generated Pager Sql is " + pagingSelect.toString().replaceAll("\r|\n", ""));
        }

        return pagingSelect.toString();
    }

    public String getLimitString(Long offset, Long limit) {
        StringBuffer limitSql = new StringBuffer();
        limitSql.append(" limit ").append(limit).append(" offset ").append(offset);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Generated Pager Sql is " + limitSql.toString().replaceAll("\r|\n", ""));
        }

        return limitSql.toString();
    }

    public String getCountString(String sql) {
        sql = StringUtils.lowerCase(sql);
        if (sql != null) {
            sql = sql.toLowerCase();
        }

        StringBuffer countSql = new StringBuffer(sql.length() + 100);
        countSql.append("select count(1) from ").append(StringUtils.substringBetween(StringUtils.lowerCase(sql), "from", "order"));
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Generated Count Sql is " + countSql.toString().replaceAll("\r|\n", ""));
        }

        return countSql.toString();
    }

    public String getIdentityString() {
        throw new MException(MError.NOT_SUPPORT, " oracle identity is not support");
    }
}
