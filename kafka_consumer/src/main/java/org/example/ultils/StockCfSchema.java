package org.example.ultils;

import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import javax.annotation.Nullable;

public class StockCfSchema {
    private StructType schema;
    public StockCfSchema() {
        this.schema = DataTypes.createStructType(new StructField[]{
            DataTypes.createStructField("Url", DataTypes.StringType, true),
                DataTypes.createStructField("CenterName", DataTypes.StringType, true),
                DataTypes.createStructField("Symbol", DataTypes.StringType, true),
                DataTypes.createStructField("TradeCenterID", DataTypes.LongType, true),
                DataTypes.createStructField("ChangePrice", DataTypes.DoubleType, true),
                DataTypes.createStructField("VonHoa", DataTypes.DoubleType, true),
                DataTypes.createStructField("ChangeVolume", DataTypes.DoubleType, true),
                DataTypes.createStructField("EPS", DataTypes.StringType, true),
                DataTypes.createStructField("PE", DataTypes.DoubleType, true),
                DataTypes.createStructField("Beta", DataTypes.DoubleType, true),
                DataTypes.createStructField("Price", DataTypes.DoubleType, true),
                DataTypes.createStructField("UpdatedDate", DataTypes.StringType, true),
                DataTypes.createStructField("FullName", DataTypes.StringType, true),
                DataTypes.createStructField("ParentCategoryId", DataTypes.LongType, true)
        });
    }
    public  StructType getStockCfSchema(){
        return this.schema;
    }
}
