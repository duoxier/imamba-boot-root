package com.imamba.boot.persist.hbase;

import com.imamba.boot.persist.hbase.rowkey.KerberosAuthentication;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class HBaseAccessor implements InitializingBean, DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(HBaseAccessor.class);
    private String encoding;
    private Charset charset;
    private Configuration configuration;
    private Connection connection;
    private Properties properties;
    private String quorum;
    private Integer port;

    public HBaseAccessor() {
        this.charset = HBaseUtils.getCharset(this.encoding);
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public void setZkQuorum(String quorum) {
        this.quorum = quorum;
    }

    public void setZkPort(Integer port) {
        this.port = port;
    }

    public void afterPropertiesSet() {
        this.charset = HBaseUtils.getCharset(this.encoding);
        Configuration entries = new Configuration();
        if (this.properties != null && this.properties.get("hbase-site.xml") != null) {
            logger.info("add resource " + this.properties.getProperty("hbase-site.xml"));
            entries.addResource(new Path(this.properties.getProperty("hbase-site.xml")));
        }

        this.configuration = HBaseConfiguration.create(entries);
        if (StringUtils.hasText(this.quorum)) {
            this.configuration.set("hbase.zookeeper.quorum", this.quorum.trim());
        }

        if (this.port != null) {
            this.configuration.set("hbase.zookeeper.property.clientPort", this.port.toString());
        }

        if (this.properties != null) {
            if (this.properties.get("hbase.master.keytab.file") != null && this.properties.get("authentication.interval") != null) {
                long delayTime = Long.valueOf(this.properties.get("authentication.interval").toString());
                ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
                executor.scheduleWithFixedDelay(new KerberosAuthentication(this.properties, this.configuration, this), 0L, delayTime, TimeUnit.MINUTES);
            }
        } else {
            logger.info("不进行kerberos认证");
            this.createConnection();
        }

    }

    public void createConnection() {
        try {
            this.connection = ConnectionFactory.createConnection(this.getConfiguration());
        } catch (IOException var2) {
            logger.error(var2.getMessage(), var2);
        }

    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public Charset getCharset() {
        return this.charset;
    }

    public Configuration getConfiguration() {
        return this.configuration;
    }

    public Connection getConnection() {
        if (this.connection != null && !this.connection.isClosed() && !this.connection.isAborted()) {
            return this.connection;
        } else {
            this.createConnection();
            return this.connection;
        }
    }

    public void destroy() throws Exception {
        try {
            if (this.connection != null) {
                this.connection.close();
            }
        } catch (IOException var2) {
            logger.error(var2.getMessage(), var2);
        }

    }
}