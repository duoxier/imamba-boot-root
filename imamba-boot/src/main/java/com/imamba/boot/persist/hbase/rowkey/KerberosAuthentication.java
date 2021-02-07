package com.imamba.boot.persist.hbase.rowkey;

import com.imamba.boot.persist.hbase.HBaseAccessor;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Properties;

public class KerberosAuthentication implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(KerberosAuthentication.class);
    private Properties properties;
    private Configuration configuration;
    private HBaseAccessor hBaseAccessor;

    public KerberosAuthentication(Properties properties, Configuration configuration, HBaseAccessor hBaseAccessor) {
        this.properties = properties;
        this.configuration = configuration;
        this.hBaseAccessor = hBaseAccessor;
    }

    public void run() {
        try {
            logger.info((new Date()).toString() + "开始kerberox认证");
            this.configuration.set("hadoop.security.authentication", "Kerberos");
            System.setProperty("java.security.krb5.conf", this.properties.get("java.security.krb5.conf").toString());
            UserGroupInformation.setConfiguration(this.configuration);
            UserGroupInformation userGroupInformation = UserGroupInformation.loginUserFromKeytabAndReturnUGI(this.properties.get("hbase.kerberos.user").toString(), this.properties.get("hbase.master.keytab.file").toString());
            UserGroupInformation.setLoginUser(userGroupInformation);
            logger.info("before createConnection");
            this.hBaseAccessor.createConnection();
            logger.info("kerberox认证结束");
        } catch (Exception var2) {
            var2.printStackTrace();
        }

    }
}