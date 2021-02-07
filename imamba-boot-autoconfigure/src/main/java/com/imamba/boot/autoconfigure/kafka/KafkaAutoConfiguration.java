package com.imamba.boot.autoconfigure.kafka;


import com.imamba.boot.autoconfigure.kafka.KafkaProperties.Consumer;
import com.imamba.boot.kafka.KafkaConsumerManager;
import com.imamba.boot.kafka.listener.AbstractListener;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import javax.annotation.PreDestroy;
import java.util.Iterator;
import java.util.Map;

/**
 * desc:
 * author:zhongjianbin
 * Date:2019/7/13 20:00
 */

@Configuration
@ConditionalOnClass({KafkaProducer.class, KafkaConsumer.class})
@ConditionalOnProperty({"spring.kafka.bootstrapServers"})
@EnableConfigurationProperties({KafkaProperties.class})
public class KafkaAutoConfiguration implements InitializingBean, ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(KafkaAutoConfiguration.class);
    private ApplicationContext context;
    private KafkaProducer producer;
    @Autowired
    private KafkaProperties properties;
    @Autowired
    private KafkaConsumerManager consumerManager;

    public KafkaAutoConfiguration() {
    }

    @Bean
    public KafkaConsumerManager consumerManager() {
        return new KafkaConsumerManager();
    }

    @Bean
    public KafkaProducer producer() {
        this.producer = new KafkaProducer(this.properties.buildProducerProperties());
        return this.producer;
    }

    @PreDestroy
    public void destory() {
        if (this.consumerManager != null) {
            this.consumerManager.shutdown();
        }

        if (this.producer != null) {
            this.producer.close();
        }

    }

    public void afterPropertiesSet() throws Exception {
        Map<String, Consumer> consumers = this.properties.getConsumers();
        if (CollectionUtils.isEmpty(consumers)) {
            logger.debug("consumers is empty");
        } else {
            Iterator var2 = consumers.keySet().iterator();

            while(var2.hasNext()) {
                String key = (String)var2.next();
                Consumer consumer = (Consumer)consumers.get(key);
                Map<String, Object> props = this.properties.buildConsumerProperties(consumer);
                AbstractListener listener = (AbstractListener)consumer.getListener().newInstance();
                this.consumerManager.createConsumer(consumer.getTaskNum(), consumer.getTopic(), props, this.context, listener);
            }

        }
    }

    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }
}
