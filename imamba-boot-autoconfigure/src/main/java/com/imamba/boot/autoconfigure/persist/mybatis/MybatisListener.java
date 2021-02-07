package com.imamba.boot.autoconfigure.persist.mybatis;


import com.imamba.boot.persist.mybatis.RowBoundsAdapter;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.util.ClassUtils;

public class MybatisListener implements ApplicationListener<ApplicationPreparedEvent> {
    public static final String ROWBONDS_CLASS_NAME = "org.apache.ibatis.session.RowBounds";
    private static final boolean isPersenet = ClassUtils.isPresent("org.apache.ibatis.session.RowBounds", RowBoundsAdapter.class.getClassLoader());

    public MybatisListener() {
    }

    public void onApplicationEvent(ApplicationPreparedEvent event) {
        if (isPersenet) {
            RowBoundsAdapter.getInstance().usePage();
        }

    }
}
