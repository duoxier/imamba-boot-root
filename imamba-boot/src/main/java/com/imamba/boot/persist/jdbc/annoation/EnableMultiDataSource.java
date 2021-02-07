package com.imamba.boot.persist.jdbc.annoation;

import com.imamba.boot.persist.jdbc.MultiDataSourceRegister;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({MultiDataSourceRegister.class})
public @interface EnableMultiDataSource {
    String[] keys();

    String defaultKey();
}

