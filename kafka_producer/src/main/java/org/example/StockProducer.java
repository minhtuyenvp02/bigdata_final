package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.util.concurrent.RateLimiter;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.connect.json.JsonSerializer;
import org.example.crawler.StockCrawler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class StockProducer {

    private static Logger LOGGER = LoggerFactory.getLogger("StockProducer");
    private static final long PROGRESS_REPORTING_INTERVAL = 5;
    private static final Logger log = LoggerFactory.getLogger("StockProducer");
    public static void main(String[] args) {
        final String topicName = "stockStreaming" ;
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:8097, localhost:8098, localhost:8099");
//        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka11:29092, kafka12:29092, kafka13:29092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName());
        props.put(ProducerConfig.LINGER_MS_CONFIG, "20");
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, Integer.toString(32 * 1024));
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
        final KafkaProducer<String, JsonNode> producer = new KafkaProducer<>(props);


        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(
                () -> log.info("Message published"),
                2, PROGRESS_REPORTING_INTERVAL, TimeUnit.SECONDS);
        StockCrawler stockCrawler = new StockCrawler(producer, topicName);
        stockCrawler.scheduleDataCrawling();
    }
}

