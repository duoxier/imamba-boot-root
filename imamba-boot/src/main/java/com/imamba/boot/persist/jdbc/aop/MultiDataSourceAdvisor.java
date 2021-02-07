package com.imamba.boot.persist.jdbc.aop;

import com.imamba.boot.persist.jdbc.annoation.MultiDataSource;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;

public class MultiDataSourceAdvisor extends AbstractPointcutAdvisor {
    private MultiDataSourceInterceptor interceptor = new MultiDataSourceInterceptor();
    private AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();

    public MultiDataSourceAdvisor() {
    }

    public Pointcut getPointcut() {
        this.pointcut.setExpression("@annotation(" + MultiDataSource.class.getName() + ")");
        return this.pointcut;
    }

    public Advice getAdvice() {
        return this.interceptor;
    }
}

