package com.imamba.boot.persist.mybatis;

import com.imamba.boot.common.exception.MException;
import org.apache.ibatis.javassist.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RowBoundsAdapter {
    public static final String PAGE_CLASS_NAME = "com.ne.boot.common.entity.Page";
    public static final String ROWBONDS_CLASS_NAME = "org.apache.ibatis.session.RowBounds";
    private static final Logger logger = LoggerFactory.getLogger(RowBoundsAdapter.class);
    private boolean used = false;
    private static final RowBoundsAdapter instance = new RowBoundsAdapter();

    private RowBoundsAdapter() {
    }

    public static RowBoundsAdapter getInstance() {
        return instance;
    }

    public synchronized void usePage() {
        try {
            if (this.used) {
                logger.debug("rowbounds has been set");
            } else {
                ClassPool pool = ClassPool.getDefault();
                ClassClassPath classPath = new ClassClassPath(this.getClass());
                pool.insertClassPath(classPath);
                CtClass cc = pool.get("com.ne.boot.common.entity.Page");
                if (cc.isFrozen()) {
                    logger.info("page is frozen");
                    cc.defrost();
                }

                cc.setSuperclass(pool.get("org.apache.ibatis.session.RowBounds"));
                cc.toClass();
                this.used = true;
            }
        } catch (NotFoundException | CannotCompileException var4) {
            throw new MException(var4);
        }
    }
}
