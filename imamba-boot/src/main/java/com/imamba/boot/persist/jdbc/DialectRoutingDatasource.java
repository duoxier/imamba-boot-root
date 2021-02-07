package com.imamba.boot.persist.jdbc;


import com.imamba.boot.common.ThreadContext;
import com.imamba.boot.common.exception.MError;
import com.imamba.boot.common.exception.MException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.bind.PropertiesConfigurationFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.env.*;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.util.ClassUtils;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DialectRoutingDatasource extends AbstractRoutingDataSource implements EnvironmentAware, BeanFactoryAware {
    private static final String CONFIG_DATASOURCE_PREFIX = "spring.datasource";
    public static final String DATASOURCE_KEY = DialectRoutingDatasource.class.getName() + "_DATASOURCE_KEY";
    public static final String DATASOURCE_NAME_KEY = DialectRoutingDatasource.class.getName() + "_DATASOURCE_NAME_KEY";
    public static final String DATASOURCE_DEFAULT_KEY = DialectRoutingDatasource.class.getName() + "_DATASOURCE_DEFAULT_KEY";
    private final String[] keys;
    private final String defaultKey;
    private Environment environment;
    private BeanFactory beanFactory;
    private PropertySources propertySources;

    public DialectRoutingDatasource(String[] keys, String defaultKey) {
        this.keys = keys;
        this.defaultKey = defaultKey;
    }

    protected DataSource determineTargetDataSource() {
        DataSource dataSource = super.determineTargetDataSource();
        ThreadContext.put(DATASOURCE_KEY, dataSource);
        return super.determineTargetDataSource();
    }

    protected Object determineCurrentLookupKey() {
        Object o = ThreadContext.get(DATASOURCE_NAME_KEY) != null ? ThreadContext.get(DATASOURCE_NAME_KEY) : DATASOURCE_DEFAULT_KEY;
        return o;
    }

    private DataSource createDataSource(String key) {
        DialectDataSourceProperties properties = new DialectDataSourceProperties();
        String dataSourcePrefix = "spring.datasource." + key;
        this.bindProperties(properties, dataSourcePrefix);
        DataSource dataSource = this.createDataSource(properties);
        this.bindProperties(dataSource, dataSourcePrefix);
        return new DialectDataSource(dataSource, properties.getDialect());
    }

    private DataSource createDataSource(DialectDataSourceProperties properties) {
        if (StringUtils.isNotBlank(properties.getJndiName())) {
            JndiDataSourceLookup dataSourceLookup = new JndiDataSourceLookup();
            DataSource dataSource = dataSourceLookup.getDataSource(properties.getJndiName());
            return dataSource;
        } else {
            DataSourceBuilder factory = DataSourceBuilder.create(properties.getClassLoader()).driverClassName(properties.getDriverClassName()).url(properties.getUrl()).username(properties.getUsername()).password(properties.getPassword());
            if (properties.getType() != null) {
                factory.type(properties.getType());
            }

            return factory.build();
        }
    }

    public Dialect getDialect() {
        DataSource dataSource = this.determineTargetDataSource();
        if (!(dataSource instanceof DialectDataSource)) {
            throw new MException(MError.CONFIG_IS_NOT_CORRECT, "dataSource is not instance TSFDatasource could't get dialect");
        } else {
            String dialect = ((DialectDataSource)dataSource).getDialect();
            return DialectProvider.get(dialect);
        }
    }

    private void bindProperties(Object bean, String targetName) {
        PropertiesConfigurationFactory<Object> factory = new PropertiesConfigurationFactory(bean);
        factory.setPropertySources(this.propertySources);
        factory.setConversionService(new DefaultConversionService());
        factory.setTargetName(targetName);

        try {
            factory.bindPropertiesToTarget();
        } catch (Exception var6) {
            String targetClass = ClassUtils.getShortName(bean.getClass());
            throw new BeanCreationException("DataSource", "Could not bind properties to " + targetClass, var6);
        }
    }

    private PropertySources deducePropertySources() {
        PropertySourcesPlaceholderConfigurer configurer = this.getSinglePropertySourcesPlaceholderConfigurer();
        if (configurer != null) {
            return new DialectRoutingDatasource.FlatPropertySources(configurer.getAppliedPropertySources());
        } else if (this.environment instanceof ConfigurableEnvironment) {
            MutablePropertySources propertySources = ((ConfigurableEnvironment)this.environment).getPropertySources();
            return new DialectRoutingDatasource.FlatPropertySources(propertySources);
        } else {
            return new MutablePropertySources();
        }
    }

    private PropertySourcesPlaceholderConfigurer getSinglePropertySourcesPlaceholderConfigurer() {
        if (this.beanFactory instanceof ListableBeanFactory) {
            ListableBeanFactory listableBeanFactory = (ListableBeanFactory)this.beanFactory;
            Map<String, PropertySourcesPlaceholderConfigurer> beans = listableBeanFactory.getBeansOfType(PropertySourcesPlaceholderConfigurer.class, false, false);
            if (beans.size() == 1) {
                return (PropertySourcesPlaceholderConfigurer)beans.values().iterator().next();
            }
        }

        return null;
    }

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public void afterPropertiesSet() {
        if (this.propertySources == null) {
            this.propertySources = this.deducePropertySources();
        }

        if (this.keys != null && !ArrayUtils.isEmpty(this.keys)) {
            if (StringUtils.isBlank(this.defaultKey)) {
                throw new MException("defaultkey is null or empty");
            } else {
                Map<Object, Object> dataSources = new HashMap();
                String[] var2 = this.keys;
                int var3 = var2.length;

                for(int var4 = 0; var4 < var3; ++var4) {
                    String key = var2[var4];
                    DataSource dataSource = this.createDataSource(key);
                    dataSources.put(key, dataSource);
                }

                this.setTargetDataSources(dataSources);
                this.setDefaultTargetDataSource(dataSources.get(this.defaultKey));
                super.afterPropertiesSet();
            }
        } else {
            throw new MException("keys is null or empty");
        }
    }

    private static class FlatPropertySources implements PropertySources {
        private PropertySources propertySources;

        FlatPropertySources(PropertySources propertySources) {
            this.propertySources = propertySources;
        }

        public Iterator<PropertySource<?>> iterator() {
            MutablePropertySources result = this.getFlattened();
            return result.iterator();
        }

        public boolean contains(String name) {
            return this.get(name) != null;
        }

        public PropertySource<?> get(String name) {
            return this.getFlattened().get(name);
        }

        private MutablePropertySources getFlattened() {
            MutablePropertySources result = new MutablePropertySources();
            Iterator var2 = this.propertySources.iterator();

            while(var2.hasNext()) {
                PropertySource<?> propertySource = (PropertySource)var2.next();
                this.flattenPropertySources(propertySource, result);
            }

            return result;
        }

        private void flattenPropertySources(PropertySource<?> propertySource, MutablePropertySources result) {
            Object source = propertySource.getSource();
            if (source instanceof ConfigurableEnvironment) {
                ConfigurableEnvironment environment = (ConfigurableEnvironment)source;
                Iterator var5 = environment.getPropertySources().iterator();

                while(var5.hasNext()) {
                    PropertySource<?> childSource = (PropertySource)var5.next();
                    this.flattenPropertySources(childSource, result);
                }
            } else {
                result.addLast(propertySource);
            }

        }
    }
}

