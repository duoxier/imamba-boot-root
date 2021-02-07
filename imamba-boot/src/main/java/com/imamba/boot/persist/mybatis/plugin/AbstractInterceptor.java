package com.imamba.boot.persist.mybatis.plugin;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.MappedStatement.Builder;
import org.apache.ibatis.mapping.SqlSource;

import java.lang.reflect.Field;

public class AbstractInterceptor {
    public AbstractInterceptor() {
    }

    protected BoundSql getBoundSql(BoundSql boundSql, String sql) {
        try {
            Field field = boundSql.getClass().getDeclaredField("sql");
            field.setAccessible(true);
            field.set(boundSql, sql);
        } catch (SecurityException var5) {
            var5.printStackTrace();
        } catch (NoSuchFieldException var6) {
            var6.printStackTrace();
        } catch (IllegalArgumentException var7) {
            var7.printStackTrace();
        } catch (IllegalAccessException var8) {
            var8.printStackTrace();
        }

        return boundSql;
    }

    protected MappedStatement copyFromMappedStatement(MappedStatement ms, SqlSource sqlSource) {
        Builder builder = new Builder(ms.getConfiguration(), ms.getId(), sqlSource, ms.getSqlCommandType());
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        builder.resultMaps(ms.getResultMaps());
        builder.cache(ms.getCache());
        MappedStatement newMs = builder.build();
        return newMs;
    }

    protected class PageSqlSource implements SqlSource {
        private BoundSql boundSql;

        public PageSqlSource(BoundSql boundSql) {
            this.boundSql = boundSql;
        }

        public BoundSql getBoundSql(Object parameterObject) {
            return this.boundSql;
        }
    }
}
