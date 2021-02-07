package com.imamba.boot.autoconfigure.persist.hbase;


import com.imamba.boot.autoconfigure.persist.hbase.HbaseProperties.Kerberos;
import com.imamba.boot.persist.hbase.HBaseTemplate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
@ConditionalOnClass({HBaseTemplate.class})
@EnableConfigurationProperties({HbaseProperties.class})
public class HbaseAutoConfiguration {

    @Autowired
    private HbaseProperties hbaseProperties;

    public HbaseAutoConfiguration() {
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty({"spring.datasource.hbase.zookeeper.quorum"})
    public HBaseTemplate getInstance() {
        HBaseTemplate template = new HBaseTemplate();
        if (this.hbaseProperties.getKerberos() != null) {
            Properties properties = new Properties();
            Kerberos kerberos = this.hbaseProperties.getKerberos();
            if (StringUtils.isNotBlank(kerberos.getKeyTableFile())) {
                properties.setProperty("java.security.krb5.conf", kerberos.getKrbFile());
                properties.setProperty("hbase.security.authentication", "Kerberos");
                properties.setProperty("hbase.master.kerberos.principal", kerberos.getMasterPrincipal());
                properties.setProperty("hbase.master.keytab.file", kerberos.getKeyTableFile());
                properties.setProperty("hbase.kerberos.user", kerberos.getUsername());
                properties.setProperty("authentication.interval", kerberos.getAuthenticationInterval());
                properties.setProperty("hbase-site.xml", kerberos.getHbaseSiteXmlFile());
                template.setProperties(properties);
            }
        }

        template.setEncoding("UTF-8");
        template.setZkQuorum(this.hbaseProperties.getZookeeper().getQuorum());
        return template;
    }
}
