package com.imamba.boot.service.interceptor;

import com.imamba.boot.common.ThreadContext;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ThreadContextInterceptor extends HandlerInterceptorAdapter {
    public ThreadContextInterceptor() {
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ThreadContext.remove();
        super.afterCompletion(request, response, handler, ex);
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String appId = request.getHeader("appId");
        ThreadContext.put("appId", appId);
        return super.preHandle(request, response, handler);
    }
}

