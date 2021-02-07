package com.imamba.boot.persist.mybatis.plugin;

import com.imamba.boot.common.entity.Page;
import com.imamba.boot.persist.jdbc.Dialect;
import com.imamba.boot.persist.jdbc.DialectProvider;
import com.imamba.boot.persist.jdbc.DialectUtil;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.Set;

@Intercepts({@Signature(
        type = Executor.class,
        method = "query",
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}
)})
public class PaginationInterceptor extends AbstractInterceptor implements Interceptor {
    private final static Logger logger = LoggerFactory.getLogger(PaginationInterceptor.class);
    private String dialect = "MYSQL";

    public Object intercept(Invocation invocation) throws Throwable {

        MappedStatement mappedStatement = (MappedStatement) invocation
                .getArgs()[0];
        Object parameter = invocation.getArgs()[1];
        Object arg2 = invocation.getArgs()[2];
        Page page = null;

        if (parameter instanceof Page) {
            page = (Page) parameter;
        } else if (arg2 instanceof Page) {
            page = (Page) arg2;
        } else if (parameter instanceof MapperMethod.ParamMap) {
            //遍历parameter，寻找Page参数
            MapperMethod.ParamMap paramMap = (MapperMethod.ParamMap) parameter;
            Set<String> paramKeys = paramMap.keySet();
            for (String key : paramKeys) {
                if (paramMap.get(key) instanceof Page) {
                    page = (Page) paramMap.get(key);
                    break;
                }
            }
        }

        if (page == null) {
            return invocation.proceed();
        }

        Long offset = (page.getPageIndex() - 1) * page.getPageSize();
        Long limit = page.getPageSize();

        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        String originalSql = boundSql.getSql().trim();
        Configuration configuration = mappedStatement.getConfiguration();
        if (boundSql == null || boundSql.getSql() == null || "".equals(boundSql.getSql())) {
            return null;
        }
        DataSource dataSource = configuration.getEnvironment().getDataSource();
        Connection connection = dataSource.getConnection();

        Dialect dialect = DialectUtil.getDialect(dataSource);
        if (dialect == null) {
            dialect = DialectProvider.get(this.dialect);
        }

        String countSql = dialect.getCountString(originalSql);

        PreparedStatement countStmt = connection.prepareStatement(countSql);

        ParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, boundSql.getParameterObject(), getBoundSql(boundSql, countSql));
        parameterHandler.setParameters(countStmt);

        ResultSet rs = countStmt.executeQuery();

        if (rs.next()) {
            page.setTotalCount(rs.getLong(1));
        }
        rs.close();
        countStmt.close();
        connection.close();

        if (logger.isDebugEnabled()) {
            logger.debug("executed count sql");
        }

        String pageSql = dialect.getPageString(originalSql, offset, limit);

        MappedStatement newMs = copyFromMappedStatement(mappedStatement, new PageSqlSource(getBoundSql(boundSql, pageSql)));

        invocation.getArgs()[0] = newMs;

        return invocation.proceed();

    }

    public Object plugin(Object arg0) {
        return Plugin.wrap(arg0, this);
    }

    public void setProperties(Properties arg0) {

    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }
}