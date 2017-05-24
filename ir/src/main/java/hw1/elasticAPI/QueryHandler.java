package hw1.elasticAPI;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.util.Pair;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.termvectors.*;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Created by Sushant on 5/17/2017.
 */
public class QueryHandler {

    public static String getDocStats(String indexName, String type, String docNo, String fieldName) {

        Response indexResponse = null;
        RestClient restClient = null;
        String responseEntity ="";
        try {
            restClient = RestClient.builder(
                    new HttpHost("localhost", 9200, "http")).build();

            String json = "{\n" +
                    "  \"fields\" : [\"" + fieldName + "\"],\n" +
                    "  \"offsets\" : true,\n" +
                    "  \"payloads\" : true,\n" +
                    "  \"positions\" : true,\n" +
                    "  \"term_statistics\" : true,\n" +
                    "  \"field_statistics\" : true\n" +
                    "}";

            HttpEntity entity = new NStringEntity(json, ContentType.APPLICATION_JSON);

            String endPoint = "/" + indexName + "/" + type + "/" + docNo + "/_termvectors";

            indexResponse = restClient.performRequest("GET", endPoint
                    ,
                    Collections.singletonMap("pretty", "true"), entity);

            responseEntity = EntityUtils.toString(indexResponse.getEntity());

        } catch (Exception e) {
            System.out.println("Error doing DocStats...");
        } finally {
            try {
                restClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseEntity;
        }

    }

    public static Map<String, Integer> getIDs(String indexName, String type) {
        Client client = null;
        Map<String, Integer> docMap = new HashMap<String, Integer>();
        try {
            client = new PreBuiltTransportClient(Settings.EMPTY)
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));

            SearchResponse scrollResp = client.prepareSearch(indexName)
                    .setTypes(type)
                    .setFetchSource(new String[]{"docno", "doclength"}, null)
                    .setScroll(new TimeValue(60000))
                    .setSize(10000)
                    .execute().actionGet();
            do {
                for (SearchHit hit : scrollResp.getHits().getHits()) {
                    Map map = hit.getSource();
                    docMap.put(map.get("docno").toString(), Integer.parseInt(map.get("doclength").toString()));

                }

                scrollResp = client.prepareSearchScroll(scrollResp.getScrollId()).setScroll(new TimeValue(60000)).execute().actionGet();
            }
            while (scrollResp.getHits().getHits().length != 0); // Zero hits mark the end of the scroll and the while loop.
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } finally {
            if (client != null)
                client.close();
        }

        return docMap;
    }

    public static double getAvgDocLength(String indexName, String type) {
        double avgDocLength = 0;
        Response avgDocResponse = null;
        RestClient restClient = null;
        try {
            restClient = RestClient.builder(
                    new HttpHost("localhost", 9200, "http")).build();

            String json = "{\n" +
                    "    \"aggs\" : {\n" +
                    "        \"avg_doc_length\" : { \"avg\" : { \"field\" : \"doclength\" } }\n" +
                    "    }\n" +
                    "}";

            HttpEntity entity = new NStringEntity(json, ContentType.APPLICATION_JSON);

            String endPoint = "/" + indexName + "/" + type + "/_search?size=0";

            avgDocResponse = restClient.performRequest("POST", endPoint
                    ,
                    Collections.singletonMap("pretty", "true"), entity);

            ObjectMapper mapper = new ObjectMapper();
            String avgDocString = EntityUtils.toString(avgDocResponse.getEntity());
            final JsonNode jsonNode = mapper.readTree(avgDocString);
            JsonNode avgNode = jsonNode.get("aggregations").get("avg_doc_length").get("value");
            avgDocLength = avgNode.asDouble();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                restClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return avgDocLength;
        }

    }


    public static Pair<Integer, Integer> getTFDF(String entity, String term) {
        int tfCount = 0, dfCount = 0;
        try {
            ObjectMapper mapper = new ObjectMapper();
            final JsonNode jsonNode = mapper.readTree(entity);
            JsonNode term_vectors,fieldInfo,fieldStats,terms;
            if(jsonNode.has("term_vectors")){
                term_vectors = jsonNode.get("term_vectors");
                if(term_vectors.has("text")){
                    fieldInfo = term_vectors.get("text");
//                    if(fieldInfo.has("field_statistics")){
//                        fieldStats = fieldInfo.get("field_statistics");
//                    }
                    if(fieldInfo.has("terms")){
                        terms = fieldInfo.get("terms");
                        if(terms.has(term)){
                            JsonNode particularTermData = terms.get(term);
                            tfCount = Integer.parseInt(particularTermData.get("term_freq").toString());
                            dfCount = Integer.parseInt(particularTermData.get("doc_freq").toString());

                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            return new Pair<Integer, Integer>(tfCount, dfCount);

        }
    }

    public static ArrayList<String> getQueryStemmed(String indexName,String currentLine) {

        Response stemResponse ;
        RestClient restClient = null;
        String responseEntity ="";
        ArrayList<String> terms = new ArrayList<>();
        try {
            restClient = RestClient.builder(
                    new HttpHost("localhost", 9200, "http")).build();

            String json = "{\n" +
                    "  \"analyzer\": \"my_english\",\n" +
                    "  \"text\": \""+currentLine+"\"\n" +
                    "}";

            HttpEntity entity = new NStringEntity(json, ContentType.APPLICATION_JSON);

            String endPoint = "/" + indexName + "/_analyze";

            stemResponse = restClient.performRequest("POST", endPoint
                    ,
                    Collections.<String, String>emptyMap(), entity);

            responseEntity = EntityUtils.toString(stemResponse.getEntity());


            ObjectMapper mapper = new ObjectMapper();
            final JsonNode jsonNode = mapper.readTree(responseEntity);
            terms = (ArrayList<String>) jsonNode.get("tokens").findValuesAsText("token");
            terms.remove(0);


        } catch (Exception e) {
            System.out.println("Error doing StemResponse...");
        } finally {
            try {
                restClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return terms;
        }



    }
    ///////////////////////////////////////////////////////////////


    public static void main(String args[]) throws UnknownHostException, ExecutionException, InterruptedException {


        Response indexResponse = null;
        RestClient restClient = null;
        String responseEntity ="";
        try {
            restClient = RestClient.builder(
                    new HttpHost("localhost", 9200, "http")).build();

            String json = "{\n" +
                    "  \"query\": {\n" +
                    "    \"function_score\": {\n" +
                    "      \"query\": {\n" +
                    "        \"match\": {\n" +
                    "          \"text\": \"cow\"\n" +
                    "        }\n" +
                    "      },\n" +
                    "      \"boost_mode\": \"replace\",\n" +
                    "      \"functions\": [\n" +
                    "        {\n" +
                    "          \"script_score\": {\n" +
                    "            \"script\": \"_index['text']['cow'].tf()\"\n" +
                    "          }\n" +
                    "        }\n" +
                    "      ]\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";

            HttpEntity entity = new NStringEntity(json, ContentType.APPLICATION_JSON);

            String endPoint = "/ap_dataset/hw1/_search";

            indexResponse = restClient.performRequest("GET", endPoint
                    ,
                    Collections.singletonMap("pretty", "true"), entity);

            responseEntity = EntityUtils.toString(indexResponse.getEntity());
            System.out.println(responseEntity);
        } catch (Exception e) {
            System.out.println("Error doing DocStats...");
        } finally {
            try {
                restClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }


}
