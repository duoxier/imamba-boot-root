package com.imamba.boot.common.util;

import com.imamba.boot.common.ThreadContext;

public class SignUtil {

    public SignUtil() {
    }

    public static String getAppId() {
        return (String) ThreadContext.get("appId");
    }
}
