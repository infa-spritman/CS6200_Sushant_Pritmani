package hw1.elasticAPI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;

import java.util.Collections;

/**
 * Created by Sushant on 5/13/2017.
 */
public class BulkIndex {

    public static Response createIndexRequest(String json,String indexName) {

        Response indexResponse =null ;
        try {
            RestClient restClient = RestClient.builder(
                    new HttpHost("localhost", 9200, "http")).build();
            HttpEntity entity = new NStringEntity(json, ContentType.APPLICATION_JSON);

            indexResponse  = restClient.performRequest("POST",
                              "/"+indexName+"/"+indexName+"/_bulk",
                    Collections.<String, String>emptyMap(), entity);

            restClient.close();


        } catch (Exception e) {
            System.out.println("Error doing Bulk index...");
        }finally {
            return indexResponse;
        }

    }

}
