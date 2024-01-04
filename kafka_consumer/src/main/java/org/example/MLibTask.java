package org.example;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.VoidFunction2;
import org.apache.spark.ml.clustering.KMeans;
import org.apache.spark.ml.clustering.KMeansModel;
import org.apache.spark.ml.evaluation.ClusteringEvaluator;
import org.apache.spark.ml.feature.StandardScaler;
import org.apache.spark.ml.feature.StandardScalerModel;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.ml.param.ParamMap;
import org.apache.spark.ml.tuning.CrossValidator;
import org.apache.spark.ml.tuning.CrossValidatorModel;
import org.apache.spark.ml.tuning.ParamGridBuilder;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.UDFRegistration;
import org.apache.spark.sql.sources.In;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;
import org.elasticsearch.spark.sql.api.java.JavaEsSparkSQL;
import org.example.udf.UserDefineFunction;
import org.example.ultils.StockCfSchema;

import java.util.concurrent.TimeoutException;

import static org.apache.spark.sql.functions.*;
import static org.apache.spark.sql.functions.col;

public class MLibTask {
    public static void main(String[] args) throws TimeoutException, StreamingQueryException {
        MLibTask mLibTask = new MLibTask();
        mLibTask.start();
    }
    public void start() throws TimeoutException, StreamingQueryException {
        Logger.getLogger("org.apache").setLevel(Level.WARN);
        Logger.getLogger("org.apache.spark.storage").setLevel(Level.ERROR);
        UserDefineFunction userDefineFunction = new UserDefineFunction();
        // Setup Spark config
        SparkConf conf = new SparkConf();
        conf.setAppName("DataToEs");
//        conf.setMaster("spark://spark-master:7077");
        conf.setMaster("local[*]");
        conf.set("spark.cassandra.connection.host", "localhost")
                .set("spark.sql.debug.maxToStringFields", "1000")
                .set("spark.cassandra.connection.port", "9042")
                .set("es.index.read.missing.as.empty", "yes")
                .set("es.nodes", "localhost")
                .set("es.port", "9200")
                .set("es.index.auto.create", "yes")
                .set("spark.sql.analyzer.failAmbiguousSelfJoin", "false")
                .set("es.index.read.missing.as.empty", "yes");
        //Create Spark sesion
        SparkSession session = SparkSession.builder()
                .config(conf)
                .getOrCreate();
        session.sparkContext().setLogLevel("WARN");
        // read static stream from CSV file
        StockCfSchema stockCfSchema = new StockCfSchema();
        session.sparkContext().setLogLevel("WARN");
        Dataset<Row> stockData = session.read()
                .format("org.apache.spark.sql.cassandra")
                .option("keyspace", "stocks_exchange") // Tên keyspace của Cassandra
                .option("table", "stocks") // Tên bảng của Cassandra
                .load();
        // Join stream-static
        //append

        Dataset<Row> preparedData = stockData.select("price", "c", "f", "r", "last_volume", "lot",
                "ot", "change_pc", "high_price", "low_price", "pe", "eps", "beta", "von_hoa")
                .withColumn("change_pc", col("change_pc").cast("double"))
                .withColumn("low_price", col("low_price").cast("double"))
                .withColumn("ot", col("ot").cast("double"))
                .withColumn("high_price", col("high_price").cast("double"));

        VectorAssembler assembler = new VectorAssembler();
        assembler = assembler.setInputCols(preparedData.columns())
                .setOutputCol("features");
        Dataset<Row> assambleData = assembler.transform(preparedData);
        StandardScaler scaler = new StandardScaler()
                .setInputCol("features")
                .setOutputCol("scaled_features");
        StandardScalerModel scalerModel = scaler.fit(assambleData);
        Dataset<Row> scaledData = scalerModel.transform(assambleData);

//        Dataset<Row> trainingData = assembler.transform(preparedData).select("features");
//        kMeans.setFeaturesCol("features");
        ClusteringEvaluator evaluator = new ClusteringEvaluator()
                .setPredictionCol("prediction")
                .setFeaturesCol("scaled_features")
                .setMetricName("silhouette")
                .setDistanceMeasure("squaredEuclidean");
        Double maxScore = 0.0;
        Integer finalK = 2;
        for(int i=2; i<=8;i++){
            KMeans kMeans1 = new KMeans()
                    .setFeaturesCol("scaled_features")
                    .setK(i);
            KMeansModel kMeansModel = kMeans1.fit(scaledData);
            Dataset<Row> output = kMeansModel.transform(scaledData);
            Double score = evaluator.evaluate(output);
            if(score > maxScore){
                maxScore = score;
                finalK = i;
            }
        }
        KMeans kMeans = new KMeans()
                .setFeaturesCol("scaled_features")
                .setPredictionCol("cluster")
                .setK(finalK);

// Huấn luyện mô hình
        KMeansModel kMeansModel = kMeans.fit(scaledData);

// Áp dụng mô hình cho dữ liệu
        ClusteringEvaluator evaluator_2 = new ClusteringEvaluator()
                .setPredictionCol("cluster")
                .setFeaturesCol("scaled_features")
                .setMetricName("silhouette")
                .setDistanceMeasure("squaredEuclidean");
        Dataset<Row> clusteredData = kMeansModel.transform(scaledData);
        clusteredData.show();

        double wssse = evaluator_2.evaluate(clusteredData);
        JavaEsSparkSQL.saveToEs(clusteredData, "stock_clustered");

        System.out.println(String.format("WSSSE : %f", wssse));

    }
}
