package com.imamba.boot.kafka.listener;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.Iterator;

public abstract class AbstractListener<K, V> implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(AbstractListener.class);
    private String topic;
    private KafkaConsumer consumer;
    protected ApplicationContext context;
    protected Boolean running = false;

    public AbstractListener() {
    }

    public void run() {
        try {
            this.consumer.subscribe(Arrays.asList(this.topic));

            while(this.running) {
                try {
                    ConsumerRecords<K, V> records = this.consumer.poll(5000L);
                    if (records != null && !records.isEmpty()) {
                        Iterator iterator = records.iterator();

                        while(this.running && iterator.hasNext()) {
                            ConsumerRecord<K, V> record = (ConsumerRecord)iterator.next();
                            V v = record.value();
                            this.listen(v);
                        }
                    }
                } catch (Throwable var9) {
                    logger.error("listen error", var9);
                }
            }
        } catch (Throwable var10) {
            logger.error("listen error", var10);
        } finally {
            this.consumer.close();
        }

    }

    public void init() {
    }

    protected abstract void listen(V var1) throws Exception;

    public String getTopic() {
        return this.topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public KafkaConsumer getConsumer() {
        return this.consumer;
    }

    public void setConsumer(KafkaConsumer consumer) {
        this.consumer = consumer;
    }

    public void setRunning(Boolean running) {
        this.running = running;
    }

    public void setContext(ApplicationContext context) {
        this.context = context;
    }
}
