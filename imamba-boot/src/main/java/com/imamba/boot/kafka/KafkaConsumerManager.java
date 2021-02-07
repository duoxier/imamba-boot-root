package com.imamba.boot.kafka;

import com.imamba.boot.kafka.listener.AbstractListener;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class KafkaConsumerManager {

    private Logger logger = LoggerFactory.getLogger(KafkaConsumerManager.class);
    private ExecutorService pool = Executors.newCachedThreadPool(new KafkaConsumerManager.DefaultThreadFactory("KAFKA-CONSUMER-MANAGER"));
    private volatile boolean running = false;

    public KafkaConsumerManager() {
    }

    public void createConsumer(int taskNum, String topic, Map<String, Object> properties, ApplicationContext context, AbstractListener listener) throws Exception {
        this.running = true;

        for(int i = 0; i < taskNum; ++i) {
            KafkaConsumer consumer = new KafkaConsumer(properties);
            listener.setTopic(topic);
            listener.setConsumer(consumer);
            listener.setContext(context);
            listener.setRunning(this.running);
            listener.init();
            this.pool.submit(listener);
        }

    }

    public void shutdown() {
        this.logger.info("pre destory");
        this.pool.shutdown();
        this.running = false;
    }

    public static class DefaultThreadFactory implements ThreadFactory {
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        public DefaultThreadFactory(String name) {
            SecurityManager s = System.getSecurityManager();
            this.group = s != null ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            this.namePrefix = name + "-thread-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(this.group, r, this.namePrefix + this.threadNumber.getAndIncrement(), 0L);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }

            if (t.getPriority() != 5) {
                t.setPriority(5);
            }

            return t;
        }
    }
}
