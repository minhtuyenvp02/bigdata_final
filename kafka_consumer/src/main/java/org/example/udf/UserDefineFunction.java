package org.example.udf;

import org.apache.spark.sql.api.java.UDF0;
import org.apache.spark.sql.api.java.UDF1;
import org.apache.spark.sql.api.java.UDF2;

public class UserDefineFunction {
    public UDF2<Double, Double, Double> computePE = new UDF2<Double, Double, Double>() {
        @Override
        public Double call(Double col1, Double col2) throws Exception {
            return col1 / col2;
        }
    };
}
