package org.example.ultils;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.types.StructType;
import java.util.concurrent.TimeoutException;

public class TestCsv {
    public static void main(String[] args) throws TimeoutException, StreamingQueryException {
        Logger.getLogger("org.apache").setLevel(Level.WARN);
        Logger.getLogger("org.apache.spark.storage").setLevel(Level.ERROR);
        SparkSession session = SparkSession.builder()
                .master("local[*]")
                .appName("readCSV")
                .getOrCreate();
        StockCfSchema schema = new StockCfSchema();
        session.sparkContext().setLogLevel("WARN");
        Dataset<Row> stockData = session.readStream()
                .format("csv")
                .option("header", "true")
                .option("multiline", true)
                .option("sep", ",")
                .schema(schema.getStockCfSchema())
                .load("kafka_consumer/src/main/resources");
        StreamingQuery query = stockData.writeStream()
                .outputMode("append")
                .format("console")
                .start();
        query.awaitTermination();
    }
}
