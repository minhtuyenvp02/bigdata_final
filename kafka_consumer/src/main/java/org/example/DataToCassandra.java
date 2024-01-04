package org.example;


import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.*;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.example.ultils.StockCfSchema;

import java.util.UUID;
import java.util.concurrent.TimeoutException;

import static org.apache.spark.sql.functions.*;

public class DataToCassandra {
    public static void main(String[] args) throws TimeoutException, StreamingQueryException {
        DataToCassandra dataToCassandra = new DataToCassandra();
        dataToCassandra.start();
    }
    public void start() throws TimeoutException, StreamingQueryException {
        Logger.getLogger("org.apache").setLevel(Level.WARN);
        Logger.getLogger("org.apache.spark.storage").setLevel(Level.ERROR);

        SparkConf conf = new SparkConf();
        conf.setAppName("DataToEs")
//            .setMaster("spark://spark-master:7077")
                .setMaster("local[*]")
//            .set("spark.cassandra.connection.host", "cassandra-1")
                .set("spark.cassandra.connection.host", "localhost")
                .set("spark.sql.debug.maxToStringFields", "1000")
            .set("spark.cassandra.connection.port", "9042");

        SparkSession session = SparkSession.builder()
                .config(conf)
                .getOrCreate();

        session.sparkContext().setLogLevel("WARN");
        Dataset<Row> df = session.readStream()
                .format("kafka")
//                .option("kafka.bootstrap.servers", "kafka11:29092, kafka12:29092, kafka13:29092")
                .option("kafka.bootstrap.servers", "localhost:8097, localhost:8098, localhost:8099")
                .option("subscribe", "stockStreaming")
                .option("startingOffsets", "latest")
                .option("includeHeaders", "true")
                .load();

        df.printSchema();
        Schema schema = new Schema();
        StructType scm = schema.getSchema();
        Dataset<Row> stocks = df.selectExpr("CAST(value AS STRING)")
                .select(functions.from_json(functions.col("value"), scm).as("data"))
                .select("data.*");

        // Join stream-static
        //append
        Dataset<Row> stocks_df = stocks.drop("CWMaturityDate")
                .drop("CWLastTradingDate")
                .drop("CWExcersisePrice")
                .drop("CWExerciseRatio")
                .drop("sType")
                .drop("sBenefit")
                .drop("id")
                .drop("mp")
                .drop("CWUnderlying")
                .drop("CWIssuerName")
                .drop("CWType")
                .drop("fBVol")
                .drop("fBValue")
                .drop("fSVolume")
                .drop("fSValue")
                .drop("mc");
        StockCfSchema stockCfSchema = new StockCfSchema();
        session.sparkContext().setLogLevel("WARN");
        Dataset<Row> stockData = session.read()
                .format("csv")
                .option("header", "true")
                .option("multiline", true)
                .option("sep", ",")
                .schema(stockCfSchema.getStockCfSchema())
                .load("kafka_consumer/src/main/resources");
        // Join stream-static
        //append
        stocks_df=stocks_df.join(stockData, stocks_df.col("Sym").equalTo(stockData.col("Symbol")));
        stocks_df = stocks_df.withColumnRenamed("lastPrice", "last_price")
                .withColumnRenamed("lastVolume", "last_volume")
                .withColumnRenamed("changePC", "change_pc")
                .withColumnRenamed("avePrice", "ave_price")
                .withColumnRenamed("highPrice", "high_price")
                .withColumnRenamed("lowPrice", "low_price")
                .withColumnRenamed("fRoom", "f_room")
                .withColumnRenamed("CWListedShare", "cwlisted_share")
                .withColumnRenamed("crawledTime", "crawled_time")
                .withColumnRenamed("Url", "url")
                .withColumnRenamed("CenterName", "center_name")
                .drop("Symbol")
                .withColumnRenamed("TradeCenterID", "trade_center_id")
                .withColumnRenamed("ChangePrice", "change_price")
                .withColumnRenamed("VonHoa", "von_hoa")
                .withColumnRenamed("ChangeVolume", "change_volume")
                .withColumn("EPS", expr("CASE WHEN `EPS` = 'inf' OR `EPS` = '#NAME?' THEN 0 ELSE `EPS` END"))
                .withColumnRenamed("EPS","eps")
                .withColumn("PE", expr("CASE WHEN `EPS` = 'inf' OR `EPS` = '0' THEN 0 ELSE `Price` / CAST(`EPS` AS Double) END"))
                .withColumnRenamed("PE", "pe")
                .withColumnRenamed("Price", "price")
                .withColumn("UpdatedDate", current_timestamp())
                .withColumnRenamed("UpdatedDate", "updated_date")
                .withColumnRenamed("FullName", "full_name")
                .withColumnRenamed("Beta", "beta")
                .withColumnRenamed("ParentCategoryId", "parent_category_id");


        StreamingQuery query = stocks_df.writeStream()
                .format("org.apache.spark.sql.cassandra")
//                .format("console")
                .outputMode("append")
                .foreachBatch((Dataset<Row> writeDF, Long batchId) -> {
                    writeToCassandra(writeDF, session);
                })
                .start();
        query.awaitTermination();
    }
    private static void writeToCassandra(Dataset<Row> writeDF, SparkSession spark) {
        writeDF.write()
                .format("org.apache.spark.sql.cassandra")
                .mode("append")
                .option("table", "stocks")
                .option("keyspace", "stocks_exchange")
                .save();
    }
}
