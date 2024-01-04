//package org.example.ultils;
//
//import org.apache.spark.api.java.function.MapFunction;
//import org.apache.spark.sql.Row;
//import org.example.pojo.Stock;
//
//public class StockMappper implements MapFunction<Row, Stock> {
//    private static final long serialVersionUID = -2L;
//    @Override
//    public Stock call(Row row) throws Exception {
//        Stock stock = new Stock();
//        stock.setSym(row.getAs("sym"));
//        stock.setMc(row.getAs("mc"));
//        stock.setC(row.getAs("c"));
//        stock.setF(row.getAs("f"));
//        stock.setR(row.getAs("r"));
//        stock.setLastPrice(row.getAs("lastPrice"));
//        stock.setLastVolume(row.getAs("lastVolume"));
//        stock.setLot(row.getAs("lot"));
//        stock.setOt(row.getAs("ot"));
//        stock.setChangePc(row.getAs("changePc"));
//        stock.setAvePrice(row.getAs("avePrice"));
//        stock.setHighPrice(row.getAs("highPrice"));
//        stock.setLowPrice(row.getAs("lowPrice"));
//        stock.se
//
//
//
//
//
//
//        return null;
//    }
//}
