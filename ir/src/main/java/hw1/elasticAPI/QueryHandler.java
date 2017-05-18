package hw1.elasticAPI;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;

import java.io.IOException;
import java.util.Collections;

/**
 * Created by Sushant on 5/17/2017.
 */
public class QueryHandler {

    public static Response getDocStats(String indexName, String type, String docNo,String fieldName) {

        Response indexResponse =null ;
        RestClient restClient = null;
        try {
             restClient = RestClient.builder(
                    new HttpHost("localhost", 9200, "http")).build();

            String json = "{\n" +
                    "  \"fields\" : [\"text\"],\n" +
                    "  \"offsets\" : true,\n" +
                    "  \"payloads\" : true,\n" +
                    "  \"positions\" : true,\n" +
                    "  \"term_statistics\" : true,\n" +
                    "  \"field_statistics\" : true\n" +
                    "}";

            HttpEntity entity = new NStringEntity(json, ContentType.APPLICATION_JSON);

            String endPoint = "/"+indexName+"/"+type+"/"+docNo+"/_termvectors";

            indexResponse  = restClient.performRequest("GET", endPoint
                    ,
                    Collections.singletonMap("pretty", "true"), entity);




        } catch (Exception e) {
            System.out.println("Error doing Bulk index...");
        }finally {
            try {
                restClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return indexResponse;
        }

    }

    public static void main(String args[]){

        Response response = QueryHandler.getDocStats("ap_dataset","hw1","AP890101-0060","text");
        try {
            ObjectMapper mapper =  new ObjectMapper();
            String entity = EntityUtils.toString(response.getEntity());
            final JsonNode jsonNode = mapper.readTree(entity);
            JsonNode term_vectors = jsonNode.get("term_vectors");
            JsonNode fieldInfo = term_vectors.get("text");
            JsonNode fieldStats = fieldInfo.get("field_stastics");
            JsonNode terms = fieldInfo.get("terms");
            JsonNode particularTerm = terms.get("appear");
            System.out.println(particularTerm);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
