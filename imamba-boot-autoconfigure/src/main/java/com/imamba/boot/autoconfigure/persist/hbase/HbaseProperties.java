package com.imamba.boot.autoconfigure.persist.hbase;

import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotNull;

@ConfigurationProperties(
        prefix = "spring.datasource.hbase"
)
public class HbaseProperties {

    private HbaseProperties.Zookeeper zookeeper;
    private HbaseProperties.Kerberos kerberos;

    public HbaseProperties() {
    }

    public HbaseProperties.Zookeeper getZookeeper() {
        return this.zookeeper;
    }

    public void setZookeeper(HbaseProperties.Zookeeper zookeeper) {
        this.zookeeper = zookeeper;
    }

    public HbaseProperties.Kerberos getKerberos() {
        return this.kerberos;
    }

    public void setKerberos(HbaseProperties.Kerberos kerberos) {
        this.kerberos = kerberos;
    }

    public static class Kerberos {
        private String authenticationInterval;
        private String username;
        @NotNull
        private String keyTableFile;
        private String regionserverPrincipal;
        private String masterPrincipal;
        private String krbFile;
        private String hbaseSiteXmlFile;

        public Kerberos() {
        }

        public String getAuthenticationInterval() {
            return this.authenticationInterval;
        }

        public void setAuthenticationInterval(String authenticationInterval) {
            this.authenticationInterval = authenticationInterval;
        }

        public String getUsername() {
            return this.username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getKeyTableFile() {
            return this.keyTableFile;
        }

        public void setKeyTableFile(String keyTableFile) {
            this.keyTableFile = keyTableFile;
        }

        public String getRegionserverPrincipal() {
            return this.regionserverPrincipal;
        }

        public void setRegionserverPrincipal(String regionserverPrincipal) {
            this.regionserverPrincipal = regionserverPrincipal;
        }

        public String getMasterPrincipal() {
            return this.masterPrincipal;
        }

        public void setMasterPrincipal(String masterPrincipal) {
            this.masterPrincipal = masterPrincipal;
        }

        public String getKrbFile() {
            return this.krbFile;
        }

        public void setKrbFile(String krbFile) {
            this.krbFile = krbFile;
        }

        public String getHbaseSiteXmlFile() {
            return this.hbaseSiteXmlFile;
        }

        public void setHbaseSiteXmlFile(String hbaseSiteXmlFile) {
            this.hbaseSiteXmlFile = hbaseSiteXmlFile;
        }
    }

    public static class Zookeeper {
        private String quorum;

        public Zookeeper() {
        }

        public String getQuorum() {
            return this.quorum;
        }

        public void setQuorum(String quorum) {
            this.quorum = quorum;
        }
    }
}
