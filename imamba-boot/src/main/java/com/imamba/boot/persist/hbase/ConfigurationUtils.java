package com.imamba.boot.persist.hbase;

import org.apache.hadoop.conf.Configuration;
import org.springframework.util.Assert;

import java.util.Enumeration;
import java.util.Properties;

public abstract class ConfigurationUtils {
    public ConfigurationUtils() {
    }

    public static void addProperties(Configuration configuration, Properties properties) {
        Assert.notNull(configuration, "A non-null configuration is required");
        if (properties != null) {
            Enumeration props = properties.propertyNames();

            while(props.hasMoreElements()) {
                String key = props.nextElement().toString();
                configuration.set(key, properties.getProperty(key));
            }
        }

    }
}
