package org.example.ultils;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.settings.Settings;

import java.io.IOException;

public class Helper {
    // Create index in elasticsearch
//    public static void main(String[] args) throws IOException {
//        Helper helper = new Helper();
//        helper.createIndexInES();
//    }
    public void createIndexInES() throws IOException {
        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(new HttpHost("localhost", 9200, "http")));
        CreateIndexRequest request1 = new CreateIndexRequest("stock");
        CreateIndexRequest request2 = new CreateIndexRequest("stock_clustered");
        request1.settings(Settings.builder().put("index.number_of_shards", 1).put("index.number_of_replicas", 2));
        request2.settings(Settings.builder().put("index.number_of_shards", 1).put("index.number_of_replicas", 2));
        CreateIndexResponse createIndexResponse1 = client.indices().create(request1, RequestOptions.DEFAULT);
        CreateIndexResponse createIndexResponse2 = client.indices().create(request2, RequestOptions.DEFAULT);
        System.out.println("response id: " + createIndexResponse1.index());
        System.out.println("response id: " + createIndexResponse1.index());
    }

}
