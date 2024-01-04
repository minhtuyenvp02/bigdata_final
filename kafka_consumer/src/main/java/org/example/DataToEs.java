package org.example;


import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.internal.config.R;
import org.apache.spark.ml.clustering.KMeans;
import org.apache.spark.ml.evaluation.ClusteringEvaluator;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.ml.param.ParamMap;
import org.apache.spark.ml.tuning.CrossValidator;
import org.apache.spark.ml.tuning.CrossValidatorModel;
import org.apache.spark.ml.tuning.ParamGridBuilder;
import org.apache.spark.sql.*;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.example.udf.UserDefineFunction;
import org.example.ultils.StockCfSchema;

import java.util.concurrent.TimeoutException;

import static org.apache.spark.sql.functions.*;

public class DataToEs {
    public static void main(String[] args) throws TimeoutException, StreamingQueryException {
        DataToEs dataToEs = new DataToEs();
        dataToEs.start();
    }
    public void start() throws TimeoutException, StreamingQueryException {
        Logger.getLogger("org.apache").setLevel(Level.WARN);
        Logger.getLogger("org.apache.spark.storage").setLevel(Level.ERROR);
        UserDefineFunction userDefineFunction = new UserDefineFunction();
        // Setup Spark config
        SparkConf conf = new SparkConf();
        conf.setAppName("DataToEs");
        conf.setMaster("spark://spark-master:7077");
//        conf.setMaster("local[*]");
        conf.set("es.nodes", "elasticsearch")
                .set("es.port", "9200")
                .set("es.index.auto.create", "yes")
                .set("es.nodes.wan.only", "true")
                .set("spark.sql.analyzer.failAmbiguousSelfJoin", "false")
                .set("es.index.read.missing.as.empty", "yes");

        //Create Spark sesion
        SparkSession session = SparkSession.builder()
                .config(conf)
                .getOrCreate();
        session.sparkContext().setLogLevel("WARN");

        // register UDF
        UDFRegistration udfRegistration = session.udf();
        udfRegistration.register("computePE", userDefineFunction.computePE, DataTypes.DoubleType);

        // read Stream message from kafka
        Dataset<Row> df = session.readStream()
                .format("kafka")
//                .option("kafka.bootstrap.servers", "localhost:8097, localhost:8098, localhost:8099")
                .option("kafka.bootstrap.servers", "kafka11:29092, kafka12:29092, kafka13:29092")
                .option("subscribe", "stockStreaming")
                .option("startingOffsets", "earliest")
                .option("includeHeaders", "true")
                .load();
        df.printSchema();

        Schema schema = new Schema();
        StructType scm = schema.getSchema();
        Dataset<Row> stocks = df.selectExpr("CAST(value AS STRING)")
                .select(from_json(col("value"), scm).as("data"))
                .select("data.*");
        // DropCol transform
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

        // read static stream from CSV file
        StockCfSchema stockCfSchema = new StockCfSchema();
        session.sparkContext().setLogLevel("WARN");
        Dataset<Row> stockData = session.read()
                .format("csv")
                .option("header", "true")
                .option("multiline", true)
                .option("sep", ",")
                .schema(stockCfSchema.getStockCfSchema())
//                .load("/kafka_consumer/src/main/resources");
                .load("data");
        // Join stream-static
        //append
        stocks_df=stocks_df.join(stockData, stocks_df.col("Sym").equalTo(stockData.col("Symbol")))
                .withColumnRenamed("Url", "Website")
                .withColumnRenamed("Center", "Sàn giao dịch")
                .withColumnRenamed("sym", "Mã cổ phiếu")
                .withColumnRenamed("ChangePrice", "Thay đổi giá so với 5 phiên trước(%)")
                .withColumnRenamed("VonHoa", "Vốn hoá thị trường")
                .withColumnRenamed("ChangeVolume","Thay đổi KLGD so với BQ KLGD 5 phiên trước(%)")
                .withColumnRenamed("Price", "Thị giá")
                .withColumnRenamed("FullName", "Tên Cty")
                .withColumnRenamed("c", "Giá trần")
                .withColumnRenamed("f", "Giá sàn")
                .withColumnRenamed("r", "Giá tham chiếu")
                .withColumnRenamed("lastPrice", "Giá khớp lệnh")
                .withColumnRenamed("lastVolume", "KLGD")
                .withColumnRenamed("lot", "Tổng khối lượng giao dịch")
                .withColumnRenamed("ot", "Tỷ lệ biến động")
                .withColumn("UpdatedDate", current_timestamp())
                .withColumn("PE", expr("CASE WHEN `EPS` = 'inf' OR `EPS` = 'nan' OR `EPS` = '0' THEN 0 ELSE `Giá tham chiếu` / CAST(`EPS` AS Double) END"))
                .drop("Symbol")
                .drop("CWListedShare")
        ;
        // Select transform
        Dataset<Row> dfForCompute = stocks_df.select(col("Mã cổ phiếu"), col("Tên Cty"),col("industry"),col("CenterName"), col("Giá khớp lệnh"), col("Tổng khối lượng giao dịch"), col("Tỷ lệ biến động"), col("Vốn hoá thị trường"), col("PE"), col("UpdatedDate"));
        //groupBy + agg
        Dataset<Row> dfCenter = dfForCompute.groupBy(col("CenterName")).agg(sum("Tổng khối lượng giao dịch"));
        Dataset<Row> dfIndustry = dfForCompute.groupBy(col("industry")).agg(sum("Tổng khối lượng giao dịch"));
        Dataset<Row> avgIndustry = dfForCompute.groupBy(col("industry")).agg(avg("Tổng khối lượng giao dịch")).orderBy(col("avg(Tổng khối lượng giao dịch)").desc());//complete mode
        //select + filter + orderBy
//        Dataset<Row> filterVonHoa = dfForCompute.select(col("Mã cổ phiếu"), col("Tên Cty"), col("CenterName"), col("Vốn hoá thị trường")).filter(expr("`Vốn hoá thị trường` > 3000.0")).orderBy(col("Vốn hoá thị trường").desc());// append
//        Dataset<Row> prepareData = stocks_df.select("Giá khớp lệnh", "Giá tham chiếu", "Giá ")
        StreamingQuery query = stocks_df.writeStream()
//                .format("console")
                .outputMode("append")
                .queryName("writing_to_es")
                .format("org.elasticsearch.spark.sql")
                .option("checkpointLocation", "/tmp/")
                .option("es.resource", "stock")
                .option("es.nodes", "elasticsearch")
                .start();
        query.awaitTermination();
    }
}
